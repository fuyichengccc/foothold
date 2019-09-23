package com.jinp.videobigdata.foothold.javaEntity;

import java.io.Serializable;

public class FootHoldJava implements Serializable {
    private int footholdDeviceId;
    private String footholdPlaceName;
    private double footholdLongitude;
    private double footholdLatitude;
    private long footholdTimeLong;
    private String footholdStartTime;
    private String footholdEndTime;

    public FootHoldJava(int footholdDeviceId, String footholdPlaceName, double footholdLongitude, double footholdLatitude, long footholdTimeLong, String footholdStartTime, String footholdEndTime) {
        this.footholdDeviceId = footholdDeviceId;
        this.footholdPlaceName = footholdPlaceName;
        this.footholdLongitude = footholdLongitude;
        this.footholdLatitude = footholdLatitude;
        this.footholdTimeLong = footholdTimeLong;
        this.footholdStartTime = footholdStartTime;
        this.footholdEndTime = footholdEndTime;
    }

    public FootHoldJava() {
    }

    public int getFootholdDeviceId() {
        return footholdDeviceId;
    }

    public void setFootholdDeviceId(int footholdDeviceId) {
        this.footholdDeviceId = footholdDeviceId;
    }

    public String getFootholdPlaceName() {
        return footholdPlaceName;
    }

    public void setFootholdPlaceName(String footholdPlaceName) {
        this.footholdPlaceName = footholdPlaceName;
    }

    public double getFootholdLongitude() {
        return footholdLongitude;
    }

    public void setFootholdLongitude(double footholdLongitude) {
        this.footholdLongitude = footholdLongitude;
    }

    public double getFootholdLatitude() {
        return footholdLatitude;
    }

    public void setFootholdLatitude(double footholdLatitude) {
        this.footholdLatitude = footholdLatitude;
    }

    public long getFootholdTimeLong() {
        return footholdTimeLong;
    }

    public void setFootholdTimeLong(long footholdTimeLong) {
        this.footholdTimeLong = footholdTimeLong;
    }

    public String getFootholdStartTime() {
        return footholdStartTime;
    }

    public void setFootholdStartTime(String footholdStartTime) {
        this.footholdStartTime = footholdStartTime;
    }

    public String getFootholdEndTime() {
        return footholdEndTime;
    }

    public void setFootholdEndTime(String footholdEndTime) {
        this.footholdEndTime = footholdEndTime;
    }
}
