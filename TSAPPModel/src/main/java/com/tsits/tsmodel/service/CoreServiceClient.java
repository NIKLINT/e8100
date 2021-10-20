package com.tsits.tsmodel.service;


import com.impl.ICoreClientCallback;
import com.impl.IRFModelCallback;
import com.impl.IRFModelEvent;
import com.impl.poc.IPocCallBack;
import com.impl.poc.Node;
import com.impl.struct.TSRunTimeStatusInfo;
import com.tsits.tsmodel.enity.TSITSConfig;
import com.tsits.tsmodel.TSITSApplication;
import com.tsits.tsmodel.manager.ServiceDataManager;
import com.tsits.tsmodel.utils.LogUtils;
import com.xuexiang.xipc.XIPC;
import com.xuexiang.xipc.core.channel.IPCListener;
import com.xuexiang.xipc.core.channel.IPCService;

public class CoreServiceClient implements Runnable {

    /**
     * 应用实例
     */
    private TSITSApplication mApplication;

    private Object _watiObject=new Object();
    /**
     * 当前服务运行状态
     * 默认-正在连接
     */
    private TSRunningStatus mRunningStatus = TSRunningStatus.CONNECTING;

    /**
     * 对象锁
     * */
    private Object lock = new Object();

    /**
     * 运行数据实例
     */
    private  TSRunTimeStatusInfo runTimeStatusInfo =null;

    /**
     * 配置数据
     */
    private TSITSConfig mTSITSConfig;

    /**
     * 调用服务接口
     */
    private ICoreClientCallback mICoreClientCallback;

    private IRFModelCallback mIRFModelCallback;

    private IPocCallBack mPocEventCallback;
    /**
     * 启动服务线程
     * */
    private Thread runningCheckThread = null;

    /**
     * 绑定服务包名
     */
    private static final String pkgName = "com.tsits.coreservice";

    /**
     * 注册接口
     */
    private static final String implPkgName = "com.impl";

    private TSITSService _ServiceClient =null;

    public CoreServiceClient(TSITSApplication application,TSITSService serviceclient){
        this.mApplication = application;
        this._ServiceClient =serviceclient;
    }

    public TSRunTimeStatusInfo GetRunningStatue()
    {
        return runTimeStatusInfo;
    }

    /**
     * 连接服务
     */
    public boolean connect(){

        //连接服务得监听事件
        XIPC.setIPCListener(new IPCListener() {
            @Override
            public void onIPCConnected(Class<? extends IPCService> service) {
                LogUtils.d(this, "onIPCConnected");
//                synchronized (_watiObject) {
//                    _watiObject.notify();
//                }
                mRunningStatus = mRunningStatus.CONNECTED;
                try {
                    mIRFModelCallback = XIPC.getService(IRFModelCallback.class);
                    mICoreClientCallback = XIPC.getService(ICoreClientCallback.class);
                }catch (Exception e){
                    mIRFModelCallback=null;
                    mICoreClientCallback=null;
                }
               if(mIRFModelCallback!=null) {
                   _ServiceClient.OnCoreService_Connect();
                   mIRFModelCallback.setIRFModelCallback(_ServiceClient);
                   mIRFModelCallback.setIPocCallBack(_ServiceClient);
                   //服务连接成功,启动线程获取相应数据
                   if (runningCheckThread == null || !runningCheckThread.isAlive()) {
                       //启动服务状态自检线程
                       runningCheckThread = new Thread(CoreServiceClient.this::run);
                       runningCheckThread.start();
                       LogUtils.d(this, "启动服务线程");
                   }
               }
            }

            @Override
            public void onIPCDisconnected(Class<? extends IPCService> service) {
                super.onIPCDisconnected(service);
                _ServiceClient.OnCoreService_Disconnect();
                LogUtils.d(this, "onIPCDisconnected");
                //非正常断开
                if (mRunningStatus != mRunningStatus.EXIT){
                    mRunningStatus = mRunningStatus.DISCONNECTED;
                }
            }
        });
        //跨进程连接
        XIPC.connectApp(mApplication, pkgName);
        //注册包名下的所有定义的服务接口
        //XIPC.register(implPkgName);
        XIPC.register(ICoreClientCallback.class);
        XIPC.register(IRFModelCallback.class);
        XIPC.register(IRFModelEvent.class);
        XIPC.register(IPocCallBack.class);

//       synchronized (_watiObject) {
//            try {
//                _watiObject.wait(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        return XIPC.isConnected();
    }

