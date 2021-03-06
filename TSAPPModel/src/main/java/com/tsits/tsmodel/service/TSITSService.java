package com.tsits.tsmodel.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.impl.IRFModelEvent;
import com.impl.poc.IPocCallBack;
import com.impl.poc.Node;
import com.impl.poc.PocCallType;
import com.impl.poc.PocResolution;
import com.impl.poc.PocSipMsg;
import com.impl.poc.PocVersionUpdateEvent;
import com.impl.struct.CallModeEnum;
import com.impl.struct.RF_CallStatusUpdate;
import com.impl.struct.RF_DynamicGroup;
import com.impl.struct.RF_FrequencyInfo;
import com.impl.struct.RF_GPSInfo;
import com.impl.struct.RF_MessageInfo;
import com.impl.struct.RF_MessageInfo_Simple;
import com.impl.struct.RF_SOSInfo;
import com.impl.struct.RF_TrunkingStateUpdate;
import com.impl.struct.TSRunTimeStatusInfo;
import com.tsits.tsmodel.R;
import com.tsits.tsmodel.TSITSApplication;
import com.tsits.tsmodel.utils.CallStatusDefine;
import com.tsits.tsmodel.utils.LogUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.tsits.tsmodel.service.TSCoreCallbackName.*;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLCAUSE_DISC_CALL_END;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLCAUSE_DISC_CALL_REFUSE;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CALLEND;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CALLIDLE;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CALLING;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CALLWating;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CRATESESSION;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_DESTROYSESSION;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_IDLE;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_LocalRX;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_LocalTX;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_RINGING;

public class TSITSService extends Service implements IRFModelEvent, IPocCallBack {

    //Application??????
    private TSITSApplication mTSApplication;
    //????????????????????????
    public boolean serviceIsLive = false;
    //??????????????????ID
    private static final int NOTIFICATION_ID = 1000;

    private final String CallAcitivty_PackageName = "com.escom.talkapp";
    private final String CallAcitivty_ClassName = "com.escom.talkapp.MainActivity";

    private Timer timer;

    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(this, "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(this, "onStartCommand");
        if (!serviceIsLive) {
            //  this.serviceIsLive = true;
            //??????????????? ??????IPC??????
            mTSApplication = (TSITSApplication) getApplication();
            mTSApplication.setServiceStarted(this, 500);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // ??????????????????
                Notification notification = createForegroundNotification();
                //??????????????????????????? ,NOTIFICATION_ID???????????????????????????ID
                startForeground(NOTIFICATION_ID, notification);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.serviceIsLive = false;
        // ????????????
        stopForeground(true);
        super.onDestroy();
    }

    /**
     * ??????????????????
     *
     * @return
     */
    private Notification createForegroundNotification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // ????????????????????????id.
        String notificationChannelId = "notification_channel_id_01";

        //??????NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //???????????????????????????
            String channelName = "??????????????????";
            //?????????????????????
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
//        //????????????
//        builder.setContentTitle("ContentTitle");
//        //????????????
//        builder.setContentText("ContentText");
        //???????????????????????????
        builder.setWhen(System.currentTimeMillis());

        //?????????????????????
        return builder.build();
    }


    @Override
    public void onRFModel_DeviceRegisterStatus(int result) {
        Log.d("TSITSService", "onRFModel_DeviceRegisterStatus: ");
    }

    @Override
    public void onRFModel_DeviceInitStatus(int result) {
        Log.d("TSITSService", "onRFModel_DeviceInitStatus: ");
    }

    @Override
    public void onRFModel_AlarmStatus(int alarmType, short alarmParam) {
        LogUtils.d(this, "onRFModel_AlarmStatus Call(" + alarmType + "," + alarmParam + ")");
    }

    @Override
    public void onRFModel_RxRSSI(byte RSSI) {
        Log.d("onRFModel_RxRSSI", "onRFModel_RxRSSI: ");
    }

    @Override
    public void onRFModel_ReceiveMessage(RF_MessageInfo rf_messageInfo) {
        Log.d("TSITSService", "onRFModel_ReceiveMessage: ");
    }

    @Override
    public void onRFModel_ReceiveMsgUserStatus(RF_MessageInfo_Simple rf_messageInfo) {
        Log.d("TSITSService", "onRFModel_ReceiveMsgUserStatus: ");
    }


    @Override
    public void onRFModel_ReceiveStateSOS(RF_SOSInfo rf_sosInfo) {
        Log.d("TSITSService", "onRFModel_ReceiveStateSOS: ");
    }

