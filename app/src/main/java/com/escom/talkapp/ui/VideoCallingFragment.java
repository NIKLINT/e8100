package com.escom.talkapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.escom.talkapp.R;
import com.impl.struct.RF_CallStatusUpdate;

    public class VideoCallingFragment extends BackHandledFragment implements ITSCallInfoUpdate {

    private final String TAG = this.getClass().getName();
    private View _ContentView;

    @Override
    public boolean onBackPressed() {
        Log.i(this.toString(), "onKey Back listener is working!!!");
        //   getActivity().finish();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("VideoCallingFragment", "VoiceCallingFragment is create");

        return;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        _ContentView = inflater.inflate(R.layout.fragment_video_calling, container, false);

        return _ContentView;
    }

    @Override
    public void OnCallInfoUpdate(RF_CallStatusUpdate CallInfo) {

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
}