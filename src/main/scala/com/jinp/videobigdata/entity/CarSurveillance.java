package com.jinp.videobigdata.entity;

import java.util.List;
import java.util.StringJoiner;

public class CarSurveillance {
    private int id ;
    private String plateNumber;
    private List<String> deviceIds;
    // '0:单维布控 1:多维布控 2:动态多维布控 3:超出预警范围布控 4:落脚点异常布控'
    private int source;

    private String dealDepart;

    private String surName;

    private String personName;

    private  String personId;

    @Override
    public String toString() {
        return new StringJoiner(", ", CarSurveillance.class.getSimpleName() + "[", "]").add("id=" + id).add("plateNumber='" + plateNumber + "'").add("deviceIds=" + deviceIds).add("source=" + source).add("dealDepart='" + dealDepart + "'").add("surName='" + surName + "'").add("personName='" + personName + "'").add("personId='" + personId + "'").toString();
    }

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

    public CarSurveillance() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getDealDepart() {
        return dealDepart;
    }

    public void setDealDepart(String dealDepart) {
        this.dealDepart = dealDepart;
    }

    public CarSurveillance(int id, String plateNumber, List<String> deviceIds, int source, String dealDepart, String surName, String personName, String personId) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.deviceIds = deviceIds;
        this.source = source;
        this.dealDepart = dealDepart;
        this.surName = surName;
        this.personName = personName;
        this.personId = personId;
    }
}
