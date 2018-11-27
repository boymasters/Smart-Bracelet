package com.sanzitech.szitechwisebrave.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mr Q on 2018/01/18.
 */

public class DataBean implements Serializable{
    public String time;
    public String data;
    public List<String> datalist;
    public List<String> timelist;
    public String highdata;
    public String lowdata;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getDatalist() {
        return datalist;
    }

    public void setDatalist(List<String> datalist) {
        this.datalist = datalist;
    }

    public List<String> getTimelist() {
        return timelist;
    }

    public void setTimelist(List<String> timelist) {
        this.timelist = timelist;
    }

    public String getHighdata() {
        return highdata;
    }

    public void setHighdata(String highdata) {
        this.highdata = highdata;
    }

    public String getLowdata() {
        return lowdata;
    }

    public void setLowdata(String lowdata) {
        this.lowdata = lowdata;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "time='" + time + '\'' +
                ", data='" + data + '\'' +
                ", datalist=" + datalist +
                ", timelist=" + timelist +
                ", highdata='" + highdata + '\'' +
                ", lowdata='" + lowdata + '\'' +
                '}';
    }

}
