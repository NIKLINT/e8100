package com.escom.talkapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.escom.talkapp.R;
import com.escom.talkapp.TSModuleType;
import com.hjq.toast.ToastUtils;
import com.impl.struct.RF_CallStatusUpdate;
import com.impl.struct.RF_TrunkingStateUpdate;
import com.impl.struct.TSRunTimeStatusInfo;
import com.tsits.tsmodel.TSITSApplication;
import com.tsits.tsmodel.service.ServiceData;
import com.tsits.tsmodel.service.TSITSService;

import java.security.acl.Group;

import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_LAUNCHER_ACTION_MODESEL;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_LAUNCHER_ACTION_MODESEL_POC;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TalkAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TalkAppFragment extends BackHandledFragment implements ITSCallInfoUpdate {
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

    private TextView lblDeviceId=null;

    private LinearLayout   grpCallBtn=null;

    private TextView lblWorkType =null;

    private TextView txtgroupname =null;

    private TextView lblBottomLabel=null;

    private Button btnvoicecall =null;

    private Button btnvideocall =null;

    private boolean isPocMode =false;

    private TSModuleType _UsedMode =TSModuleType.TS_MODULE_TYPE_RFModule;

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
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        Log.i(this.getClass().getName(),"onBackKeyPressed call");
        getActivity().finish();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtils.init(getActivity().getApplication());
    }

    private void ShowTMScreenInfo(int GroupID,int TrunkingStatue,byte RegStatue,byte RF_Mode){
        switch (RegStatue){
            case RF_TrunkingStateUpdate.STATUS_ACTIVE:
            {
                if(GroupID==-1){
                    txtgroupname.setText(R.string.CALLSTATUE_LABEL_INVALIDGROUP);
                }else {
                    txtgroupname.setText(String.valueOf(GroupID));
                }
                lblBottomLabel.setText(R.string.TM_REGSTATUE_Actived);
            }break;
            case RF_TrunkingStateUpdate.STATUS_IDLE:{
                txtgroupname.setText(String.valueOf(GroupID));
                lblBottomLabel.setText(R.string.TM_REGSTATUE_Idle);
            }break;
            case RF_TrunkingStateUpdate.STATUS_LISTENING:
            {

                txtgroupname.setText(R.string.TM_REGSTATUE_LISTENING);
            }break;
            case RF_TrunkingStateUpdate.STATUS_LOCALRX:
            {
                txtgroupname.setText(R.string.TM_REGSTATUE_LocalRX);
            }break;
            case RF_TrunkingStateUpdate.STATUS_LOCALTX:
            {
                txtgroupname.setText(R.string.CALLSTATUE_LABEL_LOCALTX);
            }break;
            case RF_TrunkingStateUpdate.STATUS_MUTE:
            {
                txtgroupname.setText(R.string.TM_REGSTATUE_MUTE);
            }break;
            case RF_TrunkingStateUpdate.STATUS_OFFLINE:{
                lblBottomLabel.setText(R.string.TM_REGSTATUE_OFFLINE);
                txtgroupname.setText(R.string.CALLSTATUE_LABEL_INVALIDGROUP);
            }break;
        }
    }

    private void ShowDMScreenInfo(int GroupIndex,int GroupID,byte RF_Mode)
    {
        lblWorkType.setText(R.string.TS_WorkType_PDTNormal);

        if(GroupID==-1){
            txtgroupname.setText(R.string.CALLSTATUE_LABEL_INVALIDGROUP);
        }else{
            txtgroupname.setText(String.valueOf(GroupID));
        }
        lblBottomLabel.setText(getResources().getString(R.string.TS_DM_CurrectChannelIndex)+":"+String.valueOf(GroupIndex));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle Initbundle ;
        if(getArguments()!=null) {
            Initbundle = getArguments();
        }else{
            Initbundle=new Bundle();
            Initbundle.putString(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL,"");
        }
        View fragmentView= inflater.inflate(R.layout.fragment_talk_app, container, false);
        lblWorkType =   fragmentView.findViewById(R.id.lblWorkType);
        txtgroupname =  fragmentView.findViewById(R.id.txtgroupname);
        lblBottomLabel  =   fragmentView.findViewById(R.id.lblBottomLabel);
        btnvoicecall    =   fragmentView.findViewById(R.id.btnvoicecall);
        lblDeviceId =   fragmentView.findViewById(R.id.txtdeviceid);
        grpCallBtn=fragmentView.findViewById(R.id.CallBtnGroup);
        btnvoicecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPocMode) {
                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOn(0,false);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    int PocSessionID = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetVoiceFullCall(_tmpRuntimeInfo.getPocGroupId(),false);
                    ServiceData.get().CurrectPocSessionID.setValue(PocSessionID);
                }
               // mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOff();
            }
        });
        btnvideocall    =   fragmentView.findViewById(R.id.btnvideocall);
        btnvideocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPocMode) {
                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_PTTOn(0,false);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    int PocSessionID = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetVoiceFullCall(_tmpRuntimeInfo.getPocGroupId(),false);
                    ServiceData.get().CurrectPocSessionID.setValue(PocSessionID);
                }
            }
        });
        if(_tmpRuntimeInfo!=null)
        {

//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                    btnvoicecall.getLayoutParams();
//            params.weight   =   0;
//            lblDeviceId.setText(String.valueOf( _tmpRuntimeInfo.getPdtDeviceID()));
//            btnvoicecall.setLayoutParams(params);
            //当前使用的呼叫模式是窄带呼叫模式　
            switch (_tmpRuntimeInfo.getWorkType()) {
                case 0: {
                    //PDT　集群
                    lblWorkType.setText(R.string.TS_WorkType_PDTTrunking);
//                    lblBottomLabel.setText(getResources().getString(R.string.TS_DT_CurrectGroupIndex)+":"+String.valueOf(_tmpRuntimeInfo.getChannelIndex()));
//                    txtgroupname.setText(String.valueOf(_tmpRuntimeInfo.getCallID()));
                    //RF_TrunkingStateUpdate _tmpTrunkingInfo =ServiceData.get().TrunkingStatue.getValue();
                    //if(_tmpTrunkingInfo!=null) {
                        ShowTMScreenInfo((int) _tmpRuntimeInfo.getCurrectGroupID(), _tmpRuntimeInfo.getChannelIndex(), (byte) _tmpRuntimeInfo.getRegState(), (byte) 0);
                        ServiceData.get().TrunkingStatue.observe(getViewLifecycleOwner(), trunkruntime -> {
                            _tmpRuntimeInfo = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
                            ShowTMScreenInfo((int) _tmpRuntimeInfo.getCurrectGroupID(), trunkruntime.getCurChan(), (byte) trunkruntime.getRegState(), (byte) 0);
                        });
                }
                break;
                case 2: {
                    // PDT常规　
                    ShowDMScreenInfo(_tmpRuntimeInfo.getChannelIndex(),_tmpRuntimeInfo.getCallID(), (byte) 1);
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
            Log.i(this.toString(), "OnCallGroupIndexUpdate:" + GroupID + "--" + _tmpRuntimeInfo.getWorkType()+" 组号："+_tmpRuntimeInfo.getCurrectGroupID());
            if(Initbundle.getString(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL).equals(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL_POC)){
                lblWorkType.setText(R.string.TS_WorkType_POC);
                isPocMode=true;
                lblBottomLabel.setText(getResources().getString(R.string.TS_POC_CurrectUserID) + ":" + String.valueOf(_tmpRuntimeInfo.getPocDeviceId()));
                txtgroupname.setText(getResources().getString(R.string.TS_POC_CurrectGroupIndex)+ ":"+String.valueOf(_tmpRuntimeInfo.getPocGroupId()));
                lblDeviceId.setText(getResources().getString(R.string.TS_POC_CurrectUserName)+":");
                grpCallBtn.setVisibility(View.VISIBLE);
            }else {
                grpCallBtn.setVisibility(View.INVISIBLE);
                Log.d(this.getClass().getName(),"grpCallBtn.setVisibility(View.INVISIBLE)");
                isPocMode=false;
                switch (_tmpRuntimeInfo.getWorkType()) {
                    case 0: {
                        //PDT　集群
                        lblWorkType.setText(R.string.TS_WorkType_PDTTrunking);
                        ShowTMScreenInfo(_tmpRuntimeInfo.getCurrectGroupID(), (int) _tmpRuntimeInfo.getCurrectGroupID(), (byte) _tmpRuntimeInfo.getRegState(), (byte) 0);
                    }
                    break;
                    case 2: {
                        // PDT常规　
                        lblWorkType.setText(R.string.TS_WorkType_PDTNormal);
                        lblBottomLabel.setText(getResources().getString(R.string.TS_DM_CurrectChannelIndex) + ":" + String.valueOf(_tmpRuntimeInfo.getChannelIndex()));
                        txtgroupname.setText(String.valueOf(_tmpRuntimeInfo.getCallID()));
                    }
                    break;
                    case 3: {
                        lblWorkType.setText(R.string.TS_WorkType_MPTTrunking);
                        //模拟集群
                        lblBottomLabel.setText(getResources().getString(R.string.TS_DM_CurrectChannelIndex) + ":" + String.valueOf(_tmpRuntimeInfo.getChannelIndex()));
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
        }else{
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
    public void OnCallGroupIndexUpdate(short GroupIndex) {

    }

    @Override
    public void OnResetCallTo(String callId, byte callType, byte emergency) {
        Log.i(this.getClass().getName(),"OnResetCallTo");
        mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetCallID(callId,(byte)0,emergency);
    }

    @Override
    public void OnDirecttalkCreate() {

    }

    @Override
    public void OnDirecttalkDestroy() {

    }
}