package com.jinp.videobigdata.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class VehicleData implements Serializable {

    //@JacksonXmlProperty(localName = "CamID")
    private String camId;
    //@JacksonXmlProperty(localName = "DevID")
    private String devID;
    //@JacksonXmlProperty(localName = "RecordID")
    private String recordID;
    //@JacksonXmlProperty(localName = "CarPlate")
    private String carPlate;
    //@JacksonXmlProperty(localName = "EquipmentType")
    private String equipmentType;
    private double longitude;
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    private double latitude;
    //@JacksonXmlProperty(localName = "PanoramaFlag")
    private String panoramaFlag;

    //@JacksonXmlProperty(localName = "TollgateID")
    private String tollgateID;
    //@JacksonXmlProperty(localName = "TollgateName")
    private String tollgateName;
    //@JacksonXmlProperty(localName = "PassTime")
    @JSONField(format = "yyyyMMddHHmmssSSS")
    private long passTime;
    //@JacksonXmlProperty(localName = "PlaceCode")
    private String placeCode;
    //@JacksonXmlProperty(localName = "PlaceName")
    private String placeName;
    //@JacksonXmlProperty(localName = "LaneID")
    private String laneID;
    //@JacksonXmlProperty(localName = "LaneType")
    private String laneType;
    //@JacksonXmlProperty(localName = "LaneDirection")
    private String laneDirection;
    //@JacksonXmlProperty(localName = "LaneDescription")
    private String laneDescription;
    //@JacksonXmlProperty(localName = "Direction")
    private String direction;
    //@JacksonXmlProperty(localName = "DirectionName")
    private String directionName;
    //@JacksonXmlProperty(localName = "PlateStatus")
    private String plateStatus;
    //@JacksonXmlProperty(localName = "PlateConfidence")
    private String plateConfidence;
    //@JacksonXmlProperty(localName = "CharConfidence")
    private String charConfidence;
    //@JacksonXmlProperty(localName = "PlateType")
    private String plateType;
    //@JacksonXmlProperty(localName = "PlateColor")
    private String plateColor;
    //@JacksonXmlProperty(localName = "PlateNumber")
    private Integer plateNumber;
    //@JacksonXmlProperty(localName = "PlateCoincide")
    private String plateCoincide;
    //@JacksonXmlProperty(localName = "PicNumber")
    private Integer picNumber;
    //@JacksonXmlProperty(localName = "VehicleSpeed")
    private Integer vehicleSpeed;
    //@JacksonXmlProperty(localName = "LimitedSpeed")
    private Integer limitedSpeed;
    //@JacksonXmlProperty(localName = "MarkedSpeed")
    private Integer markedSpeed;
    //@JacksonXmlProperty(localName = "SimulateFlag")
    private Integer simulateFlag;
    //@JacksonXmlProperty(localName = "DriveStatus")
    private Integer driveStatus;
    //@JacksonXmlProperty(localName = "VehicleWeight")
    private Integer vehicleWeight;
    //@JacksonXmlProperty(localName = "VehicleBrand")
    private String vehicleBrand;
    //@JacksonXmlProperty(localName = "VehicleBody")
    private String vehicleBody;
    //@JacksonXmlProperty(localName = "VehicleType")
    private String vehicleType;
    //@JacksonXmlProperty(localName = "VehicleType2")
    private String vehicleType2;
    //@JacksonXmlProperty(localName = "TargetType")
    private String targetType;
    //@JacksonXmlProperty(localName = "VehicleLength")
    private Integer vehicleLength;
    //@JacksonXmlProperty(localName = "VehicleColor")
    private String vehicleColor;
    //@JacksonXmlProperty(localName = "IdentifyStatus")
    private String identifyStatus;
    //@JacksonXmlProperty(localName = "RedLightStartTime")
    private String redLightStartTime;
    //@JacksonXmlProperty(localName = "RedLightEndTime")
    private String redLightEndTime;
    //@JacksonXmlProperty(localName = "RedLightTime")
    private String redLightTime;
    //@JacksonXmlProperty(localName = "DealTag")
    private String dealTag;
    //@JacksonXmlProperty(localName = "DressColor")
    private String dressColor;
    //@JacksonXmlProperty(localName = "ApplicationType")
    private Integer applicationType;
    //@JacksonXmlProperty(localName = "GlobalComposeFlag")
    private Integer globalComposeFlag;
    //@JacksonXmlProperty(localName = "RearPlateColor")
    private String rearPlateColor;
    //@JacksonXmlProperty(localName = "RearPlateType")
    private String rearPlateType;
    //@JacksonXmlProperty(localName = "RearPlateConfidence")
    private String rearPlateConfidence;
    //@JacksonXmlProperty(localName = "RearVehiclePlateID")
    private String rearVehiclePlateID;
    //@JacksonXmlProperty(localName = "ImageURL1")
    private String imageURL1;
    //@JacksonXmlProperty(localName = "ImageURL2")
    private String imageURL2;
    //@JacksonXmlProperty(localName = "ImageURL3")
    private String imageURL3;
    //@JacksonXmlProperty(localName = "ImageURL4")
    private String imageURL4;

    private int deviceId;
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private String dt;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getRearPlateConfidence() {
        return rearPlateConfidence;
    }

    public void setRearPlateConfidence(String rearPlateConfidence) {
        this.rearPlateConfidence = rearPlateConfidence;
    }

    public String getIdentifyStatus() {
        return identifyStatus;
    }

    public void setIdentifyStatus(String identifyStatus) {
        this.identifyStatus = identifyStatus;
    }


    public String getCamId() {
        return camId;
    }

    public void setCamId(String camId) {
        this.camId = camId;
    }

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getPanoramaFlag() {
        return panoramaFlag;
    }

    public void setPanoramaFlag(String panoramaFlag) {
        this.panoramaFlag = panoramaFlag;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getTollgateID() {
        return tollgateID;
    }

    public void setTollgateID(String tollgateID) {
        this.tollgateID = tollgateID;
    }

    public String getTollgateName() {
        return tollgateName;
    }

    public void setTollgateName(String tollgateName) {
        this.tollgateName = tollgateName;
    }

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    public String getPlaceCode() {
        return placeCode;
    }

    public void setPlaceCode(String placeCode) {
        this.placeCode = placeCode;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLaneID() {
        return laneID;
    }

    public void setLaneID(String laneID) {
        this.laneID = laneID;
    }

    public String getLaneType() {
        return laneType;
    }

    public void setLaneType(String laneType) {
        this.laneType = laneType;
    }

    public String getLaneDirection() {
        return laneDirection;
    }

    public void setLaneDirection(String laneDirection) {
        this.laneDirection = laneDirection;
    }

    public String getLaneDescription() {
        return laneDescription;
    }

    public void setLaneDescription(String laneDescription) {
        this.laneDescription = laneDescription;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public String getPlateStatus() {
        return plateStatus;
    }

    public void setPlateStatus(String plateStatus) {
        this.plateStatus = plateStatus;
    }

    public String getPlateConfidence() {
        return plateConfidence;
    }

    public void setPlateConfidence(String plateConfidence) {
        this.plateConfidence = plateConfidence;
    }

    public String getCharConfidence() {
        return charConfidence;
    }

    public void setCharConfidence(String charConfidence) {
        this.charConfidence = charConfidence;
    }

    public String getPlateType() {
        return plateType;
    }

    public void setPlateType(String plateType) {
        this.plateType = plateType;
    }

    public String getPlateColor() {
        return plateColor;
    }

    public void setPlateColor(String plateColor) {
        this.plateColor = plateColor;
    }

    public Integer getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(Integer plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPlateCoincide() {
        return plateCoincide;
    }

    public void setPlateCoincide(String plateCoincide) {
        this.plateCoincide = plateCoincide;
    }

    public Integer getPicNumber() {
        return picNumber;
    }

    public void setPicNumber(Integer picNumber) {
        this.picNumber = picNumber;
    }

    public Integer getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(Integer vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }

    public Integer getLimitedSpeed() {
        return limitedSpeed;
    }

    public void setLimitedSpeed(Integer limitedSpeed) {
        this.limitedSpeed = limitedSpeed;
    }

    public Integer getMarkedSpeed() {
        return markedSpeed;
    }

    public void setMarkedSpeed(Integer markedSpeed) {
        this.markedSpeed = markedSpeed;
    }

    public Integer getSimulateFlag() {
        return simulateFlag;
    }

    public void setSimulateFlag(Integer simulateFlag) {
        this.simulateFlag = simulateFlag;
    }

    public Integer getDriveStatus() {
        return driveStatus;
    }

    public void setDriveStatus(Integer driveStatus) {
        this.driveStatus = driveStatus;
    }

    public Integer getVehicleWeight() {
        return vehicleWeight;
    }

    public void setVehicleWeight(Integer vehicleWeight) {
        this.vehicleWeight = vehicleWeight;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleBody() {
        return vehicleBody;
    }

    public void setVehicleBody(String vehicleBody) {
        this.vehicleBody = vehicleBody;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleType2() {
        return vehicleType2;
    }

    public void setVehicleType2(String vehicleType2) {
        this.vehicleType2 = vehicleType2;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(Integer vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public VehicleData() {
    }

    public String getRedLightStartTime() {
        return redLightStartTime;
    }

    public void setRedLightStartTime(String redLightStartTime) {
        this.redLightStartTime = redLightStartTime;
    }

    public String getRedLightEndTime() {
        return redLightEndTime;
    }

    public void setRedLightEndTime(String redLightEndTime) {
        this.redLightEndTime = redLightEndTime;
    }

    public String getRedLightTime() {
        return redLightTime;
    }

    public void setRedLightTime(String redLightTime) {
        this.redLightTime = redLightTime;
    }

    public String getDealTag() {
        return dealTag;
    }

    public void setDealTag(String dealTag) {
        this.dealTag = dealTag;
    }

    public String getDressColor() {
        return dressColor;
    }

    public void setDressColor(String dressColor) {
        this.dressColor = dressColor;
    }

    public Integer getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(Integer applicationType) {
        this.applicationType = applicationType;
    }

    public Integer getGlobalComposeFlag() {
        return globalComposeFlag;
    }

    public void setGlobalComposeFlag(Integer globalComposeFlag) {
        this.globalComposeFlag = globalComposeFlag;
    }

    public String getRearPlateColor() {
        return rearPlateColor;
    }

    public void setRearPlateColor(String rearPlateColor) {
        this.rearPlateColor = rearPlateColor;
    }

    public String getRearPlateType() {
        return rearPlateType;
    }

    public void setRearPlateType(String rearPlateType) {
        this.rearPlateType = rearPlateType;
    }

    public String getRearVehiclePlateID() {
        return rearVehiclePlateID;
    }

    public void setRearVehiclePlateID(String rearVehiclePlateID) {
        this.rearVehiclePlateID = rearVehiclePlateID;
    }

    public String getImageURL1() {
        return imageURL1;
    }

    public void setImageURL1(String imageURL1) {
        this.imageURL1 = imageURL1;
    }

    public String getImageURL2() {
        return imageURL2;
    }

    public void setImageURL2(String imageURL2) {
        this.imageURL2 = imageURL2;
    }

    public String getImageURL3() {
        return imageURL3;
    }

    public void setImageURL3(String imageURL3) {
        this.imageURL3 = imageURL3;
    }

    public String getImageURL4() {
        return imageURL4;
    }

    public void setImageURL4(String imageURL4) {
        this.imageURL4 = imageURL4;
    }

}
