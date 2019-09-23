package com.jinp.videobigdata.foothold

import java.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.{SerializeConfig, SerializerFeature}
import com.jinp.videobigdata.entity.{VehicleData, WifiData}
import com.jinp.videobigdata.foothold.javaEntity.FootHoldJava
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object FootHoldAlarm {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("footHoldsAlarm")
      .set("spark.mongodb.output.uri", "mongodb://100.67.29.64:27017,100.67.29.65:27017,100.67.29.66:27017/whs.foothold")
    val spark = SparkSession.builder().config(conf).getOrCreate()

    // 第一部分 : 聚合相关sourceValue的所有落脚点信息 : (sourceValue, 落脚点集合)
    val footholdsGroup: Map[String, Array[FootHold]] = getAllFootholds()
    val footholdsGroupBroad = spark.sparkContext.broadcast(footholdsGroup) // TODO 需要定时更新广播变量


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
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(_.value()).foreachRDD { eachRDD =>
      val footholds = footholdsGroupBroad.value
      eachRDD.foreachPartition { part =>
        part.foreach { value =>
          if ("消费ifi数据" == true) {
            val wifiList: util.List[WifiData] = JSON.parseArray[WifiData](value, classOf[WifiData])
            // 判断wifiList中的设备id是否在该mac地址的落脚点中
            for (i <- 0 until wifiList.size()) {
              val wifi = wifiList.get(i)
              footholds.get(wifi.getMacAddress) match {
                case Some(arr) => if (!arr.map(_.footholdDeviceId).contains(wifi.getDeviceId)) {
                  // 将wifi数据发送kafka TODO
                }
                case None => // 该mac地址的落脚点数据未计算
              }
            }
          }
          else if ("消费到车辆数据" == true) {
            val vehicleList: util.List[VehicleData] = JSON.parseArray[VehicleData](value, classOf[VehicleData])
            // 判断vehicleList中的设备id是否在该车牌号落脚点中
            for (i <- 0 until vehicleList.size() - 1) {
              val vehicle = vehicleList.get(i)
              footholds.get(vehicle.getPlateNumber.toString) match {
                case Some(arr) => if (!arr.map(_.footholdDeviceId).contains(vehicle.getDeviceId)) {
                  // 将vehicle数据发送kafka TODO
                }
                case None => // 该车牌号的落脚点数据未计算
              }
            }
          }
        }
      }

    }

  }

  /**
    * 返回落脚点集合 :  (sourceValue, 落脚点集合)
    *
    * @return
    */
  def getAllFootholds(): Map[String, Array[FootHold]] = {
    // 1. 要布控的所有落脚点信息 [定时更新][根据此信息条件查找所有相关的落脚点]
    val monitoredFt: Array[MonitoredFootHolds] = ??? // TODO
    // 2. 从mongo中查找所有的相关落脚点, 并汇总每个mac地址或车牌号的所有落脚点信息  [WhsFootHold样例类和Mongo中字段一一对应]
    val allFootHolds: Array[WhsFootHold] = ??? // TODO
    // 3. 得到某个mac或车牌的所有落脚点 : (sourceValue, 落脚点集合])
    allFootHolds
      .groupBy(_.sourceValue)
      .map { item =>
        val sourceValue = item._1
        val fts = item._2.flatMap { whsFt => JSON.parseArray[FootHoldJava](whsFt.footholds, classOf[FootHoldJava]).toArray(Array[FootHoldJava]()) }
        // java转scala样例类
        val ftsCase = fts.map(ft => FootHold(ft.getFootholdDeviceId, ft.getFootholdPlaceName, ft.getFootholdLongitude, ft.getFootholdLatitude, ft.getFootholdTimeLong, ft.getFootholdStartTime, ft.getFootholdEndTime))
        (sourceValue, ftsCase)
      }
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
