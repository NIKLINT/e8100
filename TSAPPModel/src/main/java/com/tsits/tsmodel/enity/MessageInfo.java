package com.tsits.tsmodel.enity;

import java.io.Serializable;

/**
 * @author： YY
 * @date： 2020/8/18
 * 短信内容信息
 */
public class MessageInfo implements Serializable {

    private String date;
    /**
     * * 0：接收未读 * 1：发送未成功 * 2：发送成功 * 3：接收已读 * 4：正在发送中
     */
    private int isSend;
    /**
     * 消息内容
     */
    private String content;

    /**
     * 发起者
     */
    private String sender;
    /**
     * 目标号码
     */
    private String tel;
    private String name;
    /**
     * 发的内容是什么类型 “video”,"pic","audio"等
     */
    private String type;
    /**
     * 判断是否已经下载 0:没下载 1：下载完成 -1 ：正在下载
     */
    private int isLoad;
    /**
     * 是否是个群信息 0：个人信息 1：群信息
     */
    private int isCrowd = 0;
    /**
     * 在群的情况下使用，表示组下面的成员，如果是个人短信是空
     */
    private String userTel;
    /**
     * 是否播放过 0：没播放过 1：播放过了
     */
    private int hasPlayed = 0;
    /**
     * 是否正在播放 0:没有播放 1：正在播放
     */
    private int isPlaying = 0;
    /**
     * 发该短信的人是否在线 0:l离线 1：在线
     */
    private int loadStatus = 0;

    public int getIsPlaying() {
        return this.isPlaying;
    }

    public void setIsPlaying(int isPlaying) {
        this.isPlaying = isPlaying;
    }

    public int getIsAudioOver() {
        return this.hasPlayed;
    }

    public void setIsAudioOver(int isAudioOver) {
        this.hasPlayed = isAudioOver;
    }

    public String getUserTel() {
        return this.userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return this.date;
    }

    public void setData(String date) {
        this.date = date;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getIsSend() {
        return this.isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public int isCrowd() {
        return this.isCrowd;
    }

    public void setCrowd(int isCrowd) {
        this.isCrowd = isCrowd;
    }

    public MessageInfo(String date, int isSend, String content, String tel, String name, String type, int isLoad, int isCrowd, String userTel, int isAudioOver) {
        this.date = date;
        this.isSend = isSend;
        this.content = content;
        this.tel = tel;
        this.name = name;
        this.type = type;
        this.isLoad = isLoad;
        this.isCrowd = isCrowd;
        this.userTel = userTel;
        this.hasPlayed = isAudioOver;
    }

    public int getIsLoad() {
        return this.isLoad;
    }

    public void setIsLoad(int isLoad) {
        this.isLoad = isLoad;
    }

    public void setLoadStatus(int loadStatus) {
        this.loadStatus = loadStatus;
    }

    public int getLoadStatus() {
        return this.loadStatus;
    }

    @Override
    public String toString() {
        return this.tel + " " + this.isSend + " " + this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIsCrowd() {
        return this.isCrowd;
    }

    public void setIsCrowd(int isCrowd) {
        this.isCrowd = isCrowd;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