    @Override
    public void onRFModel_DeviceKillState(short Result) {
        Log.d("TSITSService", "onRFModel_DeviceKillState: ");
    }

    @Override
    public void onRFModel_ReceiveGPSUpdate(RF_GPSInfo rf_gpsInfo) {
        Log.d("TSITSService", "onRFModel_ReceiveGPSUpdate: ");
    }

    @Override
    public void onRFModel_DynamicGroupUpdate(RF_DynamicGroup rf_dynamicGroup) {
        Log.d("TSITSService", "onRFModel_DynamicGroupUpdate: ");
    }

    @Override
    public void onRFModel_TrunkingStateUpdate(RF_TrunkingStateUpdate rf_trunkingStateUpdate) {
        ServiceData.get().TrunkingStatue.postValue(rf_trunkingStateUpdate);
        Log.d("TSITSService", "onRFModel_TrunkingStateUpdate: ");
    }

    @Override
    public void onRFModel_TrunkingCurrentGroupChaneged(short GroupID, short Result, long groupNumber) {
        Log.d("TSITSService", "onRFModel_TrunkingCurrentGroupChaneged: " + Result);
        {
            if (Result == 1) {
                //???-- ???????????????
                //0 -- ??????
                ServiceData.get().DefaultCurrectChannelIndex.postValue(-1);
            } else {
                ServiceData.get().DefaultCurrectChannelIndex.postValue((int) GroupID);
            }
//            Intent _Param = new Intent();
//            _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
//            _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE);
//            if(Result==1) {
//                _Param.putExtra(TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE_PARA, GroupID);
//            }else{
//                _Param.putExtra(TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE_PARA, -1);
//            }
//            _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //   getApplication().startActivity(_Param);
        }
    }

