package com.sanzitech.szitechwisebrave.Application;

import android.app.Application;

/**
 * Created by Mr Q on 2018/01/16.
 */

public class MyApplication extends Application {
    /**
     * shareprepreferences设置表名
     */
    public static final String SetConfig = "SetConfig";
    /**
     * 设置基本配置
     */
    public float temperature_police_line = (float) 38.0;
    public int sphygmus_police_line = 100;
    public int time_piloce_line = 1;
    public boolean is_police = true;
    public String path_media = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public float getTemperature_police_line() {
        return temperature_police_line;
    }

    public void setTemperature_police_line(float temperature_police_line) {
        this.temperature_police_line = temperature_police_line;
    }

    public int getSphygmus_police_line() {
        return sphygmus_police_line;
    }

    public void setSphygmus_police_line(int sphygmus_police_line) {
        this.sphygmus_police_line = sphygmus_police_line;
    }

    public int getTime_piloce_line() {
        return time_piloce_line;
    }

    public void setTime_piloce_line(int time_piloce_line) {
        this.time_piloce_line = time_piloce_line;
    }

    public boolean isIs_police() {
        return is_police;
    }

    public void setIs_police(boolean is_police) {
        this.is_police = is_police;
    }

    public String getPath_media() {
        return path_media;
    }

    public void setPath_media(String path_media) {
        this.path_media = path_media;
    }
}
