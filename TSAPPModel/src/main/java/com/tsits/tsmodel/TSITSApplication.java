package com.tsits.tsmodel;

import android.app.Application;

//import com.baidu.mapapi.CoordType;
//import com.baidu.mapapi.SDKInitializer;
//import com.baidu.mapapi.map.MapView;
//import com.impl.IRFModelEvent;
import com.tsits.tsmodel.service.CoreServiceClient;
import com.tsits.tsmodel.service.TSITSService;
import com.tsits.tsmodel.utils.LogUtils;
import com.tsits.tsmodel.utils.TSAudioManager;

public class TSITSApplication extends Application {
    private TSITSService mTSService;
    private CoreServiceClient mCoreServiceClient;
    //private IRFModelEvent mRFModelEvent;
    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
       // SDKInitializer.initialize(this);
        //SDKInitializer.setCoordType(CoordType.BD09LL);


    }

    public CoreServiceClient getCoreService()
    {
        return mCoreServiceClient;
    }
    /**
     * 开启服务
     */
    public boolean setServiceStarted(TSITSService callbackService,int Timeout) {
        this.mTSService = callbackService;
        if (mCoreServiceClient != null) {
            mCoreServiceClient.disconnect();
            mCoreServiceClient=null;
        }
        mCoreServiceClient = new CoreServiceClient(this,mTSService);
        boolean _retValue= mCoreServiceClient.connect();
        LogUtils.d(this, "连接IPC服务:"+mCoreServiceClient.isConnected());
        return _retValue;
    }

}