    private void CreateCallTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Integer timeValue = ServiceData.get().SessionCalltime.getValue();
                    Log.d("TAG", "run timer: " + timeValue++);
                    ServiceData.get().SessionCalltime.postValue(timeValue);
                }
            }, 0, 1000);
        } else {
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Integer timeValue = ServiceData.get().SessionCalltime.getValue();
                    Log.d("TAG", "run timer: " + timeValue++);
                    ServiceData.get().SessionCalltime.postValue(timeValue);
                }
            }, 0, 1000);
        }
    }

    @Override
    public void onRFModel_CallStatusUpdate(RF_CallStatusUpdate rf_callStatusUpdate) {
        Log.d("TSITSService", "onRFModel_CallStatusUpdate: type--" + rf_callStatusUpdate.getCallType()
                + ",statue--" + rf_callStatusUpdate.getCallStatus() + ",cause--" + rf_callStatusUpdate.getCallCause());
        // Log.d("TSITSService", "onRFModel_CallStatusUpdate: type--"+rf_callStatusUpdate.toString());

        TSRunTimeStatusInfo _runtimeinfo = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
        Log.d("TSITSService", "_runtimeinfo.getWorkType" + _runtimeinfo.getWorkType());

        if (_runtimeinfo.getWorkType() == 3 || _runtimeinfo.getWorkType() == 4) {  //
        } else {
            mHandler.post(() -> {
                ServiceData.get().CallStatueInfo.setValue(rf_callStatusUpdate);
                // if(isDown.getCallStatus() ==TS_CALLSTATUE_LOCALTXBEGIN)
                switch (rf_callStatusUpdate.getCallStatus()) {
                    case CALLSTATUE_CRATESESSION: {
                        Log.d("CALLSTATUE_CRATESESSION", "onRFModel_CallStatusUpdate: type--" + rf_callStatusUpdate.getCallType() +
                                ",statue--" + rf_callStatusUpdate.getCallStatus() + ",cause--" + rf_callStatusUpdate.getCallCause());
                        CreateCallTimer();
                        {
                            Intent _Param = new Intent();
                            _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
                            _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE);
                            _Param.putExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA, rf_callStatusUpdate);
                            _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(_Param);
                        }
                    }
                    break;
                    case CALLSTATUE_DESTROYSESSION: {
                        Log.d("CALLSTATUE_DESTROYSESSION", "onRFModel_CallStatusUpdate: type--" + rf_callStatusUpdate.getCallType() +
                                ",statue--" + rf_callStatusUpdate.getCallStatus() + ",cause--" + rf_callStatusUpdate.getCallCause());
                        if (timer != null) {
                            ServiceData.get().SessionCalltime.postValue(0);
                            timer.cancel();
                            timer.purge();  //????????????
                            timer = null;
                            {
                                Intent _Param = new Intent();
                                _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
                                _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE);
                                _Param.putExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA, rf_callStatusUpdate);
                                _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplication().startActivity(_Param);
                            }
                        }
                    }
                    break;
//            default:{
//              //  CreateCallTimer();
//                {
//                    Intent _Param =new Intent();
//                    _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
//                    _Param.putExtra(TS_CORESERVICE_EVENT,TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE);
//                    _Param.putExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA,rf_callStatusUpdate);
//                    _Param.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                    getApplication().startActivity(_Param);
//                }
//            }
                }
            });
        }

    }

    @Override
    public void onRFModel_FrequencyUpdate(List<RF_FrequencyInfo> frequencyList) {
        Log.d("TSITSService", "onRFModel_FrequencyUpdate: ");
    }

    @Override
    public void onRFModel_HandSelectedBaseStation(short result) {
        Log.d("TSITSService", "onRFModel_HandSelectedBaseStation: ");
    }

    @Override
    public void onRFModel_TrunkingStateCreate(boolean status, int CallerID, int CallID, int CallType) {
        Log.d("TSITSService", "onRFModel_TrunkingStateCreate: ");
    }


    @Override
    public void onRFModel_SessionUnlock() {
        Log.d("TSITSService", "onRFModel_SessionUnlock: ");
    }

    @Override
    public void onRFPttCallback(boolean isDown, int workType) {
        Log.d("TSITSService", "onRFPttCallback: " + isDown);
        if (isDown) { //PTT Down
            //check is in poc call session
            if ((ServiceData.get().CurrectPocSessionID.getValue() < 1) && (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CLLMODE_POC)) {
                //if not in call session ,then send a poc call request
                TSRunTimeStatusInfo _runtimeinfo = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
                Log.d("TSITSService isDown", "_runtimeinfo " + _runtimeinfo);
                int PocSessionID = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetVoiceFullCall(_runtimeinfo.getPocGroupId(), false);
                Log.d("TSITSService isDown", "CurrectPocSessionID " + ServiceData.get().CurrectPocSessionID.getValue());
                Log.d("TSITSService isDown", "getPocGroupId " + _runtimeinfo.getPocGroupId());

                //request call success ,then send ptton
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (PocSessionID > 0) {
                    Log.d("TSITSService isDown", "PocSessionID > 0 " + PocSessionID);
                    ServiceData.get().CurrectPocSessionID.setValue(PocSessionID);
                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOn(ServiceData.get().CurrectPocSessionID.getValue(), false);
                } else {
                    Log.d("TSITSService isDown", "PocSessionID == -1 " + PocSessionID);
                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOn(ServiceData.get().CurrectPocSessionID.getValue(), false);
                }
            } else {
                Log.d("TSITSService isDown", "ServiceData.get().CurrectPocSessionID.getValue() ELSE" + ServiceData.get().CurrectPocSessionID.getValue());
                mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOn(ServiceData.get().CurrectPocSessionID.getValue(), false);
            }
        } else {   //PTT Up
//            mHandler.post(() -> {
            Log.d("TSITSService isDown", "TSITSService isDown false");
            if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CLLMODE_POC) {
//                    RF_CallStatusUpdate CallInfo=ServiceData.get().CallStatueInfo.getValue();
//                    RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CALLIDLE, (short) 0, (short) 1, (short) 0, (short) 1,
//                            Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
//                            Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getDestID())),
//                            Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
//                            new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
//                    CallInfo.setCallType((short) ServiceData.get().CallStatueInfo.getValue().getCallType());
//                    CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
//                    Intent _Param = new Intent();
//                    _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
//                    _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
//                    _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
//                    _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getApplication().startActivity(_Param);
            }
            mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOff(ServiceData.get().CurrectPocSessionID.getValue());
        }
    }


    public void OnCoreService_Connect() {
        LogUtils.d(this, "OnCoreService_Connect Call");
        serviceIsLive = true;
    }

    public void OnCoreService_Disconnect() {
        Log.d("TSITSService", "OnCoreService_Disconnect: ");
        serviceIsLive = false;
    }

    //region POC CallBack
    @Override
    public void onInitFail(int code, String reason) {
        Log.d("TSITSService", "POC ---  onInitFail: ");
    }

    @Override
    public void onInitSuc() {
        Log.d("TSITSService", "POC ---  onInitSuc: ");
    }

    @Override
    public void onError(int errorCode) {
        Log.d("TSITSService", "POC ---  onError: ");
    }

    @Override
    public void onAVChatServiceConsultVideoInfo(PocResolution result) {
        Log.d("TSITSService", "POC ---  onAVChatServiceConsultVideoInfo: ");
        //??????App??????POC???????????????????????????????????????????????????Intent??????????????????App
    }


    @Override
    public void onAVChatServiceNotifyCallFail(PocSipMsg pocSipMsg) {
        Log.d("TSITSService", "POC ---  onAVChatServiceNotifyCallFail: ");
        Log.d("TSITSService", "pocSipMsg.getCallType() CallFail: " + pocSipMsg.getCallType());

        if (pocSipMsg.getCallType() == 9) {

        } else {
            RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CALLEND, (short) 1, (short) 1, (short) 0, (short) 1,
                    Long.parseLong(pocSipMsg.getCallTel()), Long.parseLong(pocSipMsg.getCalledTel()), Long.parseLong(pocSipMsg.getCallTel()),
                    new byte[1], new byte[1], new byte[1], CALLCAUSE_DISC_CALL_REFUSE, 0, 0, 0);
            CallInfo.setCallType((short) pocSipMsg.getCallType());
            ServiceData.get().CurrectPocSessionID.setValue(0);
            CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
            {
                Intent _Param = new Intent();
                _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
                _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
                _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
                _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(_Param);
            }
        }
    }

    @Override
    public void onAVChatServiceNotifyCallHangupEvent(PocSipMsg pocSipMsg) {
        Log.d("TSITSService", "POC ---  onAVChatServiceNotifyCallHangupEvent: ");
        Log.d("TSITSService", "pocSipMsg.getCallType() CallHangupEvent: " + pocSipMsg.getCallType());
        if (timer != null) {
            ServiceData.get().SessionCalltime.postValue(0);
            ServiceData.get().CurrectPocSessionID.setValue(0);
            timer.cancel();
            timer.purge();  //????????????
            RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CALLEND, (short) 1, (short) 1, (short) 0, (short) 1,
                    Long.parseLong(pocSipMsg.getCallTel()), Long.parseLong(pocSipMsg.getCalledTel()), Long.parseLong(pocSipMsg.getCallTel()),
                    new byte[1], new byte[1], new byte[1], CALLCAUSE_DISC_CALL_END, 0, 0, 0);
            CallInfo.setCallType((short) pocSipMsg.getCallType());
            CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
            timer = null;

            Intent _Param = new Intent();
            _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
            _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
            _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
            _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(_Param);
        }
    }

    @Override
    public void onAVChatServiceNotifyCallInSuc(PocSipMsg pocSipMsg) {
        Log.d("TSITSService", "POC ---  onAVChatServiceNotifyCallInSuc: ");
        Log.d("TSITSService", "pocSipMsg.getCallType() CallInSuc: " + pocSipMsg.getCallType());
        if (pocSipMsg.getCallType() != 9) {
            RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CRATESESSION, (short) 1, (short) 1, (short) 0, (short) 1,
                    Long.parseLong(pocSipMsg.getCallTel()), Long.parseLong(pocSipMsg.getCalledTel()), Long.parseLong(pocSipMsg.getCallTel()),
                    new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
            CallInfo.setCallType((short) pocSipMsg.getCallType());
            CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
            ServiceData.get().CurrectPocSessionID.setValue(1);
            CreateCallTimer();
            {
                Intent _Param = new Intent();
                _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
                _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
                _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
                _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(_Param);
            }
        } else {
            Log.d("TSITSService", "POC ---  onAVChatServiceNotifyCallInSuc ELSE: ");
        }
    }

    @Override
    public void onAVChatServiceNotifyCallOutSuc(PocSipMsg pocSipMsg) {
        Log.d("TSITSService", "POC ---  onAVChatServiceNotifyCallOutSuc: ");
        Log.d("TSITSService", "pocSipMsg.getCallType() CallOutSuc: " + pocSipMsg.getCallType());
        if (pocSipMsg.getCallType() != 9) {
            RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CRATESESSION, (short) 1, (short) 1, (short) 0, (short) 1,
                    Long.parseLong(pocSipMsg.getCallTel()), Long.parseLong(pocSipMsg.getCalledTel()), Long.parseLong(pocSipMsg.getCallTel()),
                    new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
            CallInfo.setCallType((short) pocSipMsg.getCallType());
            CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
            CreateCallTimer();

            Intent _Param = new Intent();
            _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
            _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
            _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
            _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(_Param);
        } else {
            Log.d("TSITSService", "POC ---  onAVChatServiceNotifyCallOutSuc ELSE: ");

        }
    }

    @Override
    public void onAVChatServiceNotifyRecCallInEvent(PocSipMsg pocSipMsg) {//??????????????????????????????????????????????????????
        Log.d("TSITSService", "POC ---  onAVChatServiceNotifyRecCallInEvent: ");
        Log.d("TSITSService", "pocSipMsg.getCallType() CallInEvent: " + pocSipMsg.getCallType());
        ((TSITSApplication) getApplication()).getCoreService().getICoreServiceEvent().onRFAudio_SetMainInterfaceType(2);//???????????????????????????????????????
        //onRFAudio_SetMainInterfaceType(2)??????????????????????????????PTT??????POC?????????onRFAudio_SetMainInterfaceType(1)?????????????????????POC???????????????PTT???PDT?????????
        if (pocSipMsg.getCallType() == 9) { //???????????????9

        } else if (pocSipMsg.getCallType() == 3) { //?????????3
            RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_RINGING, (short) 0, (short) 1, (short) 0, (short) 1,
                    Long.parseLong(pocSipMsg.getCallTel()), Long.parseLong(pocSipMsg.getCalledTel()), Long.parseLong(pocSipMsg.getCallTel()),
                    new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
            CallInfo.setCallType((short) pocSipMsg.getCallType());
            CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);

            Intent _Param = new Intent();
            _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
            _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
            _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
            _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(_Param);

        } else if (pocSipMsg.getCallType() == 2) { //?????????2
            RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CALLIDLE, (short) 0, (short) 1, (short) 0, (short) 1,
                    Long.parseLong(pocSipMsg.getCallTel()), Long.parseLong(pocSipMsg.getCalledTel()), Long.parseLong(pocSipMsg.getCallTel()),
                    new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
            CallInfo.setCallType((short) pocSipMsg.getCallType());
            CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);

            Intent _Param = new Intent();
            _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
            _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
            _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
            _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(_Param);
        }
    }

    @Override
    public void onSystemMsgNotifyLoginAtOtherDevice() {
        Log.d("TSITSService", "POC ---  onSystemMsgNotifyLoginAtOtherDevice: ");

    }

    @Override
    public void onSystemMsgNotifyUpdateInfo(PocVersionUpdateEvent updateInfo) {
        Log.d("TSITSService", "POC ---  onSystemMsgNotifyUpdateInfo: ");
    }

    @Override
    public void requestSpeakAuthFailed() {
        Log.d("TSITSService", "POC ---  requestSpeakAuthFailed: ");
    }

    @Override
    public void requestSpeakAuthSuc() {
        Log.d("TSITSService", "POC ---  requestSpeakAuthSuc: ");
        RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CRATESESSION, (short) 0, (short) 1, (short) 0, (short) 1,
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getDestID())),
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
                new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
        CallInfo.setCallType((short) ServiceData.get().CallStatueInfo.getValue().getCallType());
        CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
        Intent _Param = new Intent();
        _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
        _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
        _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
        _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(_Param);
    }

    @Override
    public void speakAuthIdle() {
        Log.d("TSITSService", "POC ---  speakAuthIdle: ");
        RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CALLIDLE, (short) 0, (short) 1, (short) 0, (short) 1,
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getDestID())),
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
                new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
        CallInfo.setCallType((short) ServiceData.get().CallStatueInfo.getValue().getCallType());
        CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
        Intent _Param = new Intent();
        _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
        _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
        _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
        _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(_Param);
    }

    @Override
    public void speakAuthTakenBySomeOne(String tel) {
        Log.d("TSITSService", "POC ---  speakAuthTakenBySomeOne: ");
        RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_IDLE, (short) 0, (short) 1, (short) 0, (short) 1,
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getDestID())),
                Long.parseLong(String.valueOf(ServiceData.get().CallStatueInfo.getValue().getSrcID())),
                new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
        CallInfo.setCallType((short) ServiceData.get().CallStatueInfo.getValue().getCallType());
        CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
        CallInfo.setTalkID(Long.parseLong(tel));
        Intent _Param = new Intent();
        _Param.setComponent(new ComponentName(CallAcitivty_PackageName, CallAcitivty_ClassName));
        _Param.putExtra(TS_CORESERVICE_EVENT, TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE);
        _Param.putExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, CallInfo);
        _Param.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(_Param);
    }


    //endregion
}
