package com.jinp.videobigdata.foothold

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.jinp.videobigdata.entity.{VehicleData, WifiData}
import com.mongodb.spark.MongoSpark
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}
import utils.TubaoMelkman

import scala.collection.mutable.ArrayBuffer

/**
 * sparkSQL, 从hive读取wifi和vehicle数据, 算出落脚点, 存入mongo
 */
object ComputeFootHold extends Logging {

  def main(args: Array[String]): Unit = {
    val durationTime = if (args.length > 0) args(0).trim.toInt else -1
    val dt = if (args.length > 1) args(1).trim else "20190921"
    val sc = new SparkConf().setIfMissing("spark.master", "local[*]").setAppName("footHolds_demo")
      .set("spark.mongodb.output.uri", "mongodb://100.67.29.64:27017,100.67.29.65:27017,100.67.29.66:27017/whs.foothold")
    val spark = SparkSession.builder().config(sc).enableHiveSupport().getOrCreate()

    import spark.implicits._

    //1. 从hive中读取数据, 封装成Bean对象 VehicleData 和 WifiData.  格式为RDD
    val wifiInputRDD: Dataset[caseEntity.WifiData] = spark.sql("select t1.macAddress,t1.brand,t1.captureTime,t1.deviceId,t1.strength ," +
      "t1.identificationType ,t1.certificateCode ,t1.apSsid ,t1.apMac ,t1.apChannel,t1.encryptType ," +
      "t1.xCoordinate ,t1.yCoordinate ,t1.placeCode ,t1.placeName ,t1.devNo ,t1.longitude ,t1.latitude," +
      s"dt from dwd.dwd_wifi_whs_opt as t1 where dt=$dt").as[caseEntity.WifiData]

    val vehicleInputRDD: Dataset[caseEntity.VehicleData] = spark.sql("select t1.plateNumber, t1.passTime,t1.deviceId, t1.deviceName," +
      "t1.placeName, t1.longitude,t1.latitude from dwd.dwd_car_whs_rt as t1 " +
      s"where dt=$dt").as[caseEntity.VehicleData]

    val wifiRDD: RDD[WifiData] = wifiInputRDD.rdd.map { inputWifi =>
      val w = new WifiData
      w.setMacAddress(inputWifi.macAddress)
      w.setBrand(inputWifi.brand)
      w.setCaptureTime(new Date(inputWifi.captureTime.trim.toLong))
      w.setDeviceId(inputWifi.deviceId)
      w.setDevNo(inputWifi.devNo)
      w.setPlaceCode(inputWifi.placeCode)
      w.setPlaceName(inputWifi.placeName)
      w.setLongitude(inputWifi.longitude)
      w.setLatitude(inputWifi.latitude)
      w
    }
    val vehicleRDD: RDD[VehicleData] = vehicleInputRDD.rdd.map { inputVehicle =>
      val v = new VehicleData
      v.setPlateNumber(inputVehicle.plateNumber)
      v.setPassTime(inputVehicle.passTime)
      v.setDeviceId(inputVehicle.deviceId)
      v.setDeviceName(inputVehicle.deviceName)
      v.setPlaceName(inputVehicle.placeName)
      v.setLongitude(inputVehicle.longitude)
      v.setLatitude(inputVehicle.latitude)
      v
    }

    //2. 数据转换, 将 mac和 vehicle信息, 聚合成落脚点数据, 存入mongo表 dws_whs_foothold
    // 2.1 wifi数据转换
    val wifiGroupById = wifiRDD.map(item => (item.getMacAddress, item)).groupByKey() // 按MAC地址聚合
    val wifiFormat: Dataset[WhsFootHold] = wifiGroupById.mapPartitions { part =>
      part.map { wifi =>
        val wifiArr = wifi._2.toArray
        generateDataFromWifi(wifiArr, durationTime, dt)
      }
    }.filter(_.isDefined).map(_.get).toDS()
    MongoSpark.save(wifiFormat)

    // 2.2 vehicle数据转换
    val vehicleGroupById = vehicleRDD.map(item => (item.getPlateNumber, item)).groupByKey() // 按车牌号聚合
    val vehicleFormat: Dataset[WhsFootHold] = vehicleGroupById.mapPartitions { part =>
      // 从连接池中拿到mongo连接
      part.map { vehicle =>
        val vehicleArr = vehicle._2.toArray
        generateDataFromVehicle(vehicleArr, durationTime, dt)
      }
    }.filter(_.isDefined).map(_.get).toDS()
    MongoSpark.save(vehicleFormat)

    spark.stop()
  }

