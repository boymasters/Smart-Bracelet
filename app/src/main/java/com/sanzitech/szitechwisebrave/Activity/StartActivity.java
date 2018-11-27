package com.sanzitech.szitechwisebrave.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.sanzitech.szitechwisebrave.R;
import com.sanzitech.szitechwisebrave.ToolUtils.SharedPreferencesUtils;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.sendEmptyMessageDelayed(1,2000);
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                    break;
            }
        }
    };


}