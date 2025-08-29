package com.guide.zm04c.matrix

object ResultCode {

    val TAG = "mobilelibrary"

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val READY_CONNECT_DEVICE = 1

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val SUCC_FIND_MATCHED_DEVICE = 2

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val SUCC_FIND_DEVICE_INTERFACE = 3

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val SUCC_CONNECT_INTERFACE = 4

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val SUCC_FIND_ENDPOINT = 5

 //USB
    val SUCC_USB_SEND_CMD = 6


 // USB
    val ERROR_FIND_DEVICE_NOT_MATCH = -100

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val ERROR_NOT_FIND_DEVICE = -101

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val ERROR_NOT_FIND_INTERFACE = -102

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val ERROR_OPEN_DEVICE_FAILD = -103

    // [Technical comment in Chinese - content removed for ASCII compatibility]
    val ERROR_CONNECT_DEVICE_FAILD = -104

    // input
    val ERROR_FIND_ENDPOINT_FAILD = -105

 //USB
    val ERROR_USE_NOT_AGRREN_PERMISSIONS = -106

    //usbisvalid
    val ERROR_USE_USB_ISVALID = -107

 //USB
    val ERROE_USB_SEND_CMD_FAILD = -108
}