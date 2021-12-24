package com.escom.talkapp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.escom.talkapp.ui.BackHandledFragment;
import com.escom.talkapp.ui.BackHandledInterface;
import com.escom.talkapp.ui.ITSCallInfoUpdate;
import com.escom.talkapp.ui.TalkAppFragment;
import com.escom.talkapp.ui.VoiceCallingFragment;
import com.escom.talkapp.ui.VoiceCallingFragmentAccept;
import com.escom.talkapp.ui.VoiceCallingFragmentWaiting;
import com.impl.ICoreClientCallback;
import com.impl.poc.PocSipMsg;
import com.impl.struct.CallModeEnum;
import com.impl.struct.RF_CallStatusUpdate;
import com.impl.struct.TSRunTimeStatusInfo;
import com.tsits.tsmodel.TSITSApplication;
import com.tsits.tsmodel.service.ServiceData;
import com.tsits.tsmodel.service.TSITSService;

import java.util.ArrayList;

import static com.impl.struct.CallModeEnum.TS_CLLMODE_POC;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE_PARA;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_EVENT_ONRFPTTCALLBACK;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_LAUNCHER_ACTION_MODESEL;
import static com.tsits.tsmodel.service.TSCoreCallbackName.TS_CORESERVICE_LAUNCHER_ACTION_MODESEL_POC;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLCAUSE_DISC_CALLED_PARTY_BUSY;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_CRATESESSION;
import static com.tsits.tsmodel.utils.CallStatusDefine.CALLSTATUE_RINGING;
import static java.nio.file.Paths.get;


public class MainActivity extends AppCompatActivity implements BackHandledInterface, ServiceConnection {
    private static final String TAG = "MainActivity";
    private boolean hadIntercept;
    private BackHandledFragment _CurrectFragment;
    private boolean isOutGoing = true;
    private PocSipMsg pocSipMsg;
    private static NavController navController = null;
    private TSRunTimeStatusInfo _tmpRuntimeInfo = null;


    @Override
    protected void onStop() {
        super.onStop();
        TSRunTimeStatusInfo Runtimeinfo = ((TSITSApplication) getApplication()).getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
        //重新恢复默认使用模块信息
        ((TSITSApplication) getApplication()).getCoreService().getICoreServiceEvent().onRFAudio_SetMainInterfaceType(Runtimeinfo.getPriority());
        if (Runtimeinfo.getPriority() == 1) {  //判断优先级
            ServiceData.get().CurrectCallMode.setValue(CallModeEnum.TS_CALLMODE_RF);
        } else {
            ServiceData.get().CurrectCallMode.setValue(TS_CLLMODE_POC);
        }
    }


