package com.sanzitech.szitechwisebrave.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.sanzitech.szitechwisebrave.Adapter.DeviceAdapter;
import com.sanzitech.szitechwisebrave.Observable.ObserverManager;
import com.sanzitech.szitechwisebrave.R;
import com.sanzitech.szitechwisebrave.ToolUtils.ToastUtils;
import com.sanzitech.szitechwisebrave.ViewUtils.SearchingAnimView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mr Q on 2018/01/16.
 */

public class MatchActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MatchActivity.class.getSimpleName();

    private ImageView search;
    private ImageView back;
    private TextView tip;
    private TextView title;
    private LinearLayout linearLayout;
    private SearchingAnimView searchingAnimView;
    private View statusBar;
    private ImageView imageView;

    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    private DeviceAdapter mDeviceAdapter;

    private ListView listView;
    private Intent intent;


    @Override
    protected int setLayoutId() {
        return R.layout.match_activity;
    }

    @Override
    protected void initView() {
        statusBar = findViewById(R.id.statusBarView);
        linearLayout = (LinearLayout) findViewById(R.id.match_search_bg);
        search = (ImageView) findViewById(R.id.match_search);
        back = (ImageView) findViewById(R.id.match_back);
        tip = (TextView) findViewById(R.id.match_search_tip);
        title = (TextView) findViewById(R.id.match_title);
        searchingAnimView = (SearchingAnimView) findViewById(R.id.SearchingAnimView);
        listView = (ListView) findViewById(R.id.match_search_listview);
        imageView = (ImageView) findViewById(R.id.match_search_image);
    }

    @Override
    protected void initData() {
        ViewGroup.LayoutParams params = statusBar.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBar.setLayoutParams(params);
        mDeviceAdapter = new DeviceAdapter(this);
        listView.setAdapter(mDeviceAdapter);
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }
        });
    }

    private void setScanRule() {
        boolean isAutoConnect = true;
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, "3ZIL_TPMeter")
                .setAutoConnect(isAutoConnect)
                .setScanTimeOut(20 * 1000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                searchingAnimView.startAnimations();
                tip.setText(R.string.search_srarching);
                imageView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                searchingAnimView.stopAnimations();
                tip.setText(R.string.search_start);
                imageView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.match_back:
                if (searchingAnimView.isRunning())
                    searchingAnimView.stopAnimations();
                finish();
                break;
            case R.id.match_search:
                if (searchingAnimView.isRunning()) {
                    BleManager.getInstance().cancelScan();
                } else {
                    checkPermissions();
                }
                break;
        }
    }

    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            ToastUtils.showToast(this, getString(R.string.please_open_blue));
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })
                            .setCancelable(false)
                            .show();
                } else {
                    setScanRule();
                    startScan();
                }
                break;
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                setScanRule();
                startScan();
            }
        }
    }

    private void connect(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
            }

            @Override
            public void onConnectFail(BleException exception) {
                ToastUtils.showToast(MatchActivity.this, getString(R.string.connect_fail));
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    intent = new Intent();
                    intent.putExtra("KEY_DATA", bleDevice);
                    setResult(2, intent);
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                mDeviceAdapter.removeDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
                if (!isActiveDisConnected) {
                    ToastUtils.showToast(MatchActivity.this, getString(R.string.disconnected));
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }
            }
        });
    }

}
