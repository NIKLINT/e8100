package com.tsits.tsmodel.listener;


import com.impl.struct.RF_MessageInfo;

/**
 * Created by YY on 2020/8/20.
 * 收到短信
 */
public interface IReceiveMessageListener {

    /**
     * 收到短消息
     * @param msg
     */
    void receiveMessage(RF_MessageInfo msg);

}
