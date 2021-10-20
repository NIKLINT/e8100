package com.tsits.tsmodel.enity;

import java.util.Date;

/**
 * 呼叫历史记录信息类
 */
public class CallHistoryInfo {

    private String callerID;
    private String callID;
    private int callType;
    private Date dateTime;

    public String getDeviceID() {
        return callerID;
    }

    public void setDeviceID(String deviceID) {
        this.callerID = deviceID;
    }

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public CallHistoryInfo(String deviceID, String callID, int callType, Date dateTime){
        this.callerID = deviceID;
        this.callID = callID;
        this.callType = callType;
        this.dateTime = dateTime;
    }

}
