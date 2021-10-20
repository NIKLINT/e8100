package com.tsits.tsmodel.enity;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * 录音列表信息
 */
public class VoiceRecordInfo {

    private int targetID;
    private String targetName;
    private Bitmap targetIcon;
    private String lastTalker;
    private Date lastTime;
    private int newCount;
    private String path;


    public VoiceRecordInfo() {
    }

    public VoiceRecordInfo(int targetID,  Date lastTime){
        this.targetID = targetID;
        this.lastTime = lastTime;
    }

    public VoiceRecordInfo(int targetID, String targetName, Bitmap targetIcon, String lastTalker, Date lastTime, int newCount, String path) {
        this.targetID = targetID;
        this.targetName = targetName;
        this.targetIcon = targetIcon;
        this.lastTalker = lastTalker;
        this.lastTime = lastTime;
        this.newCount = newCount;
        this.path = path;
    }

    public int getTargetID() {
        return targetID;
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Bitmap getTargetIcon() {
        return targetIcon;
    }

    public void setTargetIcon(Bitmap targetIcon) {
        this.targetIcon = targetIcon;
    }

    public String getLastTalker() {
        return lastTalker;
    }

    public void setLastTalker(String lastTalker) {
        this.lastTalker = lastTalker;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public int getNewCount() {
        return newCount;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