    /**
     * 断开服务连接
     * ps：退出应用
     */
    public void disconnect(){
        if (isConnected() && mApplication != null) {
            //设置为退出
            mRunningStatus = mRunningStatus.EXIT;
            try {
                XIPC.disconnect(mApplication);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (runningCheckThread != null){
                runningCheckThread.interrupt();
                runningCheckThread = null;
            }
        }
    }

    /**
     * 返回服务连接状态
     * @return
     */
    public boolean isConnected(){
        return XIPC.isConnected();
    }

    /**
     * 服务接口
     * @return
     */
    public ICoreClientCallback getICoreServiceEvent(){

        return mICoreClientCallback;
    }


    @Override
    public void run() {
        while (mRunningStatus != mRunningStatus.EXIT) {
            switch (mRunningStatus) {
                case CONNECTING:
                    LogUtils.d(this, "正在连接状态");
                    break;
                case CONNECTED:
                    LogUtils.d(this, "已连接状态");
                    //获取配置数据
                    getTSConfigData();
//                    try {
//                        synchronized (lock) {
//                            lock.wait(1000);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    break;
                case INITIALIZED:
                    LogUtils.d(this, "初始化数据完成状态");
                    getStatus();
//                    try {
//                        synchronized (lock) {
//                            lock.wait(1000);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    break;
                case CONFIG_DATA_RESET:
                    LogUtils.d(this, "重新获取初始化数据");
                    getTSConfigData();
                    try {
                        synchronized (lock) {
                            lock.wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case RUNNING_STATUS_RESET:
                    LogUtils.d(this,"重新当前运行状态数据");
                    getStatus();
                    try {
                        synchronized (lock) {
                            lock.wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case RUNNING:
                    LogUtils.d(this, "正在运行");
                    try {
                        //10s打印日志
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case DISCONNECTED:
                    LogUtils.d(this, "断开IPC连接");
                    try {
                        //10s打印日志
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
        }
    }

    /**
     * 获取配置数据
     * @return
     */
    private void getTSConfigData() {
        if (isConnected()) {
//            //获取服务配置数据
//            mTSITSConfig = getICoreServiceEvent().getConfigData();
//
//            if (mTSITSConfig != null) {
//                //初始化配置数据完成
//                mRunningStatus = mRunningStatus.INITIALIZED;
//            } else {
//                mRunningStatus = mRunningStatus.CONNECTED;
//                ServiceDataManager.getInstance().setInitialization(false);
//            }
//
//            if (mRunningStatus == mRunningStatus.CONFIG_DATA_RESET) {
//                mRunningStatus = mRunningStatus.RUNNING_STATUS_RESET;
//            } else {
//                mRunningStatus = mRunningStatus.INITIALIZED;
//            }
            mRunningStatus = mRunningStatus.INITIALIZED;
//            //唤醒
//            synchronized (lock) {
//                lock.notify();
//            }
        }
    }

    /**
     * 获取运行状态
     * @return
     */
    private void getStatus() {
        if (isConnected()) {
            //获取运行状态数据
            try {
                runTimeStatusInfo = mICoreClientCallback.onAppModel_GetRunningStatus();
            }catch (Exception e)
            {
                e.printStackTrace();
                runTimeStatusInfo=null;
            }
            if (runTimeStatusInfo != null){
                mRunningStatus = mRunningStatus.RUNNING;
                ServiceDataManager.getInstance().coreServiceInitFinished(true, mTSITSConfig, runTimeStatusInfo);
            }else{
             //   mRunningStatus = mRunningStatus.INITIALIZED;
              //  ServiceDataManager.getInstance().setInitialization(false);
            }
        }
    }


}
