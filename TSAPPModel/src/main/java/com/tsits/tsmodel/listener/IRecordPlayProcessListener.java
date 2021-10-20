package com.tsits.tsmodel.listener;

/**
 * Created by YY on 2020/8/20
 * 录音播放进度监听
 */
public interface IRecordPlayProcessListener {
    void recordPlayProcess(int processValue, boolean playState);
}
