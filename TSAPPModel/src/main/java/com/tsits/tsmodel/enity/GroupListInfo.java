package com.tsits.tsmodel.enity;

/**
 * 组列表信息
 */
public class GroupListInfo {

    private String deviceID;

    public GroupListInfo(){

    }

    public GroupListInfo(String deviceID){
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

}
