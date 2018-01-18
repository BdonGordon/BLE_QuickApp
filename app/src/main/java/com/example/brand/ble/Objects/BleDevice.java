package com.example.brand.ble.Objects;

import android.bluetooth.BluetoothDevice;

/**
 * Created by brand on 2018-01-17.
 * Description: Object for the BLE devices
 */

public class BleDevice {
    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BleDevice(BluetoothDevice bluetoothDevice){
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getMacAddress(){
        return bluetoothDevice.getAddress();
    }

    public String getName(){
        return bluetoothDevice.getName();
    }

    public void setRssi(int rssi){
        this.rssi = rssi;
    }

    public int getRssi(){
        return this.rssi;
    }

}
