package com.jinp.videobigdata.entity;

import java.util.List;
import java.util.StringJoiner;

public class WifiSurveillance {
    private int id ;
    private String macAddress;
    private List<String> deviceIds;
    // '0:单维布控 1:多维布控 2:动态多维布控 3:超出预警范围布控 4:落脚点异常布控'
    private int source;

    private String dealDepart;

    private String surName;

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

    public String getDealDepart() {
        return dealDepart;
    }

    public void setDealDepart(String dealDepart) {
        this.dealDepart = dealDepart;
    }

    public WifiSurveillance() {
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


    @Override
    public String toString() {
        return new StringJoiner(", ", WifiSurveillance.class.getSimpleName() + "[", "]").add("id=" + id).add("macAddress='" + macAddress + "'").add("deviceIds=" + deviceIds).add("source=" + source).add("dealDepart='" + dealDepart + "'").add("surName='" + surName + "'").add("personName='" + personName + "'").add("personId='" + personId + "'").toString();
    }

    public WifiSurveillance(int id, String macAddress, List<String> deviceIds, int source, String dealDepart, String surName, String personName, String personId) {
        this.id = id;
        this.macAddress = macAddress;
        this.deviceIds = deviceIds;
        this.source = source;
        this.dealDepart = dealDepart;
        this.surName = surName;
        this.personName = personName;
        this.personId = personId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
