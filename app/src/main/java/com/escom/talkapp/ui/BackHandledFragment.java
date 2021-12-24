
package com.escom.talkapp.ui;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.impl.struct.TSRunTimeStatusInfo;
import com.tsits.tsmodel.TSITSApplication;

public abstract class BackHandledFragment extends Fragment {

    private static final String TAG = "BackHandledFragment";
    protected BackHandledInterface mBackHandledInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    protected TSITSApplication mTSApplication;

    protected TSRunTimeStatusInfo _tmpRuntimeInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandledInterface)) {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        } else {
            this.mBackHandledInterface = (BackHandledInterface) getActivity();
            Log.d(TAG, "getActivity() is = " + getActivity());
        }
        InitialRunningStatue();
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        Log.d(TAG, "mBackHandledInterface.setSelectedFragment(this) is " + this);
        mBackHandledInterface.setSelectedFragment(this); //this即可获取当前Fragment
    }


    private boolean InitialRunningStatue() {
        mTSApplication = (TSITSApplication) getActivity().getApplication();
        if (mTSApplication != null) {
            if (mTSApplication.getCoreService() != null) {
                if (mTSApplication.getCoreService().isConnected()) {
                    _tmpRuntimeInfo = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
                    return true;
                } else {
                    Log.d(this.getClass().getName(), "InitialRunningStatue: Service not Connected");
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}