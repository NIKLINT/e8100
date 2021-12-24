package com.escom.talkapp.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.escom.talkapp.MainActivity;
import com.escom.talkapp.R;
import com.impl.ICoreClientCallback;
import com.impl.poc.PocSipMsg;
import com.impl.struct.RF_CallStatusUpdate;
import com.impl.struct.CallModeEnum;
import com.tsits.tsmodel.service.ServiceData;
import com.tsits.tsmodel.utils.CallStatusDefine;
import com.tsits.tsmodel.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;

import jaygoo.widget.wlv.WaveLineView;

import static com.impl.struct.CallModeEnum.TS_CLLMODE_POC;
import static com.impl.struct.RF_CallStatusUpdate.CALLTYPE_Boradcast;
import static com.impl.struct.RF_CallStatusUpdate.CALLTYPE_GROUP;
import static com.impl.struct.RF_CallStatusUpdate.CALLTYPE_PSTN_GROUP;
import static com.impl.struct.RF_CallStatusUpdate.CALLTYPE_PSTN_SINGLE;
import static com.impl.struct.RF_CallStatusUpdate.CALLTYPE_SINGLE;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONRFPTTCALLBACK;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CALLEND;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CALLIDLE;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CRATESESSION;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_DESTROYSESSION;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_LocalRX;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_LocalTX;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoiceCallingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class VoiceCallingFragment extends BackHandledFragment implements ITSCallInfoUpdate {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private TextView _txtCallID;
    private TextView _txtCallerID;
    private ImageView _CallStaueImg;
    private View _ContentView;
    private TextView _lblCallTimer;
    private LinearLayout _acivityContent;
    private TextView lblWorkType;
    protected BackHandledInterface mBackHandledInterface = null;
    private WaveLineView mRecordingView;// = findViewById(R.id.audioRecordView)
    private byte _DrawCallAudioView = 0;
    private short _CallTimeCounter = 0;
    private Button _btnCallhangup;
    private Button _btnacceptcall;
    private IntentFilter _KeyFilter;
    private String TAG = "VoiceCallingFragment";


    public VoiceCallingFragment() {
        super();
    }

    private PocSipMsg _PocCallInfo;


    private BroadcastReceiver KeyEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(this.getClass().getName(), "KeyEventReceiver message:" + intent.getAction().toString());
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VoiceCallingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoiceCallingFragment newInstance(String param1, String param2) {
        VoiceCallingFragment fragment = new VoiceCallingFragment();
        return fragment;
    }


    @Override
    public boolean onBackPressed() {
        Log.i(this.toString(), "onKey Back listener is working!!!");
        //   getActivity().finish();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("VoiceCallingFragment", "VoiceCallingFragment is create");

        if (!(getActivity() instanceof BackHandledInterface)) {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        } else {
            this.mBackHandledInterface = (BackHandledInterface) getActivity();
        }
        _KeyFilter = new IntentFilter();
        _KeyFilter.addAction("android.intent.action.HANGUP_TSITS_CALL");
        _KeyFilter.setPriority(Integer.MAX_VALUE);
        getActivity().registerReceiver(KeyEventReceiver, _KeyFilter);

    }

    private void RegistViewEvent() {
        //region Hangup Button Event
        if (_btnCallhangup != null) {
            _btnCallhangup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CALLMODE_RF) {
                        mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(-1);
                        Log.d(TAG, "onAppModel_HangUpCall -1");
                    } else {
                        ICoreClientCallback clientSDK = mTSApplication.getCoreService().getICoreServiceEvent();
                        if (clientSDK != null) {
                            mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(1);
                            Log.d(TAG, "onAppModel_HangUpCall 1");
                            getActivity().finish();
                        }
                    }
                }
            });
        }
        //endregion


        _ContentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                Log.i(this.toString(), "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i(this.toString(), "onKey Back listener is working!!!");
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

        ServiceData.get().CallStatueInfo.observe(getViewLifecycleOwner(), callinfo -> {
            OnCallInfoUpdate(callinfo);
        });

        if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CLLMODE_POC) {
            RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) getArguments().getSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA);
            if (_CallInfo != null) {
                if (_CallInfo.getCallType() == 2) {//POC组呼
                    ServiceData.get().SessionCalltime.observe(getViewLifecycleOwner(), integer -> {
                        String min = String.format("%02d", integer / 60);
                        String sec = String.format("%02d", integer % 60);
                        _lblCallTimer.setText(min + ":" + sec);
                    });
                } else {
                    _lblCallTimer.setText("" + _CallInfo.getDestID());
                }
            }
            ServiceData.get().CallStatueInfo.setValue(_CallInfo);
        } else {
            ServiceData.get().SessionCalltime.observe(getViewLifecycleOwner(), integer -> {
                String min = String.format("%02d", integer / 60);
                String sec = String.format("%02d", integer % 60);
                _lblCallTimer.setText(min + ":" + sec);

            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        _ContentView = inflater.inflate(R.layout.fragment_voice_calling, container, false);
        _CallStaueImg = _ContentView.findViewById(R.id.imgCallType);
        lblWorkType = _ContentView.findViewById(R.id.lblCallType);
        _acivityContent = _ContentView.findViewById(R.id.linearLayout);
        _btnCallhangup = _ContentView.findViewById(R.id.btnCallhangup);
        mRecordingView = _ContentView.findViewById(R.id.audioRecordView);
        {
            //当前使用的呼叫模式是窄带呼叫模式　
        }
        if (getArguments() != null) {
            String CallEvent = getArguments().getString(TS_CORESERVICE_EVENT);
            _txtCallID = _ContentView.findViewById(R.id.lblGroupID);
            _txtCallerID = _ContentView.findViewById(R.id.lblCallFrom);
            _lblCallTimer = _ContentView.findViewById(R.id.lblCallTimer);
            Log.i(this.toString(), "Event Call:" + CallEvent);
            switch (CallEvent) {
                case TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE: {
                    _PocCallInfo = (PocSipMsg) getArguments().getSerializable(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                    ServiceData.get().CurrectCallMode.setValue(CallModeEnum.TS_CLLMODE_POC);
                }
                break;
                case TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA: {
                    ServiceData.get().CurrectCallMode.setValue(CallModeEnum.TS_CLLMODE_POC);
                    _PocCallInfo = (PocSipMsg) getArguments().getSerializable(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                }
                break;
                case TS_CORESERVICE_EVENT_ONRFPTTCALLBACK: {
                    ServiceData.get().CurrectCallMode.setValue(CallModeEnum.TS_CALLMODE_RF);

                }
                break;
                case TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE: {
                    ServiceData.get().CurrectCallMode.setValue(CallModeEnum.TS_CALLMODE_RF);

                    RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) getArguments().getSerializable(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                }
                break;
            }
            if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CALLMODE_RF) {
                switch (_tmpRuntimeInfo.getWorkType()) {
                    case 0: {
                        //PDT　集群
                        lblWorkType.setText(R.string.TS_WorkType_PDTTrunking);
                    }
                    break;
                    case 2: {
                        // PDT常规　
                        lblWorkType.setText(R.string.TS_WorkType_PDTNormal);
                    }
                    break;
                    case 3: {
                        //模拟集群
                        lblWorkType.setText(R.string.TS_WorkType_MPTTrunking);
                    }
                    break;
                    case 4: {
                        //模拟常规　
                        lblWorkType.setText(R.string.TS_WorkType_AnalogNormal);
                    }
                    break;
                }
            } else if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CLLMODE_POC) {
                Log.d("VoiceCallingFragment.getCallType", "" + ServiceData.get().CallStatueInfo.getValue().getCallType());
                if (ServiceData.get().CallStatueInfo.getValue().getCallType() == 3) {
                    lblWorkType.setText(R.string.TS_WorkType_Single_POC);
                } else {
                    lblWorkType.setText(R.string.TS_WorkType_POC);
                }
            }
        }
        RegistViewEvent();


        return _ContentView;
    }


    private void ShowCallType(RF_CallStatusUpdate _CallInfo) {
        if (_CallInfo.getCallMode() == CallModeEnum.TS_CALLMODE_RF) {
            switch (_CallInfo.getCallType()) {
                case CALLTYPE_Boradcast: {
                    _txtCallID.setText(R.string.CALLTYPE_BROADCAST);
                }
                break;
                case CALLTYPE_GROUP: {
                    Log.d(TAG, " CALLTYPE_GROUP");
                    _txtCallID.setText("呼叫组:" + _CallInfo.getTalkID());
                }
                break;
                case CALLTYPE_SINGLE: {
                    Log.d(TAG, " CALLTYPE_SINGLE");
                    Log.d(TAG, " kk._tmpRuntimeInfo.getPdtDeviceID() " + _tmpRuntimeInfo.getPdtDeviceID());
                    Log.d(TAG, " kk._CallInfo.getDestID() " + _CallInfo.getDestID());
                    Log.d(TAG, " kk._CallInfo.getSrcID() " + _CallInfo.getSrcID());
                    Log.d(TAG, " kk.getCallerID() " + mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getCallerID());
                    Log.d(TAG, " kk.getCallID() " + mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getCallID());
                    Log.d(TAG, " kk.getCallType() " + mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getCallType());

//                    if (mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getCallType()==0){
//                        Log.d(TAG, " kk.getCallType() == 0");
                    if (_CallInfo.getDestID() == _tmpRuntimeInfo.getPdtDeviceID()) { //被呼
                        _txtCallID.setText("单呼者:" + _CallInfo.getSrcID());
                    } else if (_CallInfo.getSrcID() == _tmpRuntimeInfo.getPdtDeviceID()) {  //主呼
                        _txtCallID.setText("单呼者:" + _CallInfo.getDestID());
                    } else {
                    }
//                }else{
//                        Log.d(TAG, " kk.getCallType() != 0");
//                    }
                }
                break;
                case CALLTYPE_PSTN_SINGLE:
                case CALLTYPE_PSTN_GROUP: {
                    String PSTNNum = "";
//                    try {
//                        PSTNNum = new String(_CallInfo.getPSTNAddr(), "utf8").trim();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
                    PSTNNum = bcd2str(_CallInfo.getPSTNAddr());
                    _txtCallID.setText("PSTN号码:" + PSTNNum);
                    _txtCallID.setSelected(true);
                }
                break;
            }
        } else if (_CallInfo.getCallMode() == CallModeEnum.TS_CLLMODE_POC) {
            if (_CallInfo.getCallType() == 2) {
                _txtCallID.setText("呼叫组:" + mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocGroupId());
                if (mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId()
                        .equals(ServiceData.get().CallStatueInfo.getValue().getDestID())) {
                    _txtCallerID.setText("" + ServiceData.get().CallStatueInfo.getValue().getSrcID());
                }
                ServiceData.get().CurrectPocSessionID.getValue();
            } else {
                _txtCallID.setText(R.string.CALLTYPE_POCCALL_SINGLE);
            }

        }
    }


    private void ShowCallInfo(RF_CallStatusUpdate _CallInfo) {
        LogUtils.d(this, "VoiceCallingFragment-ShowCallInfo-call-" + _CallInfo.toString());
        _txtCallerID.setText("");
        _txtCallID.setText("");
        _txtCallID.setSelected(false);
        switch ((byte) _CallInfo.getCallStatus()) {
            case CallStatusDefine.CALLSTATUE_IDLE: {
                String getPocDeviceId = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId();
                String getDestID = (new Long(_CallInfo.getDestID())).toString();
                String getSrcID = (new Long(_CallInfo.getSrcID())).toString();
                String getTalkID = (new Long(_CallInfo.getTalkID())).toString();
                if (getPocDeviceId.equals(getDestID)) {//被呼
                    Log.d(TAG, "_CallInfo.getSrcID=" + getSrcID);
                    _txtCallerID.setText("" + getTalkID);
                    ShowCallType(_CallInfo);
                } else {
                    _txtCallerID.setText("" + getTalkID);
                    ShowCallType(_CallInfo);
                }
            }
            break;
            case CallStatusDefine.CALLSTATUE_CALLING:
            case CALLSTATUE_CRATESESSION: {
                String getPocDeviceId = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId();
                String getDestID = (new Long(_CallInfo.getDestID())).toString();
                String getSrcID = (new Long(_CallInfo.getSrcID())).toString();
                Log.d(TAG, "getDestID.hashCode()" + getDestID.hashCode());
                Log.d(TAG, "_CallInfo.getDestID=" + getDestID + "_CallInfo.getPocDeviceId=" + getPocDeviceId + "_CallInfo.getSrcID=" + getSrcID);
                if (_CallInfo.getCallType() != 3) {  // poc组呼
                    if (getPocDeviceId.equals(getDestID)) {//被呼
                        Log.d(TAG, "_CallInfo.getSrcID=" + getSrcID);
                        _txtCallerID.setText(R.string.CALLSTATUE_LABEL_LOCALTX);
                        ShowCallType(_CallInfo);
                    } else if (getPocDeviceId.equals(getSrcID)) {//主呼
                        Log.d(TAG, "_CallInfo.getDestID=" + getDestID);
                        _txtCallerID.setText(R.string.CALLSTATUE_LABEL_LOCALTX);
                        ShowCallType(_CallInfo);
                    } else {
                        _txtCallerID.setText(R.string.CALLSTATUE_LABEL_CALLING);
                        ShowCallType(_CallInfo);
                    }
                } else {
                    _txtCallerID.setText(R.string.CALLSTATUE_LABEL_CALLING);
                    ShowCallType(_CallInfo);
                }
            }
            break;
            case CallStatusDefine.CALLSTATUE_RINGING: {
                _txtCallerID.setText(R.string.CALLSTATUE_LABEL_RINGING);
                ShowCallType(_CallInfo);
            }
            break;
            case CallStatusDefine.CALLSTATUE_CALLWating: {
                _txtCallerID.setText(R.string.CALLSTATUE_LABEL_CALLWATING);
                ShowCallType(_CallInfo);
            }
            break;
            case CALLSTATUE_LocalTX: {
                _txtCallerID.setText(R.string.CALLSTATUE_LABEL_LOCALTX);
                ShowCallType(_CallInfo);
            }
            break;
            case CALLSTATUE_LocalRX: {
                ShowCallType(_CallInfo);
                _txtCallerID.setText(String.valueOf(_CallInfo.getTalkID()));
            }
            break;
            case CALLSTATUE_CALLIDLE: {
                String getPocDeviceId = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId();
                String getDestID = (new Long(_CallInfo.getDestID())).toString();
                String getSrcID = (new Long(_CallInfo.getSrcID())).toString();
                if (getPocDeviceId.equals(getDestID)) {
                    _txtCallerID.setText(R.string.CALLSTATUE_LABEL_CALLIDLE);
                    ShowCallType(_CallInfo);
                } else {
                    ShowCallType(_CallInfo);
                    _txtCallerID.setText(R.string.CALLSTATUE_LABEL_CALLIDLE);
                }
            }
            break;
            case CALLSTATUE_CALLEND: {
                _lblCallTimer.setVisibility(View.INVISIBLE);
                switch ((int) _CallInfo.getCallCause()) {
                    case CallStatusDefine.CALLCAUSE_DISC_UNKNOWN_OR_NOT_DEFIEND: {
                        _txtCallerID.setText(R.string.DISC_UNKNOWN_OR_NOT_DEFIEND);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALL_END: {
                        _txtCallerID.setText(R.string.DISC_CALL_END);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_BUSY: {
                        _txtCallerID.setText(R.string.DISC_CALLED_PARTY_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_NOT_SUPPORT_ENCRYP: {
                        _txtCallerID.setText(R.string.DISC_CALLED_PARTY_NOT_SUPPORT_ENCRYP);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_REQ_ENCRYP: {
                        _txtCallerID.setText(R.string.DISC_CALLED_PARTY_REQ_ENCRYP);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_REJECT_CALL: {
                        _txtCallerID.setText(R.string.DISC_CALLED_PARTY_REJECT_CALL);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLING_PARTY_CANCEL_CALL: {
                        _txtCallerID.setText(R.string.DISC_CALLING_PARTY_CANCEL_CALL);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_RINGING_TIME_OUT: {
                        _txtCallerID.setText(R.string.DISC_CALLED_RINGING_TIME_OUT);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLING_RINGING_TIME_OUT: {
                        _txtCallerID.setText(R.string.DISC_CALLING_RINGING_TIME_OUT);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NO_ENCRY_DEVICE: {
                        _txtCallerID.setText(R.string.DISC_NO_ENCRY_DEVICE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CURR_GROUP_NOT_ATTACHED: {
                        _txtCallerID.setText(R.string.DISC_CURR_GROUP_NOT_ATTACHED);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CHANNEL_BUSY: {
                        _txtCallerID.setText(R.string.DISC_CHANNEL_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_INVALID_CALLED_ID: {
                        _txtCallerID.setText(R.string.DISC_INVALID_CALLED_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_UNKNOWN_CALLED_ID: {
                        _txtCallerID.setText(R.string.DISC_UNKNOWN_CALLED_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_NOT_REGISTER: {
                        _txtCallerID.setText(R.string.DISC_CALLED_PARTY_NOT_REGISTER);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_NOT_REACHABLE: {
                        _txtCallerID.setText(R.string.DISC_CALLED_PARTY_NOT_REACHABLE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_PREEMPTIVE_USE_OF_RESOURCE: {
                        _txtCallerID.setText(R.string.DISC_PREEMPTIVE_USE_OF_RESOURCE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_SERVICE_NOT_AVAILABLE: {
                        _txtCallerID.setText(R.string.DISC_INVALID_CALL_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_INVALID_CALL_ID: {
                        _txtCallerID.setText(R.string.DISC_INVALID_CALL_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_SYSTEM_BUSY: {
                        _txtCallerID.setText(R.string.DISC_SYSTEM_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_MAX_CALL_TIME_REACHED: {
                        _txtCallerID.setText(R.string.DISC_MAX_CALL_TIME_REACHED);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALL_RESTORATION_FAILED: {
                        _txtCallerID.setText(R.string.DISC_CALL_RESTORATION_FAILED);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALL_REFUSE: {
                        _txtCallerID.setText(R.string.DISC_CALL_REFUSE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_SERVICE_NOT_AUTH: {
                        _txtCallerID.setText(R.string.DISC_SERVICE_NOT_AUTH);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NETWORK_CONGESTION: {
                        _txtCallerID.setText(R.string.DISC_NETWORK_CONGESTION);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NO_RESPONSE: {
                        _txtCallerID.setText(R.string.DISC_NO_RESPONSE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_DEVICE_BUSY: {
                        _txtCallerID.setText(R.string.DISC_DEVICE_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NO_SIGNAL: {
                        _txtCallerID.setText(R.string.DISC_NO_SIGNAL);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_WRONG_INFO: {
                        _txtCallerID.setText(R.string.DISC_WRONG_INFO);
                    }
                    break;
                }
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //     _lblCallTimer.setText(R.string.CALLSTATUE_LABEL_CALLEND);
            }
        }


    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }


    @Override
    public void OnCallInfoUpdate(RF_CallStatusUpdate _CallInfo) {
//        Log.i(this.toString(), "OnCallInfoUpdate ,Event Call Status:" + _CallInfo.getCallStatus() + " CallCause:" + _CallInfo.getCallCause());
        //修改显示呼叫者ID
//        int _CallType = this.getActivity().getIntent().getIntExtra("CallType", 1);//呼叫类型     1----组呼（用于分组呼叫） 2---个呼 （用于联系人的号码呼叫）
//        int _CallMode = getActivity().getIntent().getIntExtra("CallMode", 2); //呼叫模式，1---窄带集群     2--宽带  3---宽带视频（暂不用）
        if (_CallInfo.getCallMode() == CallModeEnum.TS_CALLMODE_RF) {  //窄带呼叫
            if (_CallInfo.getCallPriority() == 4) {  //紧急呼叫
                if (_CallInfo.getEncrptFlag() == 1) {  // EncrptFlag   //加密呼叫指示	0-非加密，1-加密
                    _acivityContent.setBackgroundResource(R.drawable.rounded_orange);
                } else {
                    _acivityContent.setBackgroundResource(R.drawable.rounded_corner_emergency);
                }
            } else {  //非紧急呼叫
                if (_CallInfo.getEncrptFlag() == 1) {  // EncrptFlag   //加密呼叫指示	0-非加密，1-加密
                    _acivityContent.setBackgroundResource(R.drawable.rounded_orange);
                } else {
                    _acivityContent.setBackgroundResource(R.drawable.rounded_corner);
                }
            }
        } else {
            int getCallType = Integer.parseInt(String.valueOf(mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPriority()));
            _acivityContent.setBackgroundResource(R.drawable.rounded_corner);

        }


        switch (_CallInfo.getCallStatus()) {

            case CALLSTATUE_LocalTX: {
                ShowCallInfo(_CallInfo);
                //本机讲话OnCallInfoUpdate
                _CallStaueImg.setImageResource(R.drawable.ic_txlogo);
                mRecordingView.setLineColor(0xff0000);
                mRecordingView.setVolume(70);
                mRecordingView.startAnim();

            }
            break;
            case CALLSTATUE_LocalRX: {
                ShowCallInfo(_CallInfo);
                _CallStaueImg.setImageResource(R.drawable.ic_rxlogo);
                mRecordingView.setLineColor(0x00ff00);
                mRecordingView.startAnim();
                //呼叫接听
            }
            break;
            case CALLSTATUE_CALLIDLE: {
                ShowCallInfo(_CallInfo);
                mRecordingView.stopAnim();
            }
            break;
            case CALLSTATUE_CALLEND: {
                ShowCallInfo(_CallInfo);
                AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ((ServiceData.get().CallStatueInfo.getValue().getCallStatus() == CALLSTATUE_CALLEND)) {
                        if (null != getActivity()) {
                            getActivity().finish();
                        }
                    }
                });
                //呼叫结束
            }
            break;
            case CALLSTATUE_DESTROYSESSION: {

            }
            break;
            case CALLSTATUE_CRATESESSION: {
                //会话创建
                ShowCallInfo(_CallInfo);
                _CallStaueImg.setImageResource(R.drawable.ic_txlogo);
            }
            break;
            default: {
                ShowCallInfo(_CallInfo);
            }
            break;
        }
    }

    @Override
    public void OnCallGroupIndexUpdate(short GroupIndex) {

    }

    @Override
    public void OnResetCallTo(String callId, byte callType, byte emergency) {
    }

    @Override
    public void OnDirecttalkCreate() {

    }

    @Override
    public void OnDirecttalkDestroy() {

    }

    /**
     * <解码>
     * <BCD格式的字节数组解成数字字符串>
     *
     * @param bcd 字节数组
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String bcd2str(byte[] bcd) {
        if (null == bcd || bcd.length == 0) {
            return "";
        } else {
            // 存储转码后的字符串
            StringBuilder sb = new StringBuilder();

            // 循环数组解码
            for (int i = 0; i < bcd.length; i++) {
                // 转换低字节
                int low = (bcd[i] & 0x0f);
                sb.append(low);

                // 转换高字节
                int high = ((bcd[i] & 0xf0) >> 4);

                // 如果高字节等于0xf说明是补的字节，直接抛掉
                if (high != 0xf) {
                    sb.append(high);
                }
            }

            // 返回解码字符串
            return sb.toString();
        }
    }
}