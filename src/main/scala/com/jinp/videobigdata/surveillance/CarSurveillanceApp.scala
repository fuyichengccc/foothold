package com.jinp.videobigdata.surveillance

import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.JSON
import com.jinp.videobigdata.entity.{VehicleData, _}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import utils.{MySqlPoolUtils, PropertiesUtils}

import scala.collection.mutable

object CarSurveillanceApp extends Logging {

  object BroadcastWrapper {

    @volatile
    var instance: Broadcast[mutable.Map[String, CarSurveillance]] = _

    def getFromMysql: mutable.Map[String, CarSurveillance] = {
      val surveillance = MySqlPoolUtils.getCarSurveillance
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

    def getInstance(sc: SparkContext): Broadcast[mutable.Map[String, CarSurveillance]] = {
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

  def isCarInSurv(carData: VehicleData, carSurvBroad: Broadcast[mutable.Map[String, CarSurveillance]]): mutable.ListBuffer[TAlarmCar] = {

    val carSurvs = carSurvBroad.value

    val list = mutable.ListBuffer[TAlarmCar]()
    if (carSurvs != null) {
      carSurvs.foreach(
        e => {
          if (e._2.getPlateNumber.equals(carData.getCarPlate) && e._2.getDeviceIds.contains(carData.getDeviceId.toString)) {
            val surId = e._2.getId
            val alarmCar = new TAlarmCar()
            alarmCar.setAlarmTime(System.currentTimeMillis())
            alarmCar.setPassTime(carData.getPassTime)
            alarmCar.setAlarmType(0)
            alarmCar.setRecordId(carData.getRecordID)
            alarmCar.setDeviceCode(carData.getTollgateID)
            alarmCar.setDeviceId(carData.getDeviceId)
            alarmCar.setDeviceName(carData.getDeviceName)
            alarmCar.setLatitude(carData.getLatitude)
            alarmCar.setLongitude(carData.getLongitude)
            alarmCar.setCarplate(carData.getCarPlate)
            alarmCar.setSurId(surId.toLong)
            alarmCar.setSurName(e._2.getSurName)
            alarmCar.setDealDepart(e._2.getDealDepart)
            alarmCar.setDirection(carData.getDirectionName)
            alarmCar.setVehicleType(carData.getVehicleType)
            alarmCar.setVehicleSpeed(carData.getVehicleSpeed)
            alarmCar.setPlatecolor(carData.getPlateColor)
            alarmCar.setPicNumber(carData.getPicNumber)
            alarmCar.setPicurl1(carData.getImageURL1)
            alarmCar.setPicurl2(carData.getImageURL2)
            alarmCar.setPicurl3(carData.getImageURL3)
            alarmCar.setPicurl4(carData.getImageURL4)
            alarmCar.setPersonName(e._2.getPersonName)
            alarmCar.setPersonId(e._2.getPersonId)
            list += alarmCar
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

    val fromTopic = PropertiesUtils.carFromTopic.split(",").toSet

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
                  val carData = JSON.parseObject(r.value(), classOf[VehicleData])
                  val alarmWifis = isCarInSurv(carData, BroadcastWrapper.instance)
                  if (alarmWifis.nonEmpty) {
                    alarmWifis.foreach {
                      r => {
                        utils.KafkaUtils.sendCarAlarm(JSON.toJSONString(r, false))
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




