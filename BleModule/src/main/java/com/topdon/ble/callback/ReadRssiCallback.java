package com.topdon.ble.callback;



import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:44
 * author: bichuanfeng
 */
public interface ReadRssiCallback extends RequestFailedCallback {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param request 
 * @param rssi 
     */
    void onRssiRead(Request request, int rssi);
}
