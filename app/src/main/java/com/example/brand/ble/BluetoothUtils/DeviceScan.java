package com.example.brand.ble.BluetoothUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.brand.ble.MainActivity;

/**
 * Created by brand on 2018-01-16.
 * Pre-Activities: N/A
 * Post-Activities: N/A
 * Layout: N.A
 * Fragments: N/A
 * Description: Used by MainActivity to scan and display available nearby devices that have BLE enabled
 * Resources: https://developer.android.com/guide/topics/connectivity/bluetooth-le.html#connect
 */

public class DeviceScan {
    private BluetoothAdapter mBluetoothAdapter;
    private MainActivity context;
    private boolean bleScanning;
    private Handler bleHandler;
    private int signalStrength;
    private boolean isBlueToothOn;
    // Stops scanning after 10 seconds to save battery life
    private static final long SCAN_DURATION = 10000;

    public DeviceScan(MainActivity context, int signalStrength, boolean isBlueToothOn){
        this.context = context;
        this.bleHandler = new Handler();
        this.signalStrength = signalStrength;
        this.isBlueToothOn = isBlueToothOn;
        final BluetoothManager blueManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = blueManager.getAdapter();
    }

    /**
     * Returns true or false based on if the user's device is currently scanning for another BLE device
     * @return
     */
    public boolean deviceScanning(){
        return bleScanning;
    }

    /**
     * Initiates the scanning of another BLE device in the vicinity
     */
    public void initLeDevice(){
        if(!isBlueToothOn){
            Log.i("DEVICESCAN", "False isBlueToothOn");
            context.stopLeScan();
        }
        else{
            Log.i("DEVICESCAN", "True isBlueToothOn");
            startLeDevice(true);
        }
    }

    public void stopLeDevice(){
        startLeDevice(false);
    }

    /**
     *
     * @param leEnabled == > let the device know whether to continue scanning or to stop based off of the boolean value
     */
    private void startLeDevice(final boolean leEnabled){
        if(leEnabled == true && !bleScanning) {
            Toast.makeText(context, "Starting BLE Scan...", Toast.LENGTH_SHORT).show();

            //Turns off BLE device after 10 seconds
            bleHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Stopping BLE Scan...", Toast.LENGTH_SHORT).show();
                    bleScanning = false;
                    mBluetoothAdapter.stopLeScan(bleScanCallback);
                    context.stopLeScan();
                }
            }, SCAN_DURATION);

            bleScanning = true;
            mBluetoothAdapter.startLeScan(bleScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            final int newRssi = rssi;

            if(rssi > signalStrength){
                bleHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        context.addLeDevice(device, newRssi);
                    }
                });
            }
        }
    };
}
