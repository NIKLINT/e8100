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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
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
import com.escom.talkapp.SplashActivity;
import com.impl.ICoreClientCallback;
import com.impl.poc.PocCallType;
import com.impl.poc.PocSipMsg;
import com.impl.struct.RF_CallStatusUpdate;
import com.impl.struct.CallModeEnum;
import com.tsits.tsmodel.service.ServiceData;
import com.tsits.tsmodel.utils.CallStatusDefine;
import com.tsits.tsmodel.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import jaygoo.widget.wlv.WaveLineView;

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
 * Use the {@link VoiceCallingFragmentAccept#newInstance} factory method to
 * create an instance of this fragment.
 */

public class VoiceCallingFragmentAccept extends BackHandledFragment implements ITSCallInfoUpdate {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private TextView _txtCallID_accept;
    private TextView _txtCallerID_accept;
    private ImageView _CallStaueImg_accept;
    private View _ContentView_accept;
    private TextView _lblCallTimer_accept;
    private LinearLayout _acivityContent_accept;
    private TextView lblWorkType_accept;
    protected BackHandledInterface mBackHandledInterface = null;
    private WaveLineView mRecordingView_accept;// = findViewById(R.id.audioRecordView)
    private Button _btnCallhangup_accept;
    private Button _btnacceptcall_accept;
    private IntentFilter _KeyFilter;
    private String TAG = this.getClass().getName();



    // private CallModeEnum _CurrectCallMode =CallModeEnum.TS_CALLMODE_RF;
    public VoiceCallingFragmentAccept() {
        super();
    }

    private PocSipMsg _PocCallInfo;
//    private CountDownTimer countDownTimer;


    private BroadcastReceiver KeyEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(this.getClass().getName(), "KeyEventReceiver message:" + intent.getAction().toString());
            //     mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(0);
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VoiceCallingFragmentAccept.
     */
    // TODO: Rename and change types and number of parameters
    public static VoiceCallingFragmentAccept newInstance(String param1, String param2) {
        VoiceCallingFragmentAccept fragment = new VoiceCallingFragmentAccept();
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
        Log.d("VoiceCallingFragmentAccept", "VoiceCallingFragmentAccept is create");

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


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            Log.d(this.getClass().getName(), "Handler message");
            switch (message.what) {
                case 1:
                    _lblCallTimer_accept.setText(_PocCallInfo.getCallTel().toString());
                    Log.d(TAG,"getCallTel: "+_PocCallInfo.getCallTel().toString());
                    break;
            }
        }
    };


    private void RegistViewEvent() {
        //region Hangup Button Event
        RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) getArguments().getSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA);
        _PocCallInfo = (PocSipMsg) getArguments().getSerializable(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);

        if (_btnacceptcall_accept != null) {
            _btnacceptcall_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _btnacceptcall_accept.setVisibility(View.GONE);
                    _CallStaueImg_accept.setImageResource(R.drawable.ic_txlogo);
                    ICoreClientCallback clientSDK = mTSApplication.getCoreService().getICoreServiceEvent();
                    if (clientSDK != null) {
                        clientSDK.onAppModel_AnswerCall(PocCallType.getTypeof(PocCallType.FULLCALL), 1);
                    }
                }
            });
        }

        if (_btnCallhangup_accept != null) {
            _btnCallhangup_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"------------------------_btnCallhangup_accept is click!!");
                    if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CALLMODE_RF) {
                        Log.d(TAG,"-----------1-------------_btnCallhangup_accept is click!!");
                        mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(-1);
                    } else {
                        Log.d(TAG,"----------2--------------_btnCallhangup_accept is click!!");
                        if (_CallInfo != null) {
                            Log.d(TAG,"----------_CallInfo--------------_btnCallhangup_accept is click!!");
                            ICoreClientCallback clientSDK = mTSApplication.getCoreService().getICoreServiceEvent();
                            int getPocDeviceId= Integer.parseInt(clientSDK.onAppModel_GetRunningStatus().getPocDeviceId());
                            int getDestID= (int) _CallInfo.getDestID();
                            if (getPocDeviceId==getDestID) {
                                Log.d(TAG,"------------3------------_btnCallhangup_accept is click!!");
                                Log.d(TAG, clientSDK.onAppModel_GetRunningStatus().getPocDeviceId()
                                                + ""+_CallInfo.getDestID());
                                mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(1);
                            }else {
                                Log.d(TAG,"-----------4-------------_btnCallhangup_accept is click!!");
                                Log.d(TAG, clientSDK.onAppModel_GetRunningStatus().getPocDeviceId()
                                                + ""+_CallInfo.getDestID());
                                mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(1);
                            }
                        }
                        getActivity().finish();
                    }
                }
            });
        }
        //endregion


        _ContentView_accept.setOnKeyListener(new View.OnKeyListener() {
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

        ServiceData.get().SessionCalltime.observe(getViewLifecycleOwner(), integer -> {
//            String min = String.format("%02d", integer / 60);
//            String sec = String.format("%02d", integer % 60);
//            _lblCallTimer_accept.setText(min + ":" + sec);
            _lblCallTimer_accept.setText(""+_CallInfo.getSrcID());
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        _ContentView_accept = inflater.inflate(R.layout.fragment_voice_calling_accept, container, false);
        _CallStaueImg_accept = _ContentView_accept.findViewById(R.id.imgCallType_accept);
        lblWorkType_accept = _ContentView_accept.findViewById(R.id.lblCallType_accept);
        _acivityContent_accept = _ContentView_accept.findViewById(R.id.linearLayout_accept);
        _btnCallhangup_accept = _ContentView_accept.findViewById(R.id.btnCallhangup_accept);
        _btnacceptcall_accept = _ContentView_accept.findViewById(R.id.btnCallaccept_accept);
        mRecordingView_accept = _ContentView_accept.findViewById(R.id.audioRecordView_accept);



        {
            //当前使用的呼叫模式是窄带呼叫模式　

        }
        if (getArguments() != null) {
            String CallEvent = getArguments().getString(TS_CORESERVICE_EVENT);
            _txtCallID_accept = _ContentView_accept.findViewById(R.id.lblGroupID_accept);
            _txtCallerID_accept = _ContentView_accept.findViewById(R.id.lblCallFrom_accept);
            _lblCallTimer_accept = _ContentView_accept.findViewById(R.id.lblCallTimer_accept);
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
                        lblWorkType_accept.setText(R.string.TS_WorkType_PDTTrunking);
                    }
                    break;
                    case 2: {
                        // PDT常规　
                        lblWorkType_accept.setText(R.string.TS_WorkType_PDTNormal);
                    }
                    break;
                    case 3: {
                        lblWorkType_accept.setText(R.string.TS_WorkType_MPTTrunking);
                        //模拟集群
                    }
                    break;
                    case 4: {
                        //模拟常规　
                        lblWorkType_accept.setText(R.string.TS_WorkType_AnalogNormal);
                    }
                    break;
                }
            } else if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CLLMODE_POC) {
                lblWorkType_accept.setText(R.string.TS_WorkType_Single_POC);

            }

        }
        RegistViewEvent();




