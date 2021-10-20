package com.tsits.tsmodel.service;


/**
 * @author： YY
 * @date： 2020/8/12
 * 运行状态
 */
public enum TSRunningStatus {
    /**
     * 正在连接核心服务
     */
    CONNECTING,
    /**
     * 连接成功
     */
    CONNECTED,
    /**
     * 注册服务回调
     */
    REGISTER,
    /**
     * 注册服务回调成功
     */
    REGISTERED,
    /**
     * 正在初始化数据
     */
    INITIALIZE,
    /**
     * 初始化数据完成
     */
    INITIALIZED,
    /**
     * 运行状态重置
     */
    RUNNING_STATUS_RESET,
    /**
     * 配置数据重置
     */
    CONFIG_DATA_RESET,
    /**
     * 正在正常运行状态
     */
    RUNNING,
    /**
     * 已经断开连接
     */
    DISCONNECTED,
    /**
     * 应用断开
     */
    EXIT,
}