  /**
   * @param wifiDataArr 输入多条mac地址相同的wifi记录, 聚合成该mac的一条落脚点相关信息, 写入mongo
   */
  def generateDataFromWifi(wifiDataArr: Array[WifiData], durationTime: Int, dt: String): Option[WhsFootHold] = {
    val wifiSorted: Array[(WifiData, Int)] = wifiDataArr.sortBy(_.getCaptureTime)(Ordering[Date]).zipWithIndex
    // 找到连续时间对应的下标  (设备ID, 下标)
    val continuousIndex: ArrayBuffer[(Int, Int)] = findContinuousIndex(wifiSorted.map(item => (item._1.getDeviceId, item._2)))

    // 计算落脚点
    val hourMinFormat = new SimpleDateFormat("HHmm")
    val footHolds = for (item <- continuousIndex) yield {
      val startWifi = wifiSorted(item._1)._1
      val endWifi = wifiSorted(item._2)._1
      val durationMin = (endWifi.getCaptureTime.getTime - startWifi.getCaptureTime.getTime) / 1000 / 60
      FootHold(startWifi.getDeviceId,
        startWifi.getPlaceName,
        startWifi.getLongitude,
        startWifi.getLatitude,
        durationMin,
        hourMinFormat.format(startWifi.getCaptureTime), hourMinFormat.format(endWifi.getCaptureTime))
    }
    // 筛选出停留时间大于5h的落脚点  比较单位 :min
    val footHoldsFixed: Array[FootHold] = footHolds.toArray.filter(_.footholdTimeLong > durationTime)

    // 计算活动范围 : 凸包函数, 所有点去重后进行计算
    log.warn("start to find tubao")
    val pointList: Array[Point] = wifiDataArr.map(item => Point(item.getLongitude, item.getLatitude, item.getDeviceId)).distinct
    log.warn(s"${pointList.length}inputPoints of TubaoModel are below")
    log.warn(pointList.map(item => s"${item.longitude}|${item.latitude}|${item.deviceId}").mkString(","))
    val findTubao_1 = new Date().getTime
    val range = getConvexHull(pointList)
    val findTubao_2 = new Date().getTime
    if (!range.equals("")) log.warn(s"find Tubao time : ${(findTubao_2 - findTubao_1) / 1000} s")

    // 开始构造WhsFootHolds, 返回结果
    val id = s"${dt}${wifiDataArr.head.getMacAddress.hashCode & Int.MaxValue}".trim // id值 = dt & mac的哈希值(正)
    val sourceType = 3
    val sourceValue = wifiDataArr.head.getMacAddress
    val personName = "" // TODO 姓名
    val personId = "" // TODO 身份证号
    val picUrl = "" // TODO 图片url
    val updateDate = dt
    Some(WhsFootHold(id, sourceType, sourceValue, personName, personId, picUrl, updateDate, footHoldsToString(footHoldsFixed), range))
  }

  /**
   * @param vehicleDataArr 输入多条车牌号相同的车辆记录, 聚合成该车辆的一条落脚点相关信息, 写入mongo
   */
  def generateDataFromVehicle(vehicleDataArr: Array[VehicleData], durationTime: Int, dt: String): Option[WhsFootHold] = {
    val vehicleSorted: Array[(VehicleData, Int)] = vehicleDataArr.sortBy(_.getPassTime)(Ordering[Long]).zipWithIndex
    // 找到连续时间对应的下标  (设备ID, 下标)
    val continuousIndex: ArrayBuffer[(Int, Int)] = findContinuousIndex(vehicleSorted.map(item => (item._1.getDeviceId, item._2)))

    // 计算落脚点
    val hourMinFormat = new SimpleDateFormat("HHmm")
    val footHolds = for (item <- continuousIndex) yield {
      val startVehicle = vehicleSorted(item._1)._1
      val endVehicle = vehicleSorted(item._2)._1
      val durationMin = (endVehicle.getPassTime - startVehicle.getPassTime) / 1000 / 60
      FootHold(startVehicle.getDeviceId,
        startVehicle.getPlaceName,
        startVehicle.getLongitude,
        startVehicle.getLatitude,
        durationMin,
        hourMinFormat.format(new Date(startVehicle.getPassTime)),
        hourMinFormat.format(new Date(endVehicle.getPassTime)))
    }
    // 筛选出停留时间大于(5h)的落脚点, 比较单位:min
    val footHoldsFixed = footHolds.toArray.filter(_.footholdTimeLong > durationTime)

    // 计算活动范围 : 凸包函数,所有点去重后进行计算
    log.warn("start to find tubao")
    val pointList: Array[Point] = vehicleDataArr.map(item => Point(item.getLongitude, item.getLatitude, item.getDeviceId)).distinct
    val range = getConvexHull(pointList)

    // 开始构造WhsFootHolds, 返回结果
    val id = s"${dt}${vehicleDataArr.head.getPlateNumber.hashCode & Int.MaxValue}".trim // id = dt & 车牌号的hash值(正)
    val sourceType = 2
    val sourceValue = vehicleDataArr.head.getPlateNumber.toString
    val personName = "" // TODO 姓名
    val personId = "" // TODO 身份证号
    val picUrl = "" // TODO 图片url
    val updateDate = dt
    Some(WhsFootHold(id, sourceType, sourceValue, personName, personId, picUrl, updateDate, footHoldsToString(footHoldsFixed), range))
  }


