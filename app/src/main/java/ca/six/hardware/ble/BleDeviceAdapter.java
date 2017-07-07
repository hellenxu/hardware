package ca.six.hardware.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.six.hareware.R;

/**
 * @author hellenxu
 * @date 2017-07-04
 * Copyright 2017 Six. All rights reserved.
 */

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleDeviceHolder>{
    private LayoutInflater inflater;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    public BleDeviceAdapter(Context ctx, List<BluetoothDevice> data){
        if(null == ctx){
            throw new IllegalArgumentException("Context is null");
        }

        inflater = LayoutInflater.from(ctx);
        if(null != data){
            deviceList = data;
        }
    }

    public void setData(List<BluetoothDevice> data){
        if(null != data) {
            deviceList = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public BleDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_device_list, parent, false);
        return new BleDeviceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BleDeviceHolder holder, int position) {
        final BluetoothDevice device = deviceList.get(position);
        final String deviceName = TextUtils.isEmpty(device.getName()) ? "Unknown" : device.getName();
        final String deviceAddress = TextUtils.isEmpty(device.getAddress()) ? "Unknown" : device.getAddress();
        holder.tvDeviceName.setText(deviceName);
        holder.tvDeviceMac.setText(deviceAddress);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class BleDeviceHolder extends RecyclerView.ViewHolder{
        TextView tvDeviceName, tvDeviceMac;

        BleDeviceHolder(View itemView) {
            super(itemView);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
            tvDeviceMac = (TextView) itemView.findViewById(R.id.tv_device_mac);
        }
    }
}
