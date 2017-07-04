package ca.six.hardware.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

    private static final int BLUETOOTH_ENABLE_REQUEST = 100;
    private static final int SCAN_PERIOD = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ble_list);

        mBleAdapter = BluetoothAdapter.getDefaultAdapter();
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice(isBluetoothEnabled() && !isScanning);
            }
        });

        mDeviceAdapter = new BleDeviceAdapter(this, bluetoothDeviceList);
        RecyclerView rvDeviceList = (RecyclerView) findViewById(R.id.rv_device_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceList.setAdapter(mDeviceAdapter);
    }

    //check whether bluetooth is enabled.
    public boolean isBluetoothEnabled(){
        return null != mBleAdapter && mBleAdapter.isEnabled() && !mBleAdapter.isDiscovering();
    }

    public void requestBluetoothEnable(){
        if(null != mBleAdapter && !mBleAdapter.isEnabled()){
            Intent enabledBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabledBluetooth, BLUETOOTH_ENABLE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == BLUETOOTH_ENABLE_REQUEST){
            //do things after bluetooth enabled
        }
    }

    private void scanLeDevice(final boolean enable){
        if(enable){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    mBleAdapter.stopLeScan(leScanCallback);
                    mDeviceAdapter.setData(bluetoothDeviceList);
                    tvStatus.setText(R.string.start_scan);
                }
            }, SCAN_PERIOD);
            isScanning = true;
            mBleAdapter.startLeScan(leScanCallback);
            tvStatus.setText(R.string.stop_scan);
        } else {
            isScanning = false;
            mBleAdapter.stopLeScan(leScanCallback);
        }
    }

    //rssi: received signal strength indicator
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            bluetoothDeviceList.add(device);
            System.out.println("xxl - rssi: " + rssi);
        }
    };
}