  /**
   * 寻找wifi或者车辆信息的连续时间下标
   *
   * @param DataSorted : 已经按时间排序好的数据  (设备ID, 下标)
   * @return
   */
  def findContinuousIndex(DataSorted: Array[(Int, Int)]): ArrayBuffer[(Int, Int)] = {
    val continuousIndex = ArrayBuffer[(Int, Int)]()
    // 遍历数组, 找到id所停留的连续时间段
    var startIndexAndDevID = (-1, -1) //   (下标, 设备ID)
    DataSorted.foreach { item =>
      startIndexAndDevID match {
        case (-1, -1) => startIndexAndDevID = (item._2, item._1)
        case _ => {
          val (currentIndex, currentDevID) = (item._2, item._1)
          // 与start进行比较
          if (currentDevID != startIndexAndDevID._2) {
            // 若设备ID不相等, 返回当前index-1即可, 并将start初始化为current
            continuousIndex.append((startIndexAndDevID._1, currentIndex - 1))
            startIndexAndDevID = (currentIndex, currentDevID)
          } else {
            // 若设备ID相等, 则只关注最后一个元素即可
            if (currentIndex == DataSorted.length - 1) continuousIndex.append((startIndexAndDevID._1, currentIndex))
          }
        }
      }
    }
    continuousIndex.filter(item => item._1 != item._2)
  }

  /**
   * 计算凸包点
   *
   * @param points
   * @return
   */
  def getConvexHull(points: Array[Point]): String = {
    // 构造Point
    val pointList = new util.ArrayList[utils.Point]()
    points.foreach(p => pointList.add(new utils.Point(p.longitude.toFloat, p.latitude.toFloat, p.deviceId)))
    if (points.length < 3) return useCurrentPoint(points)
    val m = new TubaoMelkman(pointList);
    try {
      val tubaoPoints = m.getTubaoPoint.filter(_ != null)
      val range = new util.ArrayList[util.HashMap[String, Float]]()
      tubaoPoints.foreach { point =>
        val map = new util.HashMap[String, Float]()
        map.put("deviceId", point.getDeviceId)
        map.put("longitude", point.getX)
        map.put("latitude", point.getY)
        range.add(map)
      }
      JSON.toJSONString(range, SerializerFeature.BeanToArray)
    } catch {
      case e: Exception => {
        log.warn("计算凸包点异常,返回所有落脚点作为凸包点")
        useCurrentPoint(points)
      }
    }
  }

  def useCurrentPoint(points: Array[Point]): String = {
    val range = new util.ArrayList[util.HashMap[String, Float]]()
    points.foreach { point =>
      val map = new util.HashMap[String, Float]()
      map.put("deviceId", point.deviceId)
      map.put("longitude", point.longitude.toFloat)
      map.put("latitude", point.latitude.toFloat)
      range.add(map)
    }
    JSON.toJSONString(range, SerializerFeature.BeanToArray)
  }

  /**
   * 将FootHold的样例类数组转为json格式的字符串
   *
   * @param footholds
   * @return
   */
  def footHoldsToString(footholds: Array[FootHold]): String = {
    val footHoldList = new util.ArrayList[util.HashMap[String, String]]()
    footholds.foreach { ft =>
      val map = new util.HashMap[String, String]()
      map.put("footholdDeviceId", ft.footholdDeviceId.toString)
      map.put("footholdPlaceName", ft.footholdPlaceName)
      map.put("footholdLongitude", ft.footholdLongitude.toString)
      map.put("footholdLatitude", ft.footholdLatitude.toString)
      map.put("footholdTimeLong", ft.footholdTimeLong.toString)
      map.put("footholdStartTime", ft.footholdStartTime)
      map.put("footholdEndTime", ft.footholdEndTime)
      footHoldList.add(map)
    }
    JSON.toJSONString(footHoldList, SerializerFeature.BeanToArray)

  }
}

/**
 * 下沉到mongo中dws_whs_foothold表的字段
 *
 * @param _id        主键 (update + sourceValue的hash)
 * @param sourceType
 * @param sourceValue
 * @param personName 人员姓名
 * @param personId   身份证号
 * @param picUrl     图片url
 * @param updateDate 更新日期
 * @param foothods   落脚点信息
 * @param range
 */
case class WhsFootHold(_id: String, sourceType: Int, sourceValue: String, personName: String, personId: String, picUrl: String, updateDate: String, foothods: String, range: String)

/**
 *
 * @param footholdDeviceId  设备ID
 * @param footholdPlaceName
 * @param footholdLongitude 经度
 * @param footholdLatitude  维度
 * @param footholdTimeLong  : 停留时长 : min
 * @param footholdStartTime 落脚点起始时间
 * @param footholdEndTime   落脚点结束时间
 */
case class FootHold(footholdDeviceId: Int, footholdPlaceName: String, footholdLongitude: Double, footholdLatitude: Double, footholdTimeLong: Double, footholdStartTime: String, footholdEndTime: String)

/**
 * 设备id的经纬度地址
 *
 * @param longitude
 * @param latitude
 */
case class Point(longitude: Double, latitude: Double, deviceId: Int)