    private boolean CheckServiceIsRunning(Context context) {  //保证后台运行
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServices = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(300);
        for (int i = 0; i < runningServices.size(); i++) {
            if (runningServices.get(i).service.getClassName().equals(TSITSService.class.getName())) {  //获取正在运行的Service并判断TSITSService是否存活
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onNewIntent(this.getIntent());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//使屏幕保持亮屏
    }


    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        Log.d(TAG, "selectedFragment is " + selectedFragment + "_CurrectFragment is " + _CurrectFragment);
        _CurrectFragment = selectedFragment;
        Log.d(TAG, "selectedFragment2 is " + selectedFragment + "_CurrectFragment2 is " + _CurrectFragment);

    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "getBackStackEntryCount is " + getSupportFragmentManager().getBackStackEntryCount());
        if (_CurrectFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    protected void onUserLeaveHint() {  //用户点击HOME键会被监听并调用到,解决发射禁止弹出默认
        finish();
        super.onUserLeaveHint();
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int _CallMode = intent.getIntExtra("CallMode", 1);
        String _Callto = intent.getStringExtra("CallTo");
        String CallEvent = intent.getStringExtra(TS_CORESERVICE_EVENT);
        RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA);
        RF_CallStatusUpdate _RF_CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);


//        if( ((TSITSApplication) getApplication()).getCoreService().getICoreServiceEvent()==null)
//        {
//            Toast toast=Toast.makeText(getApplicationContext(), "链接到通讯服务失败", Toast.LENGTH_SHORT);
//            this.finish();
//        }

        if (!CheckServiceIsRunning(getApplicationContext())) {//确保后台服务开启
            Intent _serviceintent = new Intent(getApplicationContext(), TSITSService.class);
            startForegroundService(_serviceintent);
            Log.i(this.getClass().getName(), "Start TSITS Service");
            //this.finish();
        } else {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            Log.d(TAG, "----------------nav_host_fragment is open!!");
            // String CallEvent =intent.getStringExtra(TS_CORESERVICE_EVENT);
            Log.i(this.getClass().getName(), "onNewIntern Call");
            if ((intent.hasExtra("CallTo"))) {
                Log.i(this.getClass().getName(), "onNewIntern Call[CallTo:" + intent.getStringExtra
                        ("CallTo") + "-CallType:" + intent.getIntExtra("CallMode", 1)
                        + "-" + intent.getBooleanExtra("emergencyCall", false));
            }
            if (navController != null) {
                if ((intent.hasExtra("CallTo")) && (intent.hasExtra("CallType")) &&
                        (intent.hasExtra("CallMode"))) {
                    Log.i(this.getClass().getName(), "onNewIntern Call[CallTo:" + intent.getStringExtra
                            ("CallTo") + "-CallType:" + intent.getIntExtra("CallMode", 1)
                            + "-" + intent.getBooleanExtra("emergencyCall", false));

                    byte _emergencyCall = (byte) (intent.getBooleanExtra("emergencyCall", false)
                            ? 0x01 : 0x00);
                    TSITSApplication _TSITSAPP = (TSITSApplication) getApplication();
                    if (_TSITSAPP.getCoreService() != null) {
//                        switch ()
//                        if(intent.getIntExtra("CallMode",1)==1) {
//                            _TSITSAPP.getCoreService().getICoreServiceEvent().onAppModel_SetCallID(_Callto, (byte) 0, _emergencyCall);
//                        }else{
//                            _TSITSAPP.getCoreService().getICoreServiceEvent().onAppModel_SetVoiceFullCall(_Callto,false);
//                        }
                        if (intent != null) {
//                            int _CallMode = intent.getIntExtra("CallMode", 1);//呼叫模式，1---窄带集群     2--宽带  3---宽带视频

                            Log.d(TAG, "_CallMode: " + _CallMode);


                            TSITSApplication mTSApplication = (TSITSApplication) getApplication();
                            if (_tmpRuntimeInfo == null) {
                                _tmpRuntimeInfo = mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus();
                            }
                            switch (_CallMode) {
                                case 1: {
                                    Log.d(TAG, "_CallMode is 1");
                                    //窄带呼叫
                                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetCallID(_Callto, (byte) 1, (byte) _emergencyCall);
                                }
                                break;
                                case 2: {
                                    //POC呼叫
                                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetVoiceFullCall(_Callto, true);
                                    navController.navigate(R.id.navigation_calling_waiting);
                                    RF_CallStatusUpdate CallInfo = new RF_CallStatusUpdate((short) CALLSTATUE_CRATESESSION, (short) 1, (short) 1, (short) 0, (short) 1,
                                            Long.parseLong(_Callto), 0, 0,
                                            new byte[1], new byte[1], new byte[1], 0, 0, 0, 0);
                                    CallInfo.setCallMode(CallModeEnum.TS_CLLMODE_POC);
                                    ServiceData.get().CallStatueInfo.setValue(CallInfo);
                                }
                                break;
                                case 3: {
                                    //POC Video
                                    mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_SetVideoCall(_Callto, true);
                                }
                                break;

                            }
                        }
                    }
//                     ((ITSCallInfoUpdate) _CurrectFragment).OnResetCallTo(_Callto,(byte)1,_emergencyCall);
                    return;
                }
                if (intent.hasExtra(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL)) {
                    Bundle bundlePara = new Bundle();
                    bundlePara.putString(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL, intent.getStringExtra(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL));
                    if (intent.getStringExtra(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL).equals(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL_POC)) {
                        //选择宽带呼叫业务
                        //  bundlePara.putString(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL,intent.getStringExtra(TS_CORESERVICE_LAUNCHER_ACTION_MODESEL));
                        ServiceData.get().CurrectCallMode.setValue(TS_CLLMODE_POC);
                        ((TSITSApplication) getApplication()).getCoreService().getICoreServiceEvent().onRFAudio_SetMainInterfaceType(2);
                        Log.d(TAG, "选择宽带呼叫业务");
                    } else {
                        //选择窄带呼叫业务
                        ((TSITSApplication) getApplication()).getCoreService().getICoreServiceEvent().onRFAudio_SetMainInterfaceType(1);
                        ServiceData.get().CurrectCallMode.setValue(CallModeEnum.TS_CALLMODE_RF);
                        Log.d(TAG, "选择窄带呼叫业务");
                    }
                    navController.navigate(R.id.navigation_talk, bundlePara);
                    Log.d(TAG, "navigation_talk is open!!");
                }
                if (intent.hasExtra(TS_CORESERVICE_EVENT)) {
                    switch (CallEvent) {
                        case TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE: {
                            if (_CurrectFragment != null) {
                                Log.i(this.toString(), TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE + "--" + _CurrectFragment.getClass());
                                if (_CurrectFragment.getClass().equals(TalkAppFragment.class)) {
                                    short _groupIndex = intent.getShortExtra(TS_CORESERVICE_EVENT_ONDEFUALTGROUPUPDATE_PARA, (short) 0);
                                    ((ITSCallInfoUpdate) _CurrectFragment).OnCallGroupIndexUpdate(_groupIndex);
                                    Log.d(TAG, "-----------------TalkAppFragment OPEN!!");
                                }
                            }
                        }
                        break;
                        case TS_CORESERVICE_EVENT_ONRFPTTCALLBACK: {
                            Log.d(MainActivity.class.getName(),"TS_CORESERVICE_EVENT_ONRFPTTCALLBACK");
                        }
                        break;

                        case TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE: {  //收到宽带呼叫信息更新

                            int getSrcIDSize = (new Long(_CallInfo.getSrcID())).toString().length();
                            int getDestIDSize = (new Long(_CallInfo.getDestID())).toString().length();
                            String getDestID = (new Long(_CallInfo.getDestID())).toString();
                            int CallType = _CallInfo.getCallType();
                            Log.d(TAG, "_CallInfo.getCallType()" + _CallInfo.getCallType());//被叫和主叫都同步，2 POC组呼  3 POC单呼  9 POC视频单呼
                            Log.d(TAG, "_CallInfo.." + _CallInfo);
                            TSITSApplication mTSApplication = (TSITSApplication) getApplication();

                            if (_CallInfo.getCallType() == 2) {//POC组呼
                                if (mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId()
                                        .equals(getDestID)) {  //判断主呼与被呼（被呼）
                                    if (_CurrectFragment != null) {
                                        if (!_CurrectFragment.getClass().equals(VoiceCallingFragment.class)) {
                                            Bundle bundlePara = new Bundle();
                                            bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                            bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                            navController.navigate(R.id.navigation_calling, bundlePara);
                                            Log.d("TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE1", bundlePara.toString());
                                        } else {
                                            Bundle bundlePara = new Bundle();
                                            bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                            bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                            navController.navigate(R.id.navigation_calling, bundlePara);
                                            Log.d("TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE2", bundlePara.toString());
                                        }
                                    } else {
                                        Bundle bundlePara = new Bundle();
                                        bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                        //   RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                                        bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                        navController.navigate(R.id.navigation_calling, bundlePara);
                                        Log.d(TAG, "TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE3  else");
                                    }
                                    ServiceData.get().CallStatueInfo.setValue(_CallInfo);
                                } else {  //判断主呼与被呼（主呼）
                                    if (_CurrectFragment != null) {
                                        if (!_CurrectFragment.getClass().equals(VoiceCallingFragment.class)) {
                                            Bundle bundlePara = new Bundle();
                                            bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                            bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                            navController.navigate(R.id.navigation_calling, bundlePara);
                                            Log.d("TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE4", bundlePara.toString());
                                        } else {
                                            Bundle bundlePara = new Bundle();
                                            bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                            bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                            navController.navigate(R.id.navigation_calling, bundlePara);
                                            Log.d("TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE5", bundlePara.toString());
                                        }
                                    } else {
                                        Bundle bundlePara = new Bundle();
                                        bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                        //   RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                                        bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                        navController.navigate(R.id.navigation_calling, bundlePara);
                                        Log.d(TAG, "TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE6  else");
                                    }
                                    ServiceData.get().CallStatueInfo.setValue(_CallInfo);
                                }

                            } else if (_CallInfo.getCallType() == 3) {//POC单呼
                                /*
                                 * 当单呼的时候唤醒屏幕
                                 * */
                                PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
                                if (!pm.isScreenOn()) {//如果屏幕是熄灭状态
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            @SuppressLint("InvalidWakeLockTag") final PowerManager.WakeLock wakeLock = pm.newWakeLock(
                                                    PowerManager.ACQUIRE_CAUSES_WAKEUP
                                                            | PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
                                            wakeLock.acquire();//亮屏
                                            wakeLock.release();
                                        }
                                    }, 0);
                                }


//                                TSITSApplication mTSApplication = (TSITSApplication) getApplication();

                                Log.d("getPocDeviceId()", "" + mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId());
                                if (mTSApplication.getCoreService().getICoreServiceEvent().onAppModel_GetRunningStatus().getPocDeviceId()
                                        .equals(getDestID)) {//判断主呼与被呼（被呼）
                                    if (_CurrectFragment != null) {
                                        if (!_CurrectFragment.getClass().equals(VoiceCallingFragmentAccept.class)) {
                                            Bundle bundlePara = new Bundle();
                                            bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                            bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                            navController.navigate(R.id.navigation_accept, bundlePara);
                                            Log.d("TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE", bundlePara.toString());
                                        } else {
                                            //   RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
//                                    Toast.makeText(this, "CurrentFragment Error", Toast.LENGTH_SHORT).show();
                                            //  ((ITSCallInfoUpdate) _CurrectFragment).OnCallInfoUpdate(_CallInfo);
                                        }
                                    } else {
                                        Bundle bundlePara = new Bundle();
                                        bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                        //   RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                                        bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                        navController.navigate(R.id.navigation_accept, bundlePara);
                                        Log.d(TAG, "TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE  else");
                                    }
                                    ServiceData.get().CallStatueInfo.setValue(_CallInfo);
                                } else {//判断主呼与被呼（主呼）
                                    if (_CurrectFragment != null) {
                                        if (_CurrectFragment.getClass().equals(VoiceCallingFragment.class)) {
                                            Bundle bundlePara = new Bundle();
                                            bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                            bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                            navController.navigate(R.id.navigation_calling, bundlePara);
                                            Log.d("TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE", bundlePara.toString());
                                        } else {
                                            Bundle bundlePara = new Bundle();
                                            bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                            bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                            navController.navigate(R.id.navigation_calling, bundlePara);
                                            Log.d("TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE", bundlePara.toString());
                                        }
                                    } else {
                                        Bundle bundlePara = new Bundle();
                                        bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                        //   RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                                        bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE_PARA, _CallInfo);
                                        navController.navigate(R.id.navigation_calling, bundlePara);
                                        Log.d(TAG, "TS_CORESERVICE_EVENT_ONPOCCALLSTATUSUPDATE  else");
                                    }
                                    ServiceData.get().CallStatueInfo.setValue(_CallInfo);
                                }
                            }

                        }
                        break;
                        case TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE: {
                            //收到窄带呼叫信息更新
                            if (_CurrectFragment != null) {
                                if (!_CurrectFragment.getClass().equals(VoiceCallingFragment.class)) {
                                    Bundle bundlePara = new Bundle();
                                    bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                    bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA, _RF_CallInfo);
                                    navController.navigate(R.id.navigation_calling, bundlePara);
                                } else {
                                    //   RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);

                                    //  ((ITSCallInfoUpdate) _CurrectFragment).OnCallInfoUpdate(_CallInfo);
                                }
                            } else {
                                Bundle bundlePara = new Bundle();
                                bundlePara.putString(TS_CORESERVICE_EVENT, CallEvent);
                                //   RF_CallStatusUpdate _CallInfo = (RF_CallStatusUpdate) intent.getSerializableExtra(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA);
                                bundlePara.putSerializable(TS_CORESERVICE_EVENT_ONCALLSTATUSUPDATE_PARA, _RF_CallInfo);
                                navController.navigate(R.id.navigation_calling, bundlePara);
                            }
                        }
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }


}