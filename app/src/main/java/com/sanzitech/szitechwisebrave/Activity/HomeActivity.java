package com.sanzitech.szitechwisebrave.Activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.sanzitech.szitechwisebrave.Observable.Observer;
import com.sanzitech.szitechwisebrave.Observable.ObserverManager;
import com.sanzitech.szitechwisebrave.R;
import com.sanzitech.szitechwisebrave.ToolUtils.SharedPreferencesUtils;


public class HomeActivity extends BaseActivity implements View.OnClickListener, Observer {
    private View statusBar;
    private ImageView match;
    private ImageView setting;
    private ImageView real_time;
    private ImageView history_data;
    private LinearLayout linearLayout;

    private TextView temperature_data;
    private TextView sphygmus_data;
    private FrameLayout temperature_bg;
    private FrameLayout sphygmus_bg;

    private boolean isConnect = false;

    private Intent intent;

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    private int charaProp;

    @Override
    protected int setLayoutId() {
        return R.layout.home_activity_main;
    }

    @Override
    protected void initView() {
        linearLayout = (LinearLayout) findViewById(R.id.home_bg);
        statusBar = findViewById(R.id.statusBarView);
        match = (ImageView) findViewById(R.id.home_match);
        setting = (ImageView) findViewById(R.id.home_set);
        real_time = (ImageView) findViewById(R.id.home_realtime);
        history_data = (ImageView) findViewById(R.id.home_historydata);
        temperature_data = (TextView) findViewById(R.id.temperature_data);
        sphygmus_data = (TextView) findViewById(R.id.sphygmus_data);
        temperature_bg = (FrameLayout) findViewById(R.id.temperature_bg);
        sphygmus_bg = (FrameLayout) findViewById(R.id.sphygmus_bg);
    }

    @Override
    protected void initData() {
        LayoutParams params = statusBar.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBar.setLayoutParams(params);
        getMyApplication()
                .setTemperature_police_line(SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).getFloat("temperature_police_line", getMyApplication().temperature_police_line));
        getMyApplication().setSphygmus_police_line(SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).getInt("sphygmus_police_line", getMyApplication().sphygmus_police_line));
        getMyApplication().setTime_piloce_line(SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).getInt("time_piloce_line", getMyApplication().time_piloce_line));
        getMyApplication().setIs_police(SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).getBoolean("is_police", getMyApplication().is_police));
        getMyApplication().setPath_media(SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).getString("path_media", getMyApplication().path_media));
    }

    @Override
    protected void setListener() {
        match.setOnClickListener(this);
        setting.setOnClickListener(this);
        real_time.setOnClickListener(this);
        history_data.setOnClickListener(this);
        temperature_bg.setOnClickListener(this);
        sphygmus_bg.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_match:
                intent = new Intent(this, MatchActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.home_set:
                intent = new Intent(this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.home_realtime:
                if (!isConnect) {
                    isConnect = true;
                    real_time.setImageResource(R.drawable.homepage_truetime3);
                } else {
                    isConnect = false;
                    real_time.setImageResource(R.drawable.selector_start);
                }
                break;
            case R.id.home_historydata:
                intent = new Intent(this, HistoryListActivity.class);
                startActivity(intent);
                break;
            case R.id.temperature_bg:
                intent = new Intent(this, HistoryDataActivity.class);
                intent.putExtra("title","体温");
                intent.putExtra("istemp",true);
                startActivity(intent);
                break;
            case R.id.sphygmus_bg:
                intent = new Intent(this, HistoryDataActivity.class);
                intent.putExtra("title","脉搏");
                intent.putExtra("istemp",false);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().clearCharacterCallback(bleDevice);
        ObserverManager.getInstance().deleteObserver(HomeActivity.this);
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    @Override
    public void disConnected(BleDevice device) {
        if (device != null && bleDevice != null && device.getKey().equals(bleDevice.getKey())) {
//            finish();
        }
    }
}
