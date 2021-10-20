package com.tsits.tsmodel.listener;


import com.impl.struct.RF_CallStatusUpdate;

/**
 * 服务数据变更监听
 */
public interface IDataChangedListener {

    /**
     * 初始化完成
     */
    void initFinished();

//    /**
//     * 发射状态变更
//     * @param isBusy
//     */
//    void txBusyUpdate(boolean isBusy);
//
//    /**
//     * 呼叫状态变更
//     * @param callUpState
//     */
//    void callUpStatusChanged(RF_CallStatusUpdate callUpState);
//
//    /**
//     * 会话创建
//     * @param callUpState
//     */
//    void trunkingStateCreate(RF_CallStatusUpdate callUpState);
//
//    /**
//     * 模块断开呼叫状态
//     */
//    void deviceCallRelease();
//
//    /**
//     * 会话结束
//     * @param trunkingTalk
//     */
//    void trunkingStateRelease(RF_CallStatusUpdate trunkingTalk);



}
