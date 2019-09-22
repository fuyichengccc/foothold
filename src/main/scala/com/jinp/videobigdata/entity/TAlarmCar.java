package com.jinp.videobigdata.entity;
import java.io.Serializable;
import java.util.*;

/**
 *  @author wch
 */
public class TAlarmCar implements Serializable {

    private static final long serialVersionUID = 1567065903505L;


    /**
    * 主键
    * 主键id
    * isNullAble:0
    */
    private Long id;

    /**
    * 预警时间
    */
    private long alarmTime;

    /**
    * 1: 假车牌2: 套牌车3: 非法运营车辆 4: 运输危险物品车辆
    */
    private Integer alarmType;

    /**
    * 报警编号
    */
    private String alarmNo;

    /**
    * 车辆记录编号
    */
    private String recordId;

    /**
    * 布控id
    */
    private Long surId;

    /**
    * 设备编号
    */
    private String deviceCode;

    private int deviceId;

    /**
    * 过车时间
    * isNullAble:1
    */
    private long passTime;

    /**
    * 方向编号
    * isNullAble:1
    */
    private String direction;

    /**
    * 车牌号
    * isNullAble:1
    */
    private String carplate;

    /**
    * 车牌颜色
    * isNullAble:1
    */
    private String platecolor;

    /**
    * 车辆类型
    * isNullAble:1
    */
    private String vehicleType;

    /**
    * 车速
    * isNullAble:1
    */
    private Integer vehicleSpeed;

    /**
    * 当前状态
    * isNullAble:1
    */
    private Integer status;

    /**
    * 图片数量
    * isNullAble:1
    */
    private Integer picNumber;

    /**
    * 图片1
    * isNullAble:1
    */
    private String picurl1;

    /**
    * 图片2
    * isNullAble:1
    */
    private String picurl2;

    /**
    * 图片3
    * isNullAble:1
    */
    private String picurl3;

    /**
    * 图片4
    * isNullAble:1
    */
    private String picurl4;

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

    private String surName;

    private String deviceName;

    private double latitude;

    private double longitude;

    private String dealDepart;

    private String personName;

    private String personId;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setId(Long id){this.id = id;}

    public Long getId(){return this.id;}

    public void setAlarmTime(long alarmTime){this.alarmTime = alarmTime;}

    public long getAlarmTime(){return this.alarmTime;}

    public void setAlarmType(Integer alarmType){this.alarmType = alarmType;}

    public Integer getAlarmType(){return this.alarmType;}

    public void setAlarmNo(String alarmNo){this.alarmNo = alarmNo;}

    public String getAlarmNo(){return this.alarmNo;}

    public void setRecordId(String recordId){this.recordId = recordId;}

    public String getRecordId(){return this.recordId;}

    public void setSurId(Long surId){this.surId = surId;}

    public Long getSurId(){return this.surId;}



    public void setPassTime(long passTime){this.passTime = passTime;}

    public long getPassTime(){return this.passTime;}

    public void setDirection(String direction){this.direction = direction;}

    public String getDirection(){return this.direction;}

    public void setCarplate(String carplate){this.carplate = carplate;}

    public String getCarplate(){return this.carplate;}

    public void setPlatecolor(String platecolor){this.platecolor = platecolor;}

    public String getPlatecolor(){return this.platecolor;}

    public void setVehicleType(String vehicleType){this.vehicleType = vehicleType;}

    public String getVehicleType(){return this.vehicleType;}

    public void setVehicleSpeed(Integer vehicleSpeed){this.vehicleSpeed = vehicleSpeed;}

    public Integer getVehicleSpeed(){return this.vehicleSpeed;}

    public void setStatus(Integer status){this.status = status;}

    public Integer getStatus(){return this.status;}

    public void setPicNumber(Integer picNumber){this.picNumber = picNumber;}

    public Integer getPicNumber(){return this.picNumber;}

    public void setPicurl1(String picurl1){this.picurl1 = picurl1;}

    public String getPicurl1(){return this.picurl1;}

    public void setPicurl2(String picurl2){this.picurl2 = picurl2;}

    public String getPicurl2(){return this.picurl2;}

    public void setPicurl3(String picurl3){this.picurl3 = picurl3;}

    public String getPicurl3(){return this.picurl3;}

    public void setPicurl4(String picurl4){this.picurl4 = picurl4;}

    public String getPicurl4(){return this.picurl4;}

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


    @Override
    public String toString() {
        return new StringJoiner(", ", TAlarmCar.class.getSimpleName() + "[", "]").add("id=" + id).add("alarmTime=" + alarmTime).add("alarmType=" + alarmType).add("alarmNo='" + alarmNo + "'").add("recordId='" + recordId + "'").add("surId=" + surId).add("deviceCode='" + deviceCode + "'").add("deviceId=" + deviceId).add("passTime=" + passTime).add("direction='" + direction + "'").add("carplate='" + carplate + "'").add("platecolor='" + platecolor + "'").add("vehicleType='" + vehicleType + "'").add("vehicleSpeed=" + vehicleSpeed).add("status=" + status).add("picNumber=" + picNumber).add("picurl1='" + picurl1 + "'").add("picurl2='" + picurl2 + "'").add("picurl3='" + picurl3 + "'").add("picurl4='" + picurl4 + "'").add("createTime=" + createTime).add("updateTime=" + updateTime).add("createBy=" + createBy).add("updateBy=" + updateBy).add("remarks='" + remarks + "'").add("surName='" + surName + "'").add("deviceName='" + deviceName + "'").add("latitude=" + latitude).add("longitude=" + longitude).add("dealDepart='" + dealDepart + "'").add("personName='" + personName + "'").add("personId='" + personId + "'").toString();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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
