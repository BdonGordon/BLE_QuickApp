package com.example.brand.ble.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brand.ble.Objects.BleDevice;
import com.example.brand.ble.R;

import java.util.ArrayList;

/**
 * Created by brand on 2018-01-17.
 */

public class BleDeviceAdapter extends RecyclerView.Adapter <BleDeviceAdapter.MyViewHolder> {
    private ArrayList<BleDevice> bleDeviceList;
    private Context context;
    private String deviceInfo;

    public BleDeviceAdapter(Context context, ArrayList<BleDevice> bleDeviceList){
        this.context = context;
        this.bleDeviceList = bleDeviceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_device_cardview, parent, false);
        MyViewHolder mVh = new MyViewHolder(v);

        return mVh;
    }

    @Override
    public void onBindViewHolder(BleDeviceAdapter.MyViewHolder holder, int position) {
        BleDevice device = bleDeviceList.get(position);
        String name = device.getName();
        String macAdd = device.getMacAddress();
        int rssi = device.getRssi();

        if(name != null && name.length() > 0){
            deviceInfo = device.getName() + " || ";
        }
        else{
            deviceInfo = "No Name || RSSI: ";
        }

        //deviceInfo = deviceInfo + Integer.toString(rssi) + " || ";
        deviceInfo = deviceInfo + Integer.toString(rssi);

        /**Optional information to display in the Recyclerview**/
//        if(macAdd != null && macAdd.length() > 0){
//            deviceInfo = deviceInfo + macAdd;
//        }
//        else{
//            deviceInfo = deviceInfo + "No MAC address";
//        }
        holder.mDevices.setText(deviceInfo);
    }

    @Override
    public int getItemCount() {
        return bleDeviceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mDevices;

        public MyViewHolder(View itemView) {
            super(itemView);

            mDevices = itemView.findViewById(R.id.ble_device);
            mDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: connect to the device that is pressed
                }
            });
        }
    }
}
