package com.sanzitech.szitechwisebrave.Observable;


import com.clj.fastble.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
