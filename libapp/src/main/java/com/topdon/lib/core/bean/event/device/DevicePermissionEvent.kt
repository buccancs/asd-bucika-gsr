package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * event
 * @param device 
 */
data class DevicePermissionEvent(val device: UsbDevice)
