package com.jinp.videobigdata.foothold

case class WifiDataCase(macAddress: String, brand: String, captureTime: String, deviceId: Int,
                    strength: Int, identificationType: String, certificateCode: String,
                    apSsid: String, apMac: String, apChannel: String, encryptType: String,
                    xCoordinate: Double, yCoordinate: Double, placeCode: String,
                    placeName: String, devNo: String, longitude: Double, latitude: Double, dt: String)

case class VehicleDataCase(plateNumber: Int, passTime: Long, deviceId: Int, deviceName: String,placeName:String, longitude: Double, latitude: Double)


/**
  * 下沉到mongo中dws_whs_foothold表的字段
  *
  * @param _id        主键 (update + sourceValue的hash)
  * @param sourceType
  * @param sourceValue
  * @param personName 人员姓名
  * @param personId   身份证号
  * @param picUrl     图片url
  * @param dt 更新日期
  * @param foothods   落脚点信息
  * @param range
  */
case class WhsFootHold(_id: String, sourceType: Int, sourceValue: String, personName: String, personId: String, picUrl: String, dt: String, foothods: String, range: String)

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
case class FootHold(footholdDeviceId: Int, footholdPlaceName: String, footholdLongitude: Double, footholdLatitude: Double, footholdTimeLong: Long, footholdStartTime: String, footholdEndTime: String)

/**
  * 设备id的经纬度地址
  *
  * @param longitude
  * @param latitude
  */
case class Point(longitude: Double, latitude: Double, deviceId: Int)