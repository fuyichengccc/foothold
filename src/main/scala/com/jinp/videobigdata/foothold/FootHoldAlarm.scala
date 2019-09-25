package com.jinp.videobigdata.foothold

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.{SerializeConfig, SerializerFeature}
import com.jinp.videobigdata.entity.{VehicleData, WifiData}
import com.jinp.videobigdata.foothold.javaEntity.FootHoldJava
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object FootHoldAlarm extends Logging {

  def main(args: Array[String]): Unit = {
    val wifi = JSON.parseObject(
      """
        |{"macAddress":"A","captureTime":"2019-01-01 12:00:00"}
      """.stripMargin, classOf[WifiData])


    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("footHoldsAlarm")
      .set("spark.mongodb.output.uri", "mongodb://100.67.29.64:27017,100.67.29.65:27017,100.67.29.66:27017/whs.foothold")
    implicit val spark = SparkSession.builder().config(conf).getOrCreate()

    // 第一部分 : 聚合相关sourceValue的所有落脚点信息 : (sourceValue, 落脚点集合)
    val footholdsGroup: Map[String, Array[FootHold]] = getAllFootholds()
    var footholdsGroupBroad = spark.sparkContext.broadcast(footholdsGroup) // TODO 需要定时更新广播变量


    // 第二部分, 启动sparkStreaming, 消费kafka数据, 和当前mac地址(车牌号)的落脚点集合进行比较,
    val ssc: StreamingContext = ???
    val brokers: String = ???
    val groupId: String = ???
    val topics: Array[String] = ???
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(_.value()).foreachRDD { eachRDD =>
      // 定期更新广播变量
      if ("当前时间满足更新条件" == true) {
        if (footholdsGroupBroad != null) {
          footholdsGroupBroad.unpersist(true)
          footholdsGroupBroad = spark.sparkContext.broadcast(getAllFootholds())
        }
      }
      eachRDD.foreachPartition { part =>
        val footholds = footholdsGroupBroad.value
        part.foreach { value =>
          log.warn("START to process one message from kafka")
          try{
            // 匹配MAC地址对应的所有落脚点数据
            val wifi = JSON.parseObject(value, classOf[WifiData])
            log.warn("start to process one wifi message from kafka")
            footholds.get(wifi.getMacAddress) match {
              case Some(arr) => {
                // 判断该条wifi信息和所有落脚点信息的报警结果, 必须所有落脚点达到报警条件才报警
                val hmFormat = new SimpleDateFormat("HHmm")
                val oDate = wifi.getCaptureTime
                val callFlag: Array[Boolean] = arr.map { ft =>
                  val trigger1 = ft.footholdDeviceId == wifi.getDeviceId && !(ft.footholdStartTime.trim.toInt < hmFormat.format(oDate).toInt && hmFormat.format(oDate).toInt < ft.footholdEndTime.trim.toInt)
                  val trigger2 = ft.footholdDeviceId != wifi.getDeviceId && ft.footholdStartTime.trim.toInt < hmFormat.format(oDate).toInt && hmFormat.format(oDate).toInt < ft.footholdEndTime.trim.toInt
                  if (trigger1 || trigger2) true else false
                }
                if (!callFlag.contains(false)) {
                  // TODO 每个落脚点都达到报警条件, 选择报警
                }
              }
              case None => // 该mac地址的落脚点数据未计算
            }
          }catch {
            case e :Exception =>
              try{
                // 匹配车牌号对应的所有落脚点数据
                val vehicle = JSON.parseObject(value, classOf[VehicleData])
                log.warn("start to process one vehicle message from kafka")
                footholds.get(vehicle.getPlateNumber.toString) match {
                  case Some(arr) =>
                    // 判断该条车牌号和所有落脚点信息的报警结果, 必须所有落脚点达到报警条件才报警
                    val hmFormat = new SimpleDateFormat("HHmm")
                    val oDate = new Date(vehicle.getPassTime)
                    val callFlag: Array[Boolean] = arr.map { ft =>
                      val trigger1 = ft.footholdDeviceId == vehicle.getDeviceId && !(ft.footholdStartTime.trim.toInt < hmFormat.format(oDate).toInt && hmFormat.format(oDate).toInt < ft.footholdEndTime.trim.toInt)
                      val trigger2 = ft.footholdDeviceId != vehicle.getDeviceId && ft.footholdStartTime.trim.toInt < hmFormat.format(oDate).toInt && hmFormat.format(oDate).toInt < ft.footholdEndTime.trim.toInt
                      if (trigger1 || trigger2) true else false
                    }
                    if (!callFlag.contains(false)) {
                      // TODO 每个落脚点都达到报警条件, 选择报警
                    }
                  case None => // 该车牌号的落脚点数据未计算
                }
              } catch{
                case e : Exception => log.warn("json parse exception \t" + rec.value())
              }

          }

          log.warn("FINISHED to process one message from kafka")
        }
      }

    }

  }

  /**
    * 返回落脚点集合 :  (sourceValue, 落脚点集合)
    *
    * @return
    */
  def getAllFootholds()(implicit spark: SparkSession): Map[String, Array[FootHold]] = {
    log.warn("START to load and compute footholds forom mongodb")
    import spark.implicits._
    // 1. 要布控的所有落脚点信息 [定时更新][根据此信息条件查找所有相关的落脚点]
    val monitoredFt: Array[MonitoredFootHolds] = ??? // TODO
    val monitoredSourceValue = monitoredFt.map { item =>
      item.MonitorObject.toLowerCase match {
        case "车辆" => Some(item.plateNumber)
        case "mac" => Some(item.macAddress)
        case _ => None
      }
    }.filter(_.isDefined).map(_.get)
    // 2. 从mongo中查找所有的相关落脚点, 并汇总每个mac地址或车牌号的所有落脚点信息  [WhsFootHold样例类和Mongo中字段一一对应]
    log.warn("start to load data from mongo")
    val allFootHolds: Array[WhsFootHold] = MongoSpark.load(spark).as[WhsFootHold]
      .filter(item => monitoredSourceValue.contains(item.sourceValue)).collect()
    log.warn("start to aggregate data")
    // 3. 得到某个mac或车牌的所有落脚点 : (sourceValue, 落脚点集合])
    val allFootHoldsGroup = allFootHolds
      .groupBy(_.sourceValue)
      .map { item =>
        val sourceValue = item._1
        val fts = item._2.flatMap { whsFt => JSON.parseArray[FootHoldJava](whsFt.footholds, classOf[FootHoldJava]).toArray(Array[FootHoldJava]()) }
        // java转scala样例类
        val ftsCase = fts.map(ft => FootHold(ft.getFootholdDeviceId, ft.getFootholdPlaceName, ft.getFootholdLongitude, ft.getFootholdLatitude, ft.getFootholdTimeLong, ft.getFootholdStartTime, ft.getFootholdEndTime))
        (sourceValue, ftsCase)
      }
    //4. 执行合并逻辑 : 对于同一个人的多个落脚点信息, 合并成一条
    log.warn("start to merge data")
    val hmFormat = new SimpleDateFormat("HHmm")
    val allFootholdsFiexd = allFootHoldsGroup.map { item =>
      val footHoldsMerged = item._2
        .groupBy(_.footholdDeviceId)
        .map { fts =>
          fts._2.length match {
            case 1 => fts._2.head
            case _ =>
              val wholeStartTime = fts._2.sortBy(_.footholdStartTime.trim.toInt)(Ordering[Int]).head.footholdStartTime
              val wholeEndTime = fts._2.sortBy(_.footholdEndTime.trim.toInt)(Ordering[Int]).last.footholdEndTime
              val ftSample = fts._2.head
              FootHold(ftSample.footholdDeviceId, ftSample.footholdPlaceName, ftSample.footholdLongitude, ftSample.footholdLatitude,
                (hmFormat.parse(wholeEndTime).getTime - hmFormat.parse(wholeStartTime).getTime) / 1000 / 60,
                wholeStartTime, wholeEndTime
              )
          }
        }.toArray
      (item._1, footHoldsMerged)
    }
    log.warn("FINISHED to load and compute footholds from mongodb")
    allFootholdsFiexd
  }


}

/**
  * 布控的落脚点信息, 需要从mongo中查询并计算
  *
  * @param MonitorName
  * @param MonitorObject
  * @param plateNumber
  * @param macAddress
  * @param compareDate
  * @param monitorScope
  * @param startTime
  * @param endTime
  */
case class MonitoredFootHolds(
                               MonitorName: String,
                               MonitorObject: String,
                               plateNumber: String = "",
                               macAddress: String = "",
                               compareDate: String,
                               monitorScope: String = "",
                               startTime: String,
                               endTime: String
                             )
