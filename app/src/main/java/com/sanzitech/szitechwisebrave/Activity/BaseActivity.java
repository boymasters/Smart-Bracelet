package com.sanzitech.szitechwisebrave.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.clj.fastble.BleManager;
import com.gyf.barlibrary.ImmersionBar;
import com.sanzitech.szitechwisebrave.Application.MyApplication;
import com.sanzitech.szitechwisebrave.R;

/**
 * Created by Mr Q on 2018/01/09.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private InputMethodManager imm;
    protected ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        //初始化沉浸式
        if (isImmersionBarEnabled())
            initImmersionBar();
        BleManager.getInstance().init(getApplication());

        BleManager.getInstance()
                .enableLog(false)
                .setMaxConnectCount(1)
                .setOperateTimeout(5000);
        //初始化数据
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.imm = null;
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //在BaseActivity里销毁
    }


    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }
    protected abstract int setLayoutId();

    protected abstract void initView() ;

    protected abstract void initData() ;

    protected abstract void setListener() ;

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }


    public int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public MyApplication getMyApplication() {
        return (MyApplication)getApplicationContext();
    }
}