package com.tsits.tsmodel.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.tsits.tsmodel.utils.ViewBindingUtil;

/**
 * @author： YY
 * @date： 2020/8/12
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    protected T mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideBottomUIMenu();
        super.onCreate(savedInstanceState);
        mViewBinding = ViewBindingUtil.inflate(getClass(), getLayoutInflater());
        setContentView(mViewBinding.getRoot());

    }

    @Override
    protected void onResume() {
        hideBottomUIMenu();
        super.onResume();
    }

    @Override
    protected void onPause() {
        hideBottomUIMenu();
        super.onPause();
    }

    /**
     * 隐藏虚拟按键
     */
    public void hideBottomUIMenu() {
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }



}
