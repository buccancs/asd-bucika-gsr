package com.topdon.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.topdon.commons.observer.Observer;

import java.util.UUID;


/**
 * event
 * <p>
 * date: 2021/8/12 13:15
 * author: bichuanfeng
 */
public interface EventObserver extends Observer {
    /**
     * change
     *
 * @param state {@link BluetoothAdapter#STATE_OFF}
     */
    default void onBluetoothAdapterStateChanged(int state) {
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param request 
     * data
     */
    default void onCharacteristicRead(Request request, byte[] value) {
    }

    /**
     * change
     *
 * @param device 
 * @param service UUID
 * @param characteristic UUID
     * data
     */
    default void onCharacteristicChanged(Device device, UUID service, UUID characteristic,
                                         byte[] value) {
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param request 
     * data
     */
    default void onCharacteristicWrite(Request request, byte[] value) {
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param request 
 * @param rssi 
     */
    default void onRssiRead(Request request, int rssi) {
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param request 
     * data
     */
    default void onDescriptorRead(Request request, byte[] value) {
    }

    /**
     * change
     *
 * @param request 
 * @param isEnabled 
     */
    default void onNotificationChanged(Request request, boolean isEnabled) {
    }

    /**
     * change
     *
 * @param request 
 * @param mtu 
     */
    default void onMtuChanged(Request request, int mtu) {
    }

    /**
 * @param request 
 * @param txPhy {@link BluetoothDevice#PHY_LE_1M_MASK}
 * @param rxPhy {@link BluetoothDevice#PHY_LE_1M_MASK}
     */
    default void onPhyChange(Request request, int txPhy, int rxPhy) {
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param request 
 * @param failType type{@link Connection#REQUEST_FAIL_TYPE_GATT_IS_NULL}
     * data
     */
    default void onRequestFailed(Request request, int failType, Object value) {
    }

    /**
     * change
     *
 * @param device {@link Device#getConnectionState()}{@link ConnectionState#CONNECTED}
     */
    default void onConnectionStateChanged(Device device) {
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param device 
 * @param failType type{@link Connection#CONNECT_FAIL_TYPE_MAXIMUM_RECONNECTION}
     */
    default void onConnectFailed(Device device, int failType) {
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param device 
 * @param type {@link Connection#TIMEOUT_TYPE_CANNOT_CONNECT}
     */
    default void onConnectTimeout(Device device, int type) {
    }
}
