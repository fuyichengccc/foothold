package com.jinp.videobigdata.foothold

import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.JSON
import com.jinp.videobigdata.entity.{VehicleData, WifiData}
import com.jinp.videobigdata.foothold.javaEntity.FootHoldJava
import com.mongodb.spark.MongoSpark
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.util.LongAccumulator
import org.joda.time.DateTime

object FootHoldAlarm extends Logging {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("footHoldsAlarm")
      .set("spark.mongodb.input.uri", "mongodb://100.67.29.64:27017,100.67.29.65:27017,100.67.29.66:27017/whs.foothold") // TODO  可以修改ssc的参数
    implicit val spark = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._
    // 第一部分 : 聚合相关sourceValue的所有落脚点信息 : (sourceValue, 落脚点集合)
    val footholdsGroup: Map[String, Array[FootHold]] = getAllFootholds()
    var footholdsGroupBroad = spark.sparkContext.broadcast(footholdsGroup)
    // 广播变量 :  用来更新布控点
    var updateBroadValue = spark.sparkContext.broadcast(new Date().getTime / 1000 / 60 / 60)


    // 第二部分, 启动sparkStreaming, 消费kafka数据, 和当前mac地址(车牌号)的落脚点集合进行比较, //TODO 修改kafka参数
    val ssc: StreamingContext = new StreamingContext(spark.sparkContext, Seconds(5))
    val brokers: String = "100.67.29.64:9092,100.67.29.65:9092,100.67.29.66:9092"
    val groupId: String = "footholds_alarm_test_01"
    val topics: Array[String] = Array[String]("dwd_wifi_whs_rinse_rt", "dwd_car_whs_rinse_rt")
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(_.value()).foreachRDD { eachRDD =>
      // 1小时更新一次广播变量
      val currentHour = new Date().getTime / 1000 / 60 / 60
      if (currentHour - updateBroadValue.value > 0) {
        log.warn("start to update footholds BroadValue")
        if (footholdsGroupBroad != null) {
          footholdsGroupBroad.unpersist(true)
        }
        footholdsGroupBroad = spark.sparkContext.broadcast(getAllFootholds())
        // 初始化updateValuebroad
        updateBroadValue.unpersist()
        updateBroadValue =spark.sparkContext.broadcast(currentHour)
      }
      eachRDD.foreachPartition { part =>
        val footholds = footholdsGroupBroad.value
        part.foreach { value =>
          val mStart = new Date().getTime
          log.warn("START to process one message from kafka")
          try {
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
                  // TODO 每个落脚点都达到报警条件, 选择报警, 发送kafka
                  val dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:SS")
                  log.warn(s"达到报警条件, 发送kafak => MAC地址 : ${wifi.getMacAddress}, 时间 : ${dateFormat.format(wifi.getCaptureTime)}, 设备id : ${wifi.getDeviceId}")
                } else {
                  log.warn("未达到报警条件")
                }
              }
              case None => // 该mac地址的落脚点数据未计算
                println("can't find footholds data about this mac")
            }
          } catch {
            case e: Exception =>
              try {
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
                      // TODO 每个落脚点都达到报警条件, 选择报警, 发送kafka
                      val dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:SS")
                      log.warn(s"达到报警条件, 发送kafka => 车牌号 : ${vehicle.getPlateNumber}, 时间 : ${dateFormat.format(new Date(vehicle.getPassTime))}, 设备id : ${vehicle.getDeviceId}, 设备名称:${vehicle.getDeviceName}")
                    } else {
                      log.warn("未达到报警条件")
                    }
                  case None => // 该车牌号的落脚点数据未计算
                    log.warn("can't find footholds data about this vehicle")
                }
              } catch {
                case e: Exception => log.warn("json parse exception \t" + value)
              }
          }
          val mEnd = new Date().getTime
          log.warn(s"FINISHED to process one message from kafka with ${mEnd - mStart} ms")
        }
      }
    }

    ssc.start()
    ssc.awaitTermination()
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
    val monitoredFt: Array[MonitoredFootHolds] = spark.read // TODO 修改mysql参数
      .format("jdbc")
      .option("url", "jdbc:mysql://192.168.15.171:3307")
      .option("query", "select * from footholdsAlarm.t_surveillance_more_foothold where current_time between start_time and end_time")
      .option("user", "root")
      .option("password", "1234")
      .load.as[MonitoredFootHolds]
      .collect()
    // 提取sourceValue为key, 构造Map对象 :  (sourceValue, 布控点)
    val monitoredSourceValue: Map[String, MonitoredFootHolds] = monitoredFt.map { item =>
      val sourceValue = item.sur_types.toLowerCase match {
        case "wifi" => Some(item.car_no)
        case "car" => Some(item.mac_address)
        case _ => None
      }
      (sourceValue, item)
    }.filter(_._1.isDefined).map(ft => (ft._1.get, ft._2)).toMap

    // 2. 从mongo中查找所有的相关落脚点, 并汇总每个mac地址或车牌号的所有落脚点信息  [WhsFootHold样例类和Mongo中字段一一对应]
    log.warn("START to load and compute data from mongo")
    val ymdFormat = new SimpleDateFormat("yyyyMMdd")
    val allFootHolds: Array[WhsFootHold] = MongoSpark.load(spark).as[WhsFootHold]
      .filter { ft =>
        // 过滤条件 :  已经布控, 并且dt在比较时段内
        monitoredSourceValue.get(ft.sourceValue) match {
          case Some(mFt) =>
            ymdFormat.format(new Date(mFt.compare_start_time.getTime)).toInt <= ft.sourceValue.trim.toInt &&
              ymdFormat.format(new Date(mFt.compare_end_time.getTime)).toInt <= ft.sourceValue.trim.toInt
          case None => false
        }
      }
      .collect()
    log.warn("start to aggregate data")

    // 3. 得到某个mac或车牌的所有落脚点 : (sourceValue, 落脚点集合])
    val allFootHoldsGroup = allFootHolds
      .groupBy(_.sourceValue)
      .map { item =>
        val sourceValue = item._1
        val fts = item._2.flatMap { whsFt => JSON.parseArray[FootHoldJava](whsFt.foothods, classOf[FootHoldJava]).toArray(Array[FootHoldJava]()) }
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
  * 布控对象, 和mysql中表t_surveillance_more_foothold对应
  *
  * @param name
  * @param sur_types   布控对象,wifi 或者 car
  * @param car_no      车牌号
  * @param mac_address mac地址
  * @param compare_start_time
  * @param compare_end_time
  * @param start_time
  * @param end_time
  */
case class MonitoredFootHolds(
                               name: String,
                               sur_types: String,
                               car_no: String = "",
                               mac_address: String = "",
                               compare_start_time: java.sql.Timestamp,
                               compare_end_time: java.sql.Timestamp,
                               start_time: java.sql.Timestamp,
                               end_time: java.sql.Timestamp
                             )
