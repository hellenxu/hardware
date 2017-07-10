package ca.six.hardware.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ca.six.hardware.Constant.INTENT_DEVICE_CONNECTED;
import static ca.six.hardware.Constant.INTENT_DEVICE_DISCONNECTED;
import static ca.six.hardware.Constant.INTENT_SERVICES_DISCOVERED;

/**
 * @author hellenxu
 * @date 2017-07-10
 * Copyright 2017 Six. All rights reserved.
 */

public class BleDeviceService extends Service {
    private Binder bleBinder = new BleBinder();
    private List<BluetoothGattService> gattServices = new ArrayList<BluetoothGattService>();
    private BluetoothGatt bleGatt;
    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private int connectionState;

    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = BluetoothProfile.STATE_CONNECTED;
                broadcastUpdate(INTENT_DEVICE_CONNECTED);
                bleGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = BluetoothProfile.STATE_DISCONNECTED;
                broadcastUpdate(INTENT_DEVICE_DISCONNECTED);
                disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                gattServices = bleGatt.getServices();
                broadcastUpdate(INTENT_SERVICES_DISCOVERED);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    private class BleBinder extends Binder {
    }

    public boolean initialization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bleAdapter = bleManager.getAdapter();
        } else {
            bleAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        return bleAdapter != null;
    }

    public void connect(String deviceAddress) {
        BluetoothDevice device = bleAdapter.getRemoteDevice(deviceAddress);
        if (device != null) {
            bleGatt = device.connectGatt(this, false, bleGattCallback);
            connectionState = BluetoothProfile.STATE_CONNECTING;
        }
    }

    public List<BluetoothGattService> getSupportedService() {
        return gattServices;
    }

    //TODO
    public void readCharacteristics() {

    }

    //TODO
    public void writeCharacteristics() {

    }

    //TODO
    public void disconnect() {

    }

    //TODO
    public void broadcastUpdate(String action){
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return bleBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (bleGatt != null) {
            bleGatt.close();
            bleGatt = null;
        }
        return super.onUnbind(intent);
    }

}
