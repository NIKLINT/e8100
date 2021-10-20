package com.tsits.tsmodel.manager;

import com.impl.struct.RF_GPSInfo;
import com.impl.struct.TSRunTimeStatusInfo;
import com.tsits.tsmodel.enity.TSITSConfig;

import com.tsits.tsmodel.utils.LogUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 服务数据管理
 */
public class ServiceDataManager {
    private volatile static ServiceDataManager instance;

    //是否初始化完成
    private boolean initialization;
    //运行数据结构
    private TSRunTimeStatusInfo runTimeStatusInfo;
    //数据监听管理
    private TSListenerManager mTSListenerManager;

    public TSListenerManager getTSListenerManager() {
        return mTSListenerManager;
    }

    public boolean isInitialization() {
        return initialization;
    }

    //GPS坐标集合
    private LinkedHashMap<Integer, RF_GPSInfo> gpsList = new LinkedHashMap<>();
    //SOS设备集合
    private List<Integer> sosList = new ArrayList<>();

    private ServiceDataManager(){
        mTSListenerManager = TSListenerManager.getInstance();
    }

    /**
     * 返回数据管理类唯一实例
     * @return
     */
    public static ServiceDataManager getInstance() {
        synchronized (ServiceDataManager.class) {
            if (instance == null) {
                instance = new ServiceDataManager();
            }
        }
        return instance;
    }

    /**
     * 核心服务初始化完成
     * @param isSuccess
     * @param runTimeStatusInfo
     */
    public void coreServiceInitFinished(boolean isSuccess, TSITSConfig tsitsConfig, TSRunTimeStatusInfo runTimeStatusInfo) {
        this.setInitialization(isSuccess);
        if (isSuccess && runTimeStatusInfo != null ) {
            if (runTimeStatusInfo != null) {
                setRunTimeStatusInfo(runTimeStatusInfo);
                log("============================ Begin 从服务中获取的运行状态状态 ===========================");
                log(String.format("CommuniSDKInitStatus：%d\tCommuniSDK 初始化状态", runTimeStatusInfo.getSDKInitStatus()));
                log( "WorkType：" + runTimeStatusInfo.getWorkType() + "\t工作类型");
                log( "StationID：" + runTimeStatusInfo.getStationID() + "\t站点");
                log( "RegState：" + runTimeStatusInfo.getRegState() + "\t当前集群状态");
                log( "ChannelIndex：" + runTimeStatusInfo.getChannelIndex() + "\t常规：当前工作信道 集群:当前守候信道");
                log( "isTalk：" + runTimeStatusInfo.isTalk() + "\t当前是否存在会话");
                log( "CallType：" + runTimeStatusInfo.getCallType() + "\t呼叫类型");
                log( "CallID：" + runTimeStatusInfo.getCallID() + "\t呼叫 ID");
                log( "CallerID：" + runTimeStatusInfo.getCallerID() + "\t呼叫发起者");
                log( "isSuperDevice：" + runTimeStatusInfo.getIsSuperDevice() + "\t超级设备");
                log( "isStun：" + runTimeStatusInfo.getIsStun() + "\t集群下的摇晕 #范围 0-否 1-是");
                log( "DmChannelType：" + runTimeStatusInfo.getDmChannelType() + "\t模拟还是数字常规 #范围 0-模拟信道 1-数字信道");
                log( "============================从核心服务获取初始运行状态状态 End=============================");
            } else {
                log("============================ 服务中获取的运行状态状态 Null ===========================");
            }
            //设置常规模式工作频点
            resetDMWorkFrequency();
            log("AppService完成初始化.");
        }
        //触发事件回调
        mTSListenerManager.initFinishedEventListener();
    }

    public void setInitialization(boolean initialization) {
        this.initialization = initialization;
    }


    public void setRunTimeStatusInfo(TSRunTimeStatusInfo runTimeStatusInfo){
        this.runTimeStatusInfo = runTimeStatusInfo;
    }

    public TSRunTimeStatusInfo getRunTimeStatusInfo(){
        return this.runTimeStatusInfo;
    }

    /**
     * 获取常规模式工作频点
     */
    private void resetDMWorkFrequency() {

    }

    /**
     * 获取所有坐标
     * @return
     */
    public RF_GPSInfo[] getGPSAll() {
        RF_GPSInfo[] list = null;
        synchronized (gpsList) {
            list = gpsList.values().toArray(new RF_GPSInfo[0]);
        }
        return list;
    }

    /**
     * 移除GPS
     * @param info
     */
    public void removeGPS(RF_GPSInfo info){
        if (info.getLatitude() > 0 && info.getLontitude() > 0) {
            synchronized (gpsList) {
                //替换新的坐标
                if (gpsList.containsKey(info.getRcvDeviceID())) {
                    gpsList.remove(info.getRcvDeviceID());
                }
            }
        }
    }

    /**
     * 是否SOS设备
     * @param deviceId
     * @return
     */
    public boolean isSOSAlarmDevice(int deviceId) {
        boolean isSos = false;
        synchronized (sosList) {
            if (sosList.contains(deviceId)) {
                isSos = true;
            }
        }
        return isSos;
    }


    /**
     * 根据设备ID获取GPS
     * @param deviceId
     * @return
     */
    public RF_GPSInfo getGPSByDeviceId(int deviceId) {
        RF_GPSInfo gps = null;
        synchronized (gpsList) {
            if (gpsList.containsKey(deviceId)) {
                gps = gpsList.get(deviceId);
            }
        }
        return gps;
    }


    private void log(String log){
        LogUtils.d(this, log);
    }


}
