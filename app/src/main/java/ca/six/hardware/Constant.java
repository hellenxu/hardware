package ca.six.hardware;

/**
 * @author hellenxu
 * @date 2017-07-10
 * Copyright 2017 Six. All rights reserved.
 */

public interface Constant {
    // ble
    String BUNDLE_KEY_CHOSEN_DEVICE = "SelectedDevice";
    String INTENT_DEVICE_CONNECTED = "ca.six.hardware.ble.device_connected";
    String INTENT_SERVICES_DISCOVERED = "ca.six.hardware.ble.service_discovered";
    String INTENT_DEVICE_DISCONNECTED = "ca.six.hardware.ble.device_disconnected";
    String KEY_NAME = "name";
    String KEY_UUID = "uuid";
}
