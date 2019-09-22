package com.jinp.videobigdata.foothold.caseEntity

case class WifiData(macAddress: String, brand: String, captureTime: String, deviceId: Int,
                    strength: Int, identificationType: String, certificateCode: String,
                    apSsid: String, apMac: String, apChannel: String, encryptType: String,
                    xCoordinate: Double, yCoordinate: Double, placeCode: String,
                    placeName: String, devNo: String, longitude: Double, latitude: Double, dt: String)

case class VehicleData(plateNumber: Int, passTime: Long, deviceId: Int, deviceName: String,placeName:String, longitude: Double, latitude: Double)