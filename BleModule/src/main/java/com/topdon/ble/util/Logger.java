package com.topdon.ble.util;

/**
 * date: 2019/8/2 23:56
 * author: bichuanfeng
 */
public interface Logger {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int TYPE_GENERAL = 0;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int TYPE_SCAN_STATE = 1;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int TYPE_CONNECTION_STATE = 2;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int TYPE_CHARACTERISTIC_READ = 3;
    /**
     * change
     */
    int TYPE_CHARACTERISTIC_CHANGED = 4;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int TYPE_READ_REMOTE_RSSI = 5;
    /**
     * change
     */
    int TYPE_MTU_CHANGED = 6;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    int TYPE_REQUEST_FAILED = 7;
    int TYPE_DESCRIPTOR_READ = 8;
    int TYPE_NOTIFICATION_CHANGED = 9;
    int TYPE_INDICATION_CHANGED = 10;
    int TYPE_CHARACTERISTIC_WRITE = 11;
    int TYPE_PHY_CHANGE = 12;

    /**
     * log
     *
     * level
     * log
     * log
     */
    void log(int priority, int type, String msg);

    /**
     * log
     *
     * level
     * log
     * log
 * @param th 
     */
    void log(int priority, int type, String msg, Throwable th);
    
    /**
     * log
     */
    void setEnabled(boolean isEnabled);

    /**
     * log
     */
    boolean isEnabled();
}
