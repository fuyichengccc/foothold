package com.jinp.videobigdata.entity;

import java.io.Serializable;
import java.util.Date;

public class WifiData implements Serializable {
    private String macAddress;
    private String brand;
    // 捕获时间
    private Date captureTime;
    //
    private Integer strength;
    private String identificationType;
    private String certificateCode;
    private String apSsid;
    private String apMac;
    private Integer apChannel;
    private Integer encryptType;
    private double xCoordinate;
    private double yCoordinate;
    private String placeCode;
    private String placeName;
    private String devNo;
    private double longitude;
    private double latitude;
    private String dt;
    private int deviceId;

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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Date getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(Date captureTime) {
        this.captureTime = captureTime;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    public String getApSsid() {
        return apSsid;
    }

    public void setApSsid(String apSsid) {
        this.apSsid = apSsid;
    }

    public String getApMac() {
        return apMac;
    }

    public void setApMac(String apMac) {
        this.apMac = apMac;
    }

    public Integer getApChannel() {
        return apChannel;
    }

    public void setApChannel(Integer apChannel) {
        this.apChannel = apChannel;
    }

    public Integer getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(Integer encryptType) {
        this.encryptType = encryptType;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
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

    public String getDevNo() {
        return devNo;
    }

    public void setDevNo(String devNo) {
        this.devNo = devNo;
    }

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
}
