package com.topdon.ble;

/**
 * type
 * <p>
 * date: 2019/8/9 22:10
 * author: bichuanfeng
 */
public enum RequestType {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    SET_NOTIFICATION,
    /**
 * Indication
     */
    SET_INDICATION,
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    READ_CHARACTERISTIC,
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    READ_DESCRIPTOR,
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    READ_RSSI,
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    WRITE_CHARACTERISTIC,
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    CHANGE_MTU,
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    READ_PHY,
    /**
 * settings
     */
    SET_PREFERRED_PHY
}
