package com.example.brand.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brand.ble.Adapters.BleDeviceAdapter;
import com.example.brand.ble.BluetoothUtils.DeviceScan;
import com.example.brand.ble.BluetoothUtils.GattServerCallBack;
import com.example.brand.ble.Objects.BleDevice;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Pre-Activities: N/A
 * Post-Activities: N/A
 * Layout: activity_main.xml
 * Fragments: N/A
 * Description: This activity will perform all of the functions that are required by Mr. Amir from Momentaj inc.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mTransmitButton, mScanButton;
    private LinearLayout mNotifsBox;
    private BluetoothAdapter mBlueAdapter;
    private final static int REQUEST_ENABLE_BT = 1; //request code
    private boolean isBluetoothOn = false;
    private DeviceScan bleScanner;
    private HashMap<String, BleDevice> bleDeviceMap;
    private ArrayList<BleDevice> bleDeviceList;
    //RecyclerView
    private RecyclerView mBleRecycler;
    private LinearLayoutManager mLayout;
    private RecyclerView.Adapter mBleAdapter;
    private BluetoothLeAdvertiser mBlueAdvertiser;


    /**
     * Simply initializes UI components to a variable in this Activity.
     * It also checks if Bluetooth is enabled in the device or not.
     */
    public void initComponents(){
        mTransmitButton = findViewById(R.id.transmission_button);
        mScanButton = findViewById(R.id.scan_button);
        mNotifsBox = findViewById(R.id.notification_layout);
        final BluetoothManager blueManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBlueAdapter = blueManager.getAdapter();

        //Pop-up to ask user to enable Bluetooth if not enabled already
        if(mBlueAdapter == null || !mBlueAdapter.isEnabled()){
            Intent enableBlue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlue, REQUEST_ENABLE_BT);
            isBluetoothOn = false;
        }
        else{
            isBluetoothOn = true;
        }

        bleScanner = new DeviceScan(this, -75, isBluetoothOn);
        bleDeviceMap = new HashMap<>();
        bleDeviceList = new ArrayList<>();
    }

    /**
     * Checks if current device has BLE capability. If it does not, the application will terminate.
     */
    public void bleCheck(){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "Bluetooth LE is not supported on this device.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(this, "Your device supports BLE.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bleCheck();
        initComponents();

        mTransmitButton.setOnClickListener(this);
        mScanButton.setOnClickListener(this);

        mBleRecycler = findViewById(R.id.recycler_view);
        mBleRecycler.setHasFixedSize(true);
        mLayout = new LinearLayoutManager(this);
        mBleRecycler.setLayoutManager(mLayout);
        mBleAdapter = new BleDeviceAdapter(getApplicationContext(), bleDeviceList);

        mBleRecycler.setAdapter(mBleAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mBlueAdapter.isMultipleAdvertisementSupported()){
            finish();
            return;
        }

//        mBlueAdvertiser = mBlueAdapter.getBluetoothLeAdvertiser();
//        GattServerCallBack gattServerCallBack = new GattServerCallBack();
    }

    public void startLeScan(){
        mScanButton.setText("Stop Scan");
        mScanButton.setBackground(getApplicationContext().getDrawable(R.drawable.scan_button_red));
        bleDeviceList.clear();
        bleDeviceMap.clear();
        mBleAdapter.notifyDataSetChanged();
        bleScanner.initLeDevice();
    }

    public void stopLeScan(){
        mScanButton.setText("Start Scan");
        mScanButton.setBackground(getApplicationContext().getDrawable(R.drawable.scan_button_green));
        bleScanner.stopLeDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLeScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLeScan();
    }

    /**
     * When a device is scanned, it will be added into a list that will be displayed.
     * @param device
     * @param rssi
     */
    public void addLeDevice(BluetoothDevice device, int rssi){
        String macAdd = device.getAddress();
        BleDevice bleDevice;

        //If the device does not exist in the map then we want to initialize and create a BleDevice object out of it
        if(!bleDeviceMap.containsKey(macAdd)){
            bleDevice = new BleDevice(device);
            bleDevice.setRssi(rssi);

            bleDeviceMap.put(macAdd, bleDevice);
            bleDeviceList.add(bleDevice); //will be put into a recyclerview
        }
        else{
            bleDeviceMap.get(macAdd).setRssi(rssi);
        }
        mBleAdapter.notifyDataSetChanged(); //TODO: Initialize the adapter
    }


    @Override
    public void onClick(View v) {

        if(v == mTransmitButton){
            if(mBlueAdapter == null || !mBlueAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(), "Please enable bluetooth.", Toast.LENGTH_SHORT).show();
            }
            else{
                //TODO: after all the checks and data is validated to send and if the device is connected to another
                TextView notif = new TextView(this);
                notif.setText(getResources().getString(R.string.data_sending));
                mNotifsBox.addView(notif);
                //TODO: disable button until data is received
            }
        }

        else if(v == mScanButton){
            if(!bleScanner.deviceScanning()) {
                startLeScan();
            }
            else{
                stopLeScan();
            }
        }
    }
}
