package com.topdon.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * date: 2021/8/12 13:45
 * author: bichuanfeng
 */
public interface Connection {
    UUID clientCharacteristicConfig = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int REQUEST_FAIL_TYPE_REQUEST_FAILED = 0;
    int REQUEST_FAIL_TYPE_CHARACTERISTIC_NOT_EXIST = 1;
    int REQUEST_FAIL_TYPE_DESCRIPTOR_NOT_EXIST = 2;
    int REQUEST_FAIL_TYPE_SERVICE_NOT_EXIST = 3;
    /**
 * [BluetoothGatt.GATT_SUCCESS]
     */
    int REQUEST_FAIL_TYPE_GATT_STATUS_FAILED = 4;
    int REQUEST_FAIL_TYPE_GATT_IS_NULL = 5;
    int REQUEST_FAIL_TYPE_BLUETOOTH_ADAPTER_DISABLED = 6;
    int REQUEST_FAIL_TYPE_REQUEST_TIMEOUT = 7;
    int REQUEST_FAIL_TYPE_CONNECTION_DISCONNECTED = 8;
    int REQUEST_FAIL_TYPE_CONNECTION_RELEASED = 9;

 //----------type---------
    int TIMEOUT_TYPE_CANNOT_DISCOVER_DEVICE = 0;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int TIMEOUT_TYPE_CANNOT_CONNECT = 1;
    /**
 * [BluetoothGattCallback.onServicesDiscovered]
     */
    int TIMEOUT_TYPE_CANNOT_DISCOVER_SERVICES = 2;

 //-------------type-------------------
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int CONNECT_FAIL_TYPE_MAXIMUM_RECONNECTION = 1;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int CONNECT_FAIL_TYPE_CONNECTION_IS_UNSUPPORTED = 2;

    @NonNull
    Device getDevice();

    /**
 * settings
     */
    int getMtu();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    void reconnect();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    void disconnect();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    void refresh();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    void release();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    void releaseNoEvent();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @NonNull
    ConnectionState getConnectionState();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    boolean isAutoReconnectEnabled();

    @Nullable
    BluetoothGatt getGatt();

    /**
     * event
     */
    void clearRequestQueue();

    /**
     * medium
     */
    void clearRequestQueueByType(RequestType type);

    @NonNull
    ConnectionConfiguration getConnectionConfiguration();

    @Nullable
    BluetoothGattService getService(UUID service);

    @Nullable
    BluetoothGattCharacteristic getCharacteristic(UUID service, UUID characteristic);

    @Nullable
    BluetoothGattDescriptor getDescriptor(UUID service, UUID characteristic, UUID descriptor);

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    void execute(Request request);

    /**
 * Indication
     */
    boolean isNotificationOrIndicationEnabled(BluetoothGattCharacteristic characteristic);

    /**
 * Indication
     */
    boolean isNotificationOrIndicationEnabled(UUID service, UUID characteristic);

    /**
 * settings
     */
    void setBluetoothGattCallback(BluetoothGattCallback callback);

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param service UUID
 * @param characteristic UUID
 * @param property {@link BluetoothGattCharacteristic#PROPERTY_WRITE}
     */
    boolean hasProperty(UUID service, UUID characteristic, int property);
}