//        final long timeTnterval = 10000;
//
//        Runnable runnable = new Runnable() { //创建线程计时关闭通话
//            public void run() {
//                try {
//                    Thread.sleep(timeTnterval);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(1);
//                getActivity().finish();
//            }
//        };
//        thread = new Thread(runnable);
//        thread.start();

//        if (timer == null) {
//            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(1);
//                    getActivity().finish();
//                }
//            }, 5000, 5000);
//        }


        return _ContentView_accept;
    }


    private void ShowCallType(RF_CallStatusUpdate _CallInfo) {
        if (_CallInfo.getCallMode() == CallModeEnum.TS_CALLMODE_RF) {
            switch (_CallInfo.getCallType()) {
                case CALLTYPE_Boradcast: {
                    _txtCallID_accept.setText(R.string.CALLTYPE_BROADCAST);
                }
                break;
                case CALLTYPE_GROUP: {
                    _txtCallID_accept.setText("呼叫组:" + _CallInfo.getDestID());
                }
                break;
                case CALLTYPE_SINGLE: {
                    if (_CallInfo.getDestID() == _tmpRuntimeInfo.getPdtDeviceID()) {
                        _txtCallID_accept.setText("单呼者:" + _CallInfo.getSrcID());
                    } else {
                        _txtCallID_accept.setText("单呼者:" + _CallInfo.getDestID());
                    }
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
                    _txtCallID_accept.setText("PSTN号码:" + PSTNNum);
                    _txtCallID_accept.setSelected(true);
                }
                break;
            }
        } else if (_CallInfo.getCallMode() == CallModeEnum.TS_CLLMODE_POC) {
            _txtCallID_accept.setText(R.string.CALLTYPE_POCCALL_SINGLE);
        }
    }


    private void ShowCallInfo(RF_CallStatusUpdate _CallInfo) {
        LogUtils.d(this, "VoiceCallingFragment-ShowCallInfo-call-" + _CallInfo.toString());
        _txtCallerID_accept.setText("");
        _txtCallID_accept.setText("");
        _txtCallID_accept.setSelected(false);
        switch ((byte) _CallInfo.getCallStatus()) {
            case CallStatusDefine.CALLSTATUE_IDLE: {
                _txtCallerID_accept.setText(R.string.CALLSTATUE_LABEL_IDLE);
                ShowCallType(_CallInfo);
            }
            break;
            case CallStatusDefine.CALLSTATUE_CALLING:
            case CALLSTATUE_CRATESESSION: {
                _txtCallerID_accept.setText(R.string.CALLSTATUE_LABEL_CALLING);
                ShowCallType(_CallInfo);
            }
            break;
            case CallStatusDefine.CALLSTATUE_RINGING: {
                _txtCallerID_accept.setText(R.string.CALLSTATUE_LABEL_RINGING);
                ShowCallType(_CallInfo);
            }
            break;
            case CallStatusDefine.CALLSTATUE_CALLWating: {
                _txtCallerID_accept.setText(R.string.CALLSTATUE_LABEL_CALLWATING);
                ShowCallType(_CallInfo);
            }
            break;
            case CALLSTATUE_LocalTX: {
                _txtCallerID_accept.setText(R.string.CALLSTATUE_LABEL_LOCALTX);

                ShowCallType(_CallInfo);
            }
            break;
            case CALLSTATUE_LocalRX: {

                ShowCallType(_CallInfo);
                _txtCallerID_accept.setText(String.valueOf(_CallInfo.getTalkID()));
            }
            break;
            case CALLSTATUE_CALLIDLE: {
                ShowCallType(_CallInfo);
                _txtCallerID_accept.setText(R.string.CALLSTATUE_LABEL_CALLIDLE);
            }
            break;
            case CALLSTATUE_CALLEND: {
                _lblCallTimer_accept.setVisibility(View.INVISIBLE);
                switch ((int) _CallInfo.getCallCause()) {
                    case CallStatusDefine.CALLCAUSE_DISC_UNKNOWN_OR_NOT_DEFIEND: {
                        _txtCallerID_accept.setText(R.string.DISC_UNKNOWN_OR_NOT_DEFIEND);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALL_END: {
                        _txtCallerID_accept.setText(R.string.DISC_CALL_END);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_BUSY: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLED_PARTY_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_NOT_SUPPORT_ENCRYP: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLED_PARTY_NOT_SUPPORT_ENCRYP);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_REQ_ENCRYP: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLED_PARTY_REQ_ENCRYP);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_REJECT_CALL: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLED_PARTY_REJECT_CALL);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLING_PARTY_CANCEL_CALL: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLING_PARTY_CANCEL_CALL);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_RINGING_TIME_OUT: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLED_RINGING_TIME_OUT);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLING_RINGING_TIME_OUT: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLING_RINGING_TIME_OUT);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NO_ENCRY_DEVICE: {
                        _txtCallerID_accept.setText(R.string.DISC_NO_ENCRY_DEVICE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CURR_GROUP_NOT_ATTACHED: {
                        _txtCallerID_accept.setText(R.string.DISC_CURR_GROUP_NOT_ATTACHED);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CHANNEL_BUSY: {
                        _txtCallerID_accept.setText(R.string.DISC_CHANNEL_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_INVALID_CALLED_ID: {
                        _txtCallerID_accept.setText(R.string.DISC_INVALID_CALLED_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_UNKNOWN_CALLED_ID: {
                        _txtCallerID_accept.setText(R.string.DISC_UNKNOWN_CALLED_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_NOT_REGISTER: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLED_PARTY_NOT_REGISTER);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_NOT_REACHABLE: {
                        _txtCallerID_accept.setText(R.string.DISC_CALLED_PARTY_NOT_REACHABLE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_PREEMPTIVE_USE_OF_RESOURCE: {
                        _txtCallerID_accept.setText(R.string.DISC_PREEMPTIVE_USE_OF_RESOURCE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_SERVICE_NOT_AVAILABLE: {
                        _txtCallerID_accept.setText(R.string.DISC_INVALID_CALL_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_INVALID_CALL_ID: {
                        _txtCallerID_accept.setText(R.string.DISC_INVALID_CALL_ID);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_SYSTEM_BUSY: {
                        _txtCallerID_accept.setText(R.string.DISC_SYSTEM_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_MAX_CALL_TIME_REACHED: {
                        _txtCallerID_accept.setText(R.string.DISC_MAX_CALL_TIME_REACHED);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALL_RESTORATION_FAILED: {
                        _txtCallerID_accept.setText(R.string.DISC_CALL_RESTORATION_FAILED);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_CALL_REFUSE: {
                        _txtCallerID_accept.setText(R.string.DISC_CALL_REFUSE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_SERVICE_NOT_AUTH: {
                        _txtCallerID_accept.setText(R.string.DISC_SERVICE_NOT_AUTH);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NETWORK_CONGESTION: {
                        _txtCallerID_accept.setText(R.string.DISC_NETWORK_CONGESTION);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NO_RESPONSE: {
                        _txtCallerID_accept.setText(R.string.DISC_NO_RESPONSE);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_DEVICE_BUSY: {
                        _txtCallerID_accept.setText(R.string.DISC_DEVICE_BUSY);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_NO_SIGNAL: {
                        _txtCallerID_accept.setText(R.string.DISC_NO_SIGNAL);
                    }
                    break;
                    case CallStatusDefine.CALLCAUSE_DISC_WRONG_INFO: {
                        _txtCallerID_accept.setText(R.string.DISC_WRONG_INFO);
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
    public void OnCallInfoUpdate(RF_CallStatusUpdate _CallInfo) {
//        Log.i(this.toString(), "OnCallInfoUpdate ,Event Call Status:" + _CallInfo.getCallStatus() + " CallCause:" + _CallInfo.getCallCause());
        //修改显示呼叫者ID
//        int _CallType = this.getActivity().getIntent().getIntExtra("CallType", 1);//呼叫类型     1----组呼（用于分组呼叫） 2---个呼 （用于联系人的号码呼叫）
//        int _CallMode = getActivity().getIntent().getIntExtra("CallMode", 2); //呼叫模式，1---窄带集群     2--宽带  3---宽带视频（暂不用）
//        boolean emergencyCall=getActivity().getIntent().getBooleanExtra("emergencyCall",false);

        if (_CallInfo.getCallPriority() == 4) {
            _acivityContent_accept.setBackgroundResource(R.drawable.rounded_corner_emergency);
            //   mRecordingView.setBackgroundResource(R.color.emergency);
        } else {
            if (_CallInfo.getCallMode() == CallModeEnum.TS_CALLMODE_RF) {
                //receive RF CallInfo Update
                if (_CallInfo.getEncrptFlag() == 0) { // EncrptFlag   //加密呼叫指示	0-非加密，1-加密
                    _acivityContent_accept.setBackgroundResource(R.drawable.rounded_corner);
                    // mRecordingView.setBackgroundResource(R.color.backgroundColor);
                } else if (_CallInfo.getEncrptFlag() == 1) {
                    _acivityContent_accept.setBackgroundResource(R.drawable.rounded_orange);
                }
            } else {
                //Receive POC CallInfo Update
                if (_CallInfo.getCallType() == 1) {
                    _acivityContent_accept.setBackgroundResource(R.drawable.rounded_corner);
                    //single call
                }
            }
        }


        switch (_CallInfo.getCallStatus()) {

            case CALLSTATUE_LocalTX: {
                ShowCallInfo(_CallInfo);
                //本机讲话OnCallInfoUpdate
                _CallStaueImg_accept.setImageResource(R.drawable.ic_txlogo);
                mRecordingView_accept.setLineColor(0xff0000);
                mRecordingView_accept.setVolume(70);
                mRecordingView_accept.startAnim();

            }
            break;
            case CALLSTATUE_LocalRX: {
                ShowCallInfo(_CallInfo);
                _CallStaueImg_accept.setImageResource(R.drawable.ic_rxlogo);
                mRecordingView_accept.setLineColor(0x00ff00);
                mRecordingView_accept.startAnim();
                //呼叫接听
            }
            break;
            case CALLSTATUE_CALLIDLE: {
                ShowCallInfo(_CallInfo);
                mRecordingView_accept.stopAnim();
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
//                _CallStaueImg_accept.setImageResource(R.drawable.ic_txlogo);
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