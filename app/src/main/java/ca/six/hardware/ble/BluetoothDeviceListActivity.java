package ca.six.hardware.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.six.hardware.Constant;
import ca.six.hareware.R;

/**
 * @copyright six.ca
 * Created by Xiaolin on 2017-06-26.
 */

public class BluetoothDeviceListActivity extends Activity {
    private BluetoothAdapter mBleAdapter;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private Handler mHandler = new Handler();
    private boolean isScanning;
    private TextView tvStatus;
    private BleDeviceAdapter mDeviceAdapter;
    private BluetoothGatt mBluetoothGatt;
    private Context ctx;

    private static final int BLUETOOTH_ENABLE_REQUEST = 100;
    private static final int SCAN_PERIOD = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ble_list);

        ctx = this;
        mBleAdapter = BluetoothAdapter.getDefaultAdapter();
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice(isBluetoothEnabled() && !isScanning);
            }
        });

        mDeviceAdapter = new BleDeviceAdapter(this, bluetoothDeviceList);
        RecyclerView rvDevice = (RecyclerView) findViewById(R.id.rv_device_list);
        rvDevice.setLayoutManager(new LinearLayoutManager(this));
        rvDevice.setAdapter(mDeviceAdapter);
        rvDevice.addOnItemTouchListener(new BleDeviceOnItemClickListener(this, rvDevice));
    }

    //check whether bluetooth is enabled.
    public boolean isBluetoothEnabled() {
        return null != mBleAdapter && mBleAdapter.isEnabled() && !mBleAdapter.isDiscovering();
    }

    public void requestBluetoothEnable() {
        if (null != mBleAdapter && !mBleAdapter.isEnabled()) {
            Intent enabledBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabledBluetooth, BLUETOOTH_ENABLE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == BLUETOOTH_ENABLE_REQUEST) {
            //do things after bluetooth enabled
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    mBleAdapter.stopLeScan(leScanCallback);
                    mDeviceAdapter.setData(bluetoothDeviceList);
                    tvStatus.setText(R.string.menu_scan);
                }
            }, SCAN_PERIOD);
            isScanning = true;
            mBleAdapter.startLeScan(leScanCallback);
            tvStatus.setText(R.string.menu_stop);
        } else {
            isScanning = false;
            mBleAdapter.stopLeScan(leScanCallback);
        }
    }

    //rssi: received signal strength indicator
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!bluetoothDeviceList.contains(device)) {
                bluetoothDeviceList.add(device);
                mDeviceAdapter.setData(bluetoothDeviceList);
                System.out.println("xxl - rssi: " + rssi);
            }
        }
    };

    private class BleDeviceOnItemClickListener extends ItemClickListener {

        public BleDeviceOnItemClickListener(Context ctx, RecyclerView rv) {
            super(ctx, rv);
        }

        @Override
        public void onItemClick(RecyclerView.ViewHolder vh) {
            // connect device
            final int selectedPosition = vh.getAdapterPosition();
            System.out.println("xxl-selected: " + selectedPosition);

            BluetoothDevice selectedDevice = null;
            if (selectedPosition > bluetoothDeviceList.size() || selectedPosition < 0) {
                throw new IndexOutOfBoundsException("invalid index");
            } else {
                selectedDevice = bluetoothDeviceList.get(selectedPosition);
            }
            if (selectedDevice != null) {
                connectDevice(selectedDevice);
            } else {
                throw new IllegalArgumentException("Selected bluetooth device is null.");
            }
        }
    }

    private void connectDevice(BluetoothDevice device) {
        Intent deviceDetailsIntent = new Intent(this, DeviceDetailsActivity.class);
        deviceDetailsIntent.putExtra(Constant.BUNDLE_KEY_CHOSEN_DEVICE, device);
        startActivity(deviceDetailsIntent);
    }

//    private void connectDevice(BluetoothDevice device) {
//        mBluetoothGatt = device.connectGatt(this, false, gattCallback);
//    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("xxl-GATT-SUCCESS.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            System.out.println("xxl-onServiceDiscovered");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            System.out.println("xxl-onCharacteristicRead");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            System.out.println("xxl-onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            System.out.println("xxl-onCharacteristicChanged");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            System.out.println("xxl-onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            System.out.println("xxl-onDescriptorRead");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            System.out.println("xxl-onDescriptorRead");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            System.out.println("xxl-onDescriptorRead");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            System.out.println("xxl-onDescriptorRead");
        }
    };
}
