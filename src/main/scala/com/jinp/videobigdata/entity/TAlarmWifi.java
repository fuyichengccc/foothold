package com.jinp.videobigdata.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
 *  @author wch
 */
public class TAlarmWifi implements Serializable {

    private static final long serialVersionUID = 1567066103719L;


    /**
    * 主键
    * 主键id
    * isNullAble:0
    */
    private Long id;

    /**
    * 预警时间
    * isNullAble:1
    */
    private long alarmTime;

    /**
    * mac地址
    * isNullAble:1
    */
    private String macAddress;

    /**
    * 设备编号
    * isNullAble:1
    */
    private String deviceCode;
    private int deviceId;

    /**
    * 虚拟身份类型
    * isNullAble:1
    */
    private String virtualidType;

    /**
    * 虚拟身份值
    * isNullAble:1
    */
    private String virtualid;

    /**
    * 布控id
    * isNullAble:1
    */
    private Long surId;

    /**
    * 创建时间
    * isNullAble:1
    */
    private java.time.LocalDateTime createTime;

    /**
    * 修改时间
    * isNullAble:1
    */
    private java.time.LocalDateTime updateTime;

    /**
    * 
    * isNullAble:1
    */
    private Long createBy;

    /**
    * 
    * isNullAble:1
    */
    private Long updateBy;

    /**
    * 
    * isNullAble:1
    */
    private String remarks;

    private String surName;

    private String deviceName;

    private double latitude;

    private double longitude;

    private String dealDepart;

    private String personName;

    private String personId;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDealDepart() {
        return dealDepart;
    }

    public void setDealDepart(String dealDepart) {
        this.dealDepart = dealDepart;
    }

    public void setId(Long id){this.id = id;}

    public Long getId(){return this.id;}

    public void setAlarmTime(long alarmTime){this.alarmTime = alarmTime;}

    public long getAlarmTime(){return this.alarmTime;}

    public void setMacAddress(String macAddress){this.macAddress = macAddress;}

    public String getMacAddress(){return this.macAddress;}



    public void setVirtualidType(String virtualidType){this.virtualidType = virtualidType;}

    public String getVirtualidType(){return this.virtualidType;}

    public void setVirtualid(String virtualid){this.virtualid = virtualid;}

    public String getVirtualid(){return this.virtualid;}

    public void setSurId(Long surId){this.surId = surId;}

    public Long getSurId(){return this.surId;}

    public void setCreateTime(java.time.LocalDateTime createTime){this.createTime = createTime;}

    public java.time.LocalDateTime getCreateTime(){return this.createTime;}

    public void setUpdateTime(java.time.LocalDateTime updateTime){this.updateTime = updateTime;}

    public java.time.LocalDateTime getUpdateTime(){return this.updateTime;}

    public void setCreateBy(Long createBy){this.createBy = createBy;}

    public Long getCreateBy(){return this.createBy;}

    public void setUpdateBy(Long updateBy){this.updateBy = updateBy;}

    public Long getUpdateBy(){return this.updateBy;}

    public void setRemarks(String remarks){this.remarks = remarks;}

    public String getRemarks(){return this.remarks;}

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
