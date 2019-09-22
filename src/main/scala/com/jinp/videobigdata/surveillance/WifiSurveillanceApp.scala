package com.jinp.videobigdata.surveillance

import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.JSON
import com.jinp.videobigdata.entity.{TAlarmWifi, WifiData, WifiSurveillance}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import utils.{MySqlPoolUtils, PropertiesUtils}

import scala.collection.mutable

object WifiSurveillanceApp extends Logging {

  object BroadcastWrapper {

    @volatile
    var instance: Broadcast[mutable.Map[String, WifiSurveillance]] = _

    def getFromMysql: mutable.Map[String, WifiSurveillance] = {
      val surveillance = MySqlPoolUtils.getWifiSurveillance
      log.warn("名单变量更新成功： " + DateUtils.getNowTime)
      //log.warn(surveillance.toString())
      surveillance
    }

    def update(sc: SparkContext, blocking: Boolean = false): Unit = {
      if (instance != null) {
        instance.unpersist(blocking)
      }
      instance = sc.broadcast(getFromMysql)
    }

    def getInstance(sc: SparkContext): Broadcast[mutable.Map[String, WifiSurveillance]] = {
      if (instance == null) {
        synchronized {
          if (instance == null) {
            instance = sc.broadcast(getFromMysql)
          }
        }
      }
      instance
    }
  }

  def isMacInSurv(wifiData: WifiData, wifi_survs: Broadcast[mutable.Map[String, WifiSurveillance]]): mutable.ListBuffer[TAlarmWifi] = {

    val wifiSurvs = wifi_survs.value

    val list = mutable.ListBuffer[TAlarmWifi]()
    if (wifiSurvs != null) {
      wifiSurvs.foreach(
        e => {
          if (e._2.getMacAddress.equals(wifiData.getMacAddress) && e._2.getDeviceIds.contains(wifiData.getDeviceId.toString)) {
            val surId = e._2.getId
            val alarmWifi = new TAlarmWifi()
            alarmWifi.setAlarmTime(wifiData.getCaptureTime.getTime)
            alarmWifi.setVirtualid(wifiData.getCertificateCode)
            alarmWifi.setVirtualidType(wifiData.getIdentificationType)
            alarmWifi.setDeviceCode(wifiData.getPlaceCode)
            alarmWifi.setDeviceId(wifiData.getDeviceId)
            alarmWifi.setMacAddress(wifiData.getMacAddress)
            alarmWifi.setSurId(surId.toLong)
            alarmWifi.setLongitude(wifiData.getLongitude)
            alarmWifi.setLatitude(wifiData.getLatitude)
            alarmWifi.setDealDepart(e._2.getDealDepart)
            alarmWifi.setDeviceName(wifiData.getPlaceName)
            alarmWifi.setSurName(e._2.getSurName)
            alarmWifi.setPersonName(e._2.getPersonName)
            alarmWifi.setPersonId(e._2.getPersonId)
            list += alarmWifi
          }
        }
      )
    }
    list
  }

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getCanonicalName + new SimpleDateFormat("yyyyMMddHHmm").format(new Date))
      .set("spark.streaming.backpressure.enabled", "true")
      .set("spark.streaming.kafka.maxRatePerPartition", "1000")
      .set("spark.driver.memoryOverhead", "4096")
      .set("spark.executor.memoryOverhead", "4096")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.yarn.driver.memoryOverhead", "3G")

    val ssc = new StreamingContext(sparkConf, Seconds(5))

    BroadcastWrapper.getInstance(ssc.sparkContext)

    ssc.sparkContext.setLogLevel("warn")

    val fromTopic = PropertiesUtils.wifiFromTopic.split(",").toSet

    import scala.collection.JavaConverters._

    val kafkaProp = PropertiesUtils.getConsumerConf.asInstanceOf[java.util.Map[String, String]].asScala

    var offsetRanges = Array.empty[OffsetRange]

    val stream = KafkaUtils.createDirectStream(ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](fromTopic, kafkaProp))

    BroadcastWrapper.getInstance(ssc.sparkContext)

    stream
      .foreachRDD {
        rdd => {
          offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
          if (!rdd.isEmpty()) {
            if ((DateUtils.getSecondTime.compareTo("00") >= 0) && (DateUtils.getSecondTime.compareTo("05") < 0)) {
              log.warn("名单变量开始更新：")
              BroadcastWrapper.update(rdd.sparkContext, blocking = true)
            }
            rdd.foreachPartition(
              p => {
                for (r <- p) {
                  val wifiData = JSON.parseObject(r.value(), classOf[WifiData])
                  val alarmWifis = isMacInSurv(wifiData, BroadcastWrapper.instance)
                  if (alarmWifis.nonEmpty) {
                    alarmWifis.foreach {
                      r => {
                        utils.KafkaUtils.sendWifiAlarm(JSON.toJSONString(r, false))
                        log.warn("catch a alarm:\t" + JSON.toJSONString(r, false))
                      }
                    }
                  }
                }
              })
          }

          var sum = 0L
          for (elem <- offsetRanges.toIterator) {
            sum += (elem.untilOffset - elem.fromOffset)
          }
          log.warn(s"check data :\t $sum")
          stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
        }
      }

    ssc.start()
    ssc.awaitTermination()
  }

  object DateUtils {
    def getSecondTime: String = {
      new SimpleDateFormat("ss").format(new Date())
    }

    def getNowTime: String = {
      new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
    }
  }

}




