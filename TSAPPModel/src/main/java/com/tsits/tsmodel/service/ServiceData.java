package com.tsits.tsmodel.service;

import androidx.lifecycle.MutableLiveData;

import com.impl.struct.CallModeEnum;
import com.impl.struct.RF_CallStatusUpdate;
import com.impl.struct.RF_TrunkingStateUpdate;

public class ServiceData {
    private static ServiceData instance = null;
    public MutableLiveData<RF_TrunkingStateUpdate> TrunkingStatue =new MutableLiveData<RF_TrunkingStateUpdate>();
    public MutableLiveData<RF_CallStatusUpdate> CallStatueInfo =new MutableLiveData<RF_CallStatusUpdate>();
    public MutableLiveData<Integer> SessionCalltime = new MutableLiveData<>(0);
    public MutableLiveData<Integer> DefaultCurrectChannelIndex =new MutableLiveData<>(0);
    public MutableLiveData<CallModeEnum> CurrectCallMode =new MutableLiveData<>(CallModeEnum.TS_CALLMODE_RF);
    public MutableLiveData<Integer> CurrectPocSessionID =new MutableLiveData<>(0);
    private ServiceData() {

    }

    public static ServiceData get() {
        synchronized (ServiceData.class) {
            if (instance == null) {
                synchronized (ServiceData.class) {
                    instance = new ServiceData();
                }
            }
        }
        return instance;
    }
}
