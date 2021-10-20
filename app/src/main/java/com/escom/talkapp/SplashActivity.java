package com.escom.talkapp;

import com.escom.talkapp.R;
import com.tsits.tsmodel.BaseSplashActivity;
import android.content.Intent;
import android.os.Handler;

public class SplashActivity extends BaseSplashActivity {

    @Override
    public int getView() {
        return R.layout.activity_splash;
    }

    @Override
    public void start() {
        new Handler().postDelayed(() -> {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            //结束当前Activity
            SplashActivity.this.finish();
        },100);
    }
}