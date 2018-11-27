package com.sanzitech.szitechwisebrave.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanzitech.szitechwisebrave.R;
import com.sanzitech.szitechwisebrave.ToolUtils.SharedPreferencesUtils;
import com.sanzitech.szitechwisebrave.ToolUtils.ToastUtils;
import com.sanzitech.szitechwisebrave.ViewUtils.BubbleSeekBar;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;

/**
 * Created by Mr Q on 2018/01/09.
 */

public class SetActivity extends BaseActivity implements View.OnClickListener {
    private SwitchButton switchButton;
    private ImageView back;
    private BubbleSeekBar temperature_BubbleSeekBar;
    private BubbleSeekBar sphygmus_BubbleSeekBar;
    private BubbleSeekBar time_BubbleSeekBar;

    private TextView temperature_TextView;
    private TextView sphygmus_TextView;
    private TextView time_TextView;
    private TextView music;
    private View statusBar;


    @Override
    protected int setLayoutId() {
        return R.layout.set_activity_main;
    }

    @Override
    protected void initView() {
        statusBar = findViewById(R.id.statusBarView);
        back = (ImageView) findViewById(R.id.set_back);
        switchButton = (SwitchButton) findViewById(R.id.set_switch_button);
        temperature_BubbleSeekBar = (BubbleSeekBar) findViewById(R.id.set_temperature_BubbleSeekBar);
        sphygmus_BubbleSeekBar = (BubbleSeekBar) findViewById(R.id.set_sphygmus_BubbleSeekBar);
        time_BubbleSeekBar = (BubbleSeekBar) findViewById(R.id.set_time_BubbleSeekBar);
        temperature_TextView = (TextView) findViewById(R.id.set_temperature_textview);
        sphygmus_TextView = (TextView) findViewById(R.id.set_sphygmus_textview);
        time_TextView = (TextView) findViewById(R.id.set_time_textview);
        music = (TextView) findViewById(R.id.set_music);
    }

    @Override
    protected void initData() {
        ViewGroup.LayoutParams params = statusBar.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBar.setLayoutParams(params);
        back.setOnClickListener(this);
        music.setOnClickListener(this);

        switchButton.setChecked(getMyApplication().isIs_police());
        temperature_TextView.setText(String.valueOf(getMyApplication().getTemperature_police_line()) + " ℃");
        sphygmus_TextView.setText(String.valueOf(getMyApplication().getSphygmus_police_line()) + " 次/分");
        time_TextView.setText(String.valueOf(getMyApplication().getTime_piloce_line())+ " 分钟");
        temperature_BubbleSeekBar.setProgress(getMyApplication().getTemperature_police_line());
        sphygmus_BubbleSeekBar.setProgress(getMyApplication().getSphygmus_police_line());
        time_BubbleSeekBar.setProgress(getMyApplication().getTime_piloce_line());
        try {
            if (!getMyApplication().getPath_media().isEmpty()) {
                music.setText(getFileName(getRealPathFromUri(this, Uri.parse(getMyApplication().getPath_media()))) + " >");
            }
        }catch (Exception e){
            e.printStackTrace();
            music.setText("无");
        }

    }

    @Override
    protected void setListener() {
        temperature_BubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                temperature_TextView.setText(String.valueOf(progressFloat) + " ℃");
                getMyApplication().setTemperature_police_line(progressFloat);
                SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).put("temperature_police_line", progressFloat);
            }
        });
        sphygmus_BubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                sphygmus_TextView.setText(String.valueOf(progress) + " 次/分");
                getMyApplication().setSphygmus_police_line(progress);
                SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).put("sphygmus_police_line", progress);
            }
        });
        time_BubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                time_TextView.setText(String.valueOf(progress) + " 分钟");
                getMyApplication().setTime_piloce_line(progress);
                SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).put("time_piloce_line", progress);
            }
        });
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                getMyApplication().setIs_police(isChecked);
                SharedPreferencesUtils.init(getApplicationContext(), getMyApplication().SetConfig).put("is_police", isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back:
                finish();
                break;
            case R.id.set_music:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(this)) {
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                            checkPermissions();
                        } else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } else {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                        doTask();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    @TargetApi(23)
    private void checkPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), 110);
        }else {
            doTask();
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 110:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doTask();
                } else {
                    ToastUtils.showToast(getApplicationContext(), "获取权限失败，请手动开启");
                }
                break;
            default:
                break;
        }
    }


    private void doTask() {
        AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int mCurVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if (((float) mCurVolume / (float) maxVolume) < 0.0) {
            ToastUtils.showToast(this, "请保持闹铃音量大于50%");
        } else {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI ,Uri.parse(getMyApplication().getPath_media()));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "闹钟铃声");
            startActivityForResult(intent, 1);
        }
    }

    /**
     * 当设置铃声之后的回调函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        try {
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (pickedUri != null) {
                switch (requestCode) {
                    case 1:
                        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, pickedUri);
                        music.setText(getFileName(getRealPathFromUri(getApplicationContext(),pickedUri))+" >");
                        getMyApplication().setPath_media(pickedUri.toString());
                        SharedPreferencesUtils.init(getApplicationContext(),getMyApplication().SetConfig).put("path_media",pickedUri.toString());
                        break;
                }
            }else {
                ToastUtils.showToast(getApplicationContext(),"铃声不可设置“无”");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return "";
        }
    }


    /**
     * 检测是否存在指定的文件夹,如果不存在则创建
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
