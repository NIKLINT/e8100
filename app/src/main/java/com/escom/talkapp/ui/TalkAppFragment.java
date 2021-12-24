package com.escom.talkapp.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.escom.talkapp.R;
import com.escom.talkapp.TSModuleType;
import com.hjq.toast.ToastUtils;
import com.impl.poc.PocCallType;
import com.impl.struct.RF_CallStatusUpdate;
import com.impl.struct.RF_TrunkingStateUpdate;
import com.tsits.tsmodel.service.ServiceData;
import com.tsits.tsmodel.service.TSITSService;

import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_LAUNCHER_ACTION_MODESEL;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_LAUNCHER_ACTION_MODESEL_POC;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TalkAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TalkAppFragment extends BackHandledFragment implements ITSCallInfoUpdate {
    private static final String TAG = "TalkAppFragment";

//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    //    //Application实例

    private TextView lblDeviceId = null;

    private LinearLayout grpCallBtn = null;

    private TextView lblWorkType = null;

    private TextView txtgroupname = null;

    private TextView lblBottomLabel = null;

    private Button btnvoicecall = null;

    private Button btnvideocall = null;

    private boolean isPocMode = false;

    private TSModuleType _UsedMode = TSModuleType.TS_MODULE_TYPE_RFModule;

    public TalkAppFragment() {
        super();
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TalkAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TalkAppFragment newInstance(String param1, String param2) {
        TalkAppFragment fragment = new TalkAppFragment();
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        Log.i(this.getClass().getName(), "onBackKeyPressed call");
        getActivity().finish();
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtils.init(getActivity().getApplication());
    }



        private void ShowTMScreenInfo(int GroupID, int TrunkingStatue, byte RegStatue, byte RF_Mode) {
        switch (RegStatue) {
            case RF_TrunkingStateUpdate.STATUS_ACTIVE: {
                if (GroupID == -1) {
                    txtgroupname.setText(R.string.CALLSTATUE_LABEL_INVALIDGROUP);
                } else {
                    txtgroupname.setText(String.valueOf(GroupID));
                }
                lblBottomLabel.setText(R.string.TM_REGSTATUE_Actived);
            }
            break;
            case RF_TrunkingStateUpdate.STATUS_IDLE: {
                txtgroupname.setText(String.valueOf(GroupID));
                lblBottomLabel.setText(R.string.TM_REGSTATUE_Idle);

            }
            break;
            case RF_TrunkingStateUpdate.STATUS_LISTENING: {

                txtgroupname.setText(R.string.TM_REGSTATUE_LISTENING);
            }
            break;
            case RF_TrunkingStateUpdate.STATUS_LOCALRX: {
                txtgroupname.setText(R.string.TM_REGSTATUE_LocalRX);
            }
            break;
            case RF_TrunkingStateUpdate.STATUS_LOCALTX: {
                txtgroupname.setText(R.string.CALLSTATUE_LABEL_LOCALTX);
            }
            break;
            case RF_TrunkingStateUpdate.STATUS_MUTE: {
                txtgroupname.setText(R.string.TM_REGSTATUE_MUTE);
            }
            break;
            case RF_TrunkingStateUpdate.STATUS_OFFLINE: {
                lblBottomLabel.setText(R.string.TM_REGSTATUE_OFFLINE);
                txtgroupname.setText(R.string.CALLSTATUE_LABEL_INVALIDGROUP);
            }
            break;
        }
    }

    private void ShowDMScreenInfo(int GroupIndex, int GroupID, byte RF_Mode) {
        lblWorkType.setText(R.string.TS_WorkType_PDTNormal);
        if (GroupID == -1) {
            txtgroupname.setText(R.string.CALLSTATUE_LABEL_INVALIDGROUP);
        } else {
            txtgroupname.setText(String.valueOf(GroupID));
        }
        lblBottomLabel.setText(getResources().getString(R.string.TS_DM_CurrectChannelIndex) + ":" + String.valueOf(GroupIndex));
        Log.d("TalkAppFragment", "TalkAppFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle Initbundle;
        if (getArguments() != null) {
            Initbundle = getArguments();
        } else {
            Initbundle = new Bundle();
            Initbundle.putString(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL, "");
        }
        View fragmentView = inflater.inflate(R.layout.fragment_talk_app, container, false);
        lblWorkType = fragmentView.findViewById(R.id.lblWorkType);
        txtgroupname = fragmentView.findViewById(R.id.txtgroupname);
        lblBottomLabel = fragmentView.findViewById(R.id.lblBottomLabel);
        btnvoicecall = fragmentView.findViewById(R.id.btnvoicecall);
        lblDeviceId = fragmentView.findViewById(R.id.txtdeviceid);
        grpCallBtn = fragmentView.findViewById(R.id.CallBtnGroup);
        btnvoicecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"isPocMode is "+isPocMode);
                if (!isPocMode) {
                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOn(0, false);
                    Log.d(TAG,"onAppModel_PTTOn");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    int PocSessionID = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetVoiceFullCall(_tmpRuntimeInfo.getPocGroupId(), false);
                    Log.d(TAG,"PocSessionID"+PocSessionID);

                    ServiceData.get().CurrectPocSessionID.setValue(PocSessionID);
                    Log.d(TAG,"CurrectPocSessionID"+ServiceData.get().CurrectPocSessionID);
                }
                // mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOff();
            }
        });

        if (_tmpRuntimeInfo != null) {
            //当前使用的呼叫模式是窄带呼叫模式　
            switch (_tmpRuntimeInfo.getWorkType()) {
                case 0: {
                    //PDT　集群
                    lblWorkType.setText(R.string.TS_WorkType_PDTTrunking);
                    ShowTMScreenInfo((int) _tmpRuntimeInfo.getCurrectGroupID(), _tmpRuntimeInfo.getChannelIndex(), (byte) _tmpRuntimeInfo.getRegState(), (byte) 0);
                    ServiceData.get().TrunkingStatue.observe(getViewLifecycleOwner(), trunkruntime -> {
                        _tmpRuntimeInfo = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
                        Log.d(TAG,"_tmpRuntimeInfo"+_tmpRuntimeInfo);
                        ShowTMScreenInfo((int) _tmpRuntimeInfo.getCurrectGroupID(), trunkruntime.getCurChan(), (byte) trunkruntime.getRegState(), (byte) 0);
                    });
                }
                break;
                case 2: {
                    // PDT常规　
                    ShowDMScreenInfo((int)_tmpRuntimeInfo.getChannelIndex(), _tmpRuntimeInfo.getCallID(), (byte) 1);
                }
                break;
                case 3: {
                    lblWorkType.setText(R.string.TS_WorkType_MPTTrunking);
                    //模拟集群
                }
                break;
                case 4: {
                    //模拟常规　
                    lblWorkType.setText(R.string.TS_WorkType_AnalogNormal);
                }
                break;
            }

            ServiceData.get().DefaultCurrectChannelIndex.observe(getViewLifecycleOwner(), GroupID -> {
                _tmpRuntimeInfo = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
                Log.i(this.toString(), "OnCallGroupIndexUpdate:" + GroupID + "--" + _tmpRuntimeInfo.getWorkType() + " 组号：" + _tmpRuntimeInfo.getCurrectGroupID());
                if (Initbundle.getString(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL).equals(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL_POC)) {
                    lblWorkType.setText(R.string.TS_WorkType_POC);
                    isPocMode = true;
                    lblBottomLabel.setText(getResources().getString(R.string.TS_POC_CurrectUserID) + ":" + String.valueOf(_tmpRuntimeInfo.getPocDeviceId()));
                    txtgroupname.setText(getResources().getString(R.string.TS_POC_CurrectGroupIndex) + ":" + String.valueOf(_tmpRuntimeInfo.getPocGroupId()));
                    lblDeviceId.setText(getResources().getString(R.string.TS_POC_CurrectUserName) + ":"+_tmpRuntimeInfo.getPocGroupId());
                    grpCallBtn.setVisibility(View.VISIBLE);
                } else {
                    grpCallBtn.setVisibility(View.INVISIBLE);
                    Log.d(this.getClass().getName(), "grpCallBtn.setVisibility(View.INVISIBLE)");
                    isPocMode = false;
                    switch (_tmpRuntimeInfo.getWorkType()) {
                        case 0: {
                            //PDT　集群
                            lblWorkType.setText(R.string.TS_WorkType_PDTTrunking);
                            ShowTMScreenInfo(_tmpRuntimeInfo.getCurrectGroupID(), (int) _tmpRuntimeInfo.getCurrectGroupID(), (byte) _tmpRuntimeInfo.getRegState(), (byte) 0);
                            Log.d("TalkAppFragment", "TalkAppFragment1");
                        }
                        break;
                        case 2: {
                            // PDT常规　
                            lblWorkType.setText(R.string.TS_WorkType_PDTNormal);
                            lblBottomLabel.setText(getResources().getString(R.string.TS_DM_CurrectChannelIndex) + ":" + String.valueOf(_tmpRuntimeInfo.getChannelIndex()+1));
                            txtgroupname.setText(String.valueOf(_tmpRuntimeInfo.getCallID()));
                            lblDeviceId.setText(getResources().getString(R.string.TM_REGSTATUE_Idle));
                            Log.d("TalkAppFragment", "TalkAppFragment2");
                            Log.d("TalkAppFragment", "_tmpRuntimeInfo"+_tmpRuntimeInfo);
                        }
                        break;
                        case 3: {
                            lblWorkType.setText(R.string.TS_WorkType_MPTTrunking);
                            //模拟集群
                            lblBottomLabel.setText(getResources().getString(R.string.TS_DM_CurrectChannelIndex) + ":" + String.valueOf(_tmpRuntimeInfo.getChannelIndex()+1));
                            txtgroupname.setText(String.valueOf(_tmpRuntimeInfo.getCallID()));
                        }
                        break;
                        case 4: {
                            //模拟常规　
                            lblWorkType.setText(R.string.TS_WorkType_AnalogNormal);
                        }
                        break;
                    }
                }
            });
        } else {
            //  if(!CheckServiceIsRunning(getApplicationContext())) {
            Intent _serviceintent = new Intent(getActivity().getApplicationContext(), TSITSService.class);
            this.getActivity().startForegroundService(_serviceintent);
            Log.i(this.getClass().getName(), "Start TSITS Service");
            getActivity().finish();
            //}
        }
        return fragmentView;

    }


    @Override
    public void OnCallInfoUpdate(RF_CallStatusUpdate CallInfo) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void OnCallGroupIndexUpdate(short GroupIndex) {

    }

    @Override
    public void OnResetCallTo(String callId, byte callType, byte emergency) {
        Log.i(this.getClass().getName(), "OnResetCallTo");
//        mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetCallID(callId, (byte) 1, emergency);
    }

    @Override
    public void OnDirecttalkCreate() {

    }

    @Override
    public void OnDirecttalkDestroy() {

    }


}