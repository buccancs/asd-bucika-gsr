package com.topdon.ble.callback;




import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:39
 * author: bichuanfeng
 */
public interface RequestFailedCallback extends RequestCallback {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param request 
 * @param failType type{@link Connection#REQUEST_FAIL_TYPE_GATT_IS_NULL}
     * data
     */
    void onRequestFailed(Request request, int failType, Object value);
}
