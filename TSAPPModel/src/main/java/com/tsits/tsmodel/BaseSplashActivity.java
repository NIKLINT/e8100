package com.tsits.tsmodel;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.permissionx.guolindev.PermissionX;
import com.tsits.tsmodel.service.TSITSService;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSplashActivity extends AppCompatActivity {
    private List<String> mPermissionList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getView());

        //权限申请
        checkPermissions();
    }



    @Override
    public void onBackPressed() {

    }

    /**
     * 权限申请
     */
    private void checkPermissions() {
        mPermissionList = new ArrayList<>();
        mPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        if (Build.VERSION.SDK_INT >= 29) {
//            mPermissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//        }

        //动态申请权限
        PermissionX.init(this)
            .permissions(mPermissionList).explainReasonBeforeRequest()
            .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(deniedList, "APP程序需要基于这些权限", "确定", "取消"))
            .onForwardToSettings((scope, deniedList) -> scope.showForwardToSettingsDialog(deniedList, "您需要手动允许设置中的必要权限", "确定", "取消"))
            .request((allGranted, grantedList, deniedList) -> {
                if (allGranted) {
                    start();
                    //启动服务
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //android8.0以上通过startForegroundService启动service
                        startForegroundService(new Intent(BaseSplashActivity.this, TSITSService.class));
                    }else{
                        startService(new Intent(BaseSplashActivity.this, TSITSService.class));
                    }
                }else{
                    Toast.makeText(BaseSplashActivity.this, "这些权限被拒绝: $拒绝名单", Toast.LENGTH_LONG).show();
                    this.finish();
                }
            });
    }

    /**
     * 启动主界面以及主服务
     */
    protected abstract void start();

    public int getView(){
        return R.layout.activity_splash;
    }
}
