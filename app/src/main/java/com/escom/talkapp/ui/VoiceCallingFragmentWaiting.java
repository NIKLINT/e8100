package com.escom.talkapp.ui;

import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.escom.talkapp.R;
import com.impl.ICoreClientCallback;
import com.impl.poc.PocSipMsg;
import com.impl.struct.CallModeEnum;
import com.impl.struct.RF_CallStatusUpdate;
import com.tsits.tsmodel.TSITSApplication;
import com.tsits.tsmodel.service.ServiceData;

import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoiceCallingFragmentWaiting#newInstance} factory method to
 * create an instance of this fragment.
 */


public class VoiceCallingFragmentWaiting extends BackHandledFragment{

    private Button _btnCallhangup_wait;
    private TextView _lblCallTimer_wait;
    private View _ContentView;

    private PocSipMsg _PocCallInfo;

    // TODO: Rename and change types of parameters


    public VoiceCallingFragmentWaiting() {
        super();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static VoiceCallingFragmentWaiting newInstance(String param1, String param2) {
        VoiceCallingFragmentWaiting fragment = new VoiceCallingFragmentWaiting();
        return fragment;
    }


    private void RegistViewEvent() {
        if (_btnCallhangup_wait != null) {
            _btnCallhangup_wait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ServiceData.get().CurrectCallMode.getValue() == CallModeEnum.TS_CALLMODE_RF) {
                        mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_HangUpCall(1);
                        getActivity().finish();
                    }
                }
            });
        }

        _lblCallTimer_wait.setText("本机"+mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _ContentView = inflater.inflate(R.layout.fragment_voice_calling_waiting, container, false);
        _btnCallhangup_wait = _ContentView.findViewById(R.id.btnCallhangup_wait);
        _lblCallTimer_wait = _ContentView.findViewById(R.id.lblCallTimer_wait);


        RegistViewEvent();
        return _ContentView;
    }




}