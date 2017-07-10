package ca.six.hardware.ble;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.six.hardware.Constant;
import ca.six.hardware.ble.sample.SampleGattAttributes;
import ca.six.hareware.R;

import static ca.six.hardware.Constant.INTENT_DEVICE_CONNECTED;
import static ca.six.hardware.Constant.INTENT_DEVICE_DISCONNECTED;
import static ca.six.hardware.Constant.INTENT_SERVICES_DISCOVERED;
import static ca.six.hardware.Constant.KEY_NAME;
import static ca.six.hardware.Constant.KEY_UUID;

/**
 * @author hellenxu
 * @date 2017-07-10
 * Copyright 2017 Six. All rights reserved.
 */

public class DeviceDetailsActivity extends Activity {
    private TextView tvConnectState;
    private TextView tvDataValue;
    private ExpandableListView elvCharacteristics;
    private BluetoothDevice selectedDevice;
    private BleDeviceService bleService;
    private List<List<BluetoothGattCharacteristic>> bleGatts;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO handle sort of intents
            String receivedAction = intent.getAction();
            switch (receivedAction) {
                case INTENT_DEVICE_CONNECTED:
                    tvConnectState.setText(R.string.connected);
                    break;
                case INTENT_SERVICES_DISCOVERED:
                    displayServices(bleService.getSupportedService());
                    break;
                case INTENT_DEVICE_DISCONNECTED:
                    tvConnectState.setText(R.string.disconnected);
                    break;
                default:
                    break;
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = (BleDeviceService) service;
            if (!bleService.initialization()) {
                System.out.println("xxl-BleDeviceService initialization failed...");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("xxl-Device details.");
        selectedDevice = getIntent().getParcelableExtra(Constant.BUNDLE_KEY_CHOSEN_DEVICE);
        setContentView(R.layout.gatt_services_characteristics);

        setupUI();
        Intent bleSerIntent = new Intent(this, BleDeviceService.class);
        bindService(bleSerIntent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private void setupUI() {
        tvConnectState = (TextView) findViewById(R.id.connection_state);
        tvDataValue = (TextView) findViewById(R.id.data_value);
        elvCharacteristics = (ExpandableListView) findViewById(R.id.gatt_services_list);
        ((TextView) findViewById(R.id.device_address)).setText(selectedDevice.getAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_DEVICE_CONNECTED);
        intentFilter.addAction(INTENT_DEVICE_DISCONNECTED);
        intentFilter.addAction(INTENT_SERVICES_DISCOVERED);
        return intentFilter;
    }

    private void displayServices(List<BluetoothGattService> services) {
        if (services == null) {
            return;
        }
        String uuid;
        String unknownService = "Unknown Service";
        String unknownCharacteristic = "Unknown Characteristic";
        bleGatts = new ArrayList<List<BluetoothGattCharacteristic>>();
        List<HashMap<String, String>> servicesData = new ArrayList<HashMap<String, String>>();
        List<List<HashMap<String, String>>> gattCharaData = new ArrayList<List<HashMap<String, String>>>();

        for (BluetoothGattService service : services) {
            uuid = service.getUuid().toString();
            HashMap<String, String> currentService = new HashMap<String, String>();
            currentService.put(KEY_NAME, SampleGattAttributes.lookup(uuid, unknownService));
            currentService.put(KEY_UUID, uuid);
            servicesData.add(currentService);

            List<BluetoothGattCharacteristic> charas = service.getCharacteristics();
            List<HashMap<String, String>> gatt = new ArrayList<HashMap<String, String>>();
            for (BluetoothGattCharacteristic chara : charas) {
                uuid = chara.getUuid().toString();
                HashMap<String, String> currentChara = new HashMap<String, String>();
                currentChara.put(KEY_NAME, SampleGattAttributes.lookup(uuid, unknownCharacteristic));
                currentChara.put(KEY_UUID, uuid);
                gatt.add(currentChara);
            }
            gattCharaData.add(gatt);
            bleGatts.add(charas);
        }

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, servicesData,
                android.R.layout.simple_list_item_2, new String[]{KEY_NAME, KEY_UUID},
                new int[]{android.R.id.text1, android.R.id.text2}, gattCharaData,
                android.R.layout.simple_list_item_2, new String[]{KEY_NAME, KEY_UUID},
                new int[]{android.R.id.text1, android.R.id.text2});
        elvCharacteristics.setAdapter(adapter);
    }
}
