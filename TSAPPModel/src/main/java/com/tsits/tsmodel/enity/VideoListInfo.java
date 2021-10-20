package com.tsits.tsmodel.enity;

import java.util.Date;

/**
 * 适配列表信息
 */
public class VideoListInfo {

    private String deviceID;
    private Date dateTime;

    public VideoListInfo(String deviceID, Date dateTime){
        this.deviceID = deviceID;
        this.dateTime = dateTime;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
