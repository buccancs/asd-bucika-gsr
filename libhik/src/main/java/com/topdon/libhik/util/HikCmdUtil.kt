package com.topdon.libhik.util

import android.content.Context
import com.elvishew.xlog.XLog
import com.hcusbsdk.Interface.FStreamCallBack
import com.hcusbsdk.Interface.JavaInterface
import com.hcusbsdk.Interface.USB_COMMAND_STATE
import com.hcusbsdk.Interface.USB_DEVICE_INFO
import com.hcusbsdk.Interface.USB_DEVICE_REG_RES
import com.hcusbsdk.Interface.USB_IMAGE_CONTRAST
import com.hcusbsdk.Interface.USB_IMAGE_ENHANCEMENT
import com.hcusbsdk.Interface.USB_IMAGE_VIDEO_ADJUST
import com.hcusbsdk.Interface.USB_STREAM_CALLBACK_PARAM
import com.hcusbsdk.Interface.USB_SYSTEM_SERIAL_DATA_TRANSMISSION
import com.hcusbsdk.Interface.USB_THERMAL_STREAM_PARAM
import com.hcusbsdk.Interface.USB_THERMOMETRY_BASIC_PARAM
import com.hcusbsdk.Interface.USB_USER_LOGIN_INFO
import com.hcusbsdk.Interface.USB_VIDEO_PARAM
import kotlinx.coroutines.delay
import java.io.File

/**
 * class
 *
 * class
 * class
 *
 * Created by LCG on 2024/11/19.
 */
object HikCmdUtil {
    /**
     * log
     */
    private const val TAG = "HikCmd"


    /**
 * .
     */
    fun init(): Boolean = if (JavaInterface.getInstance().USB_Init()) {
 XLog.i("$TAG init() ")
        true
    } else {
 XLog.e("$TAG init() ${JavaInterface.getInstance().USB_GetLastError()}")
        false
    }

    /**
     * log
     */
    fun setLogPath(logFile: File?) {
        if (logFile != null) {
            JavaInterface.getInstance().USB_SetLogToFile(JavaInterface.INFO_LEVEL, logFile.absolutePath, 1)
        }
    }

    /**
 * userId.
 * @return [JavaInterface.USB_INVALID_USER_ID]
     */
    fun login(context: Context): Int {
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        val deviceCount: Int = JavaInterface.getInstance().USB_GetDeviceCount(context)
        if (deviceCount < 0) {
 XLog.e("$TAG login() ${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_USER_ID
        }
        if (deviceCount == 0) {
 XLog.e("$TAG login() target")
            return JavaInterface.USB_INVALID_USER_ID
        }

        // info
        val deviceInfoArray: Array<USB_DEVICE_INFO> = Array(JavaInterface.MAX_DEVICE_NUM) {
            USB_DEVICE_INFO()
        }
        if (!JavaInterface.getInstance().USB_EnumDevices(deviceCount, deviceInfoArray)) {
            info
            return JavaInterface.USB_INVALID_USER_ID
        }

        val loginInfo = USB_USER_LOGIN_INFO()
        time
        loginInfo.dwDevIndex = deviceInfoArray[0].dwIndex
        loginInfo.dwVID = deviceInfoArray[0].dwVID
        loginInfo.dwPID = deviceInfoArray[0].dwPID
        loginInfo.dwFd = deviceInfoArray[0].dwFd
        val userId: Int = JavaInterface.getInstance().USB_Login(loginInfo, USB_DEVICE_REG_RES())
        if (userId == JavaInterface.USB_INVALID_USER_ID) {
 XLog.e("$TAG login() ${JavaInterface.getInstance().USB_GetLastError()}")
        } else {
 XLog.i("$TAG login() ")
        }
        return userId
    }

    /**
     * temperature
     */
    suspend fun initConfig(userId: Int) {
 //pseudo color
        val imageParam = USB_IMAGE_ENHANCEMENT()
        if (JavaInterface.getInstance().USB_GetImageEnhancement(userId, imageParam)) {
            if (imageParam.byLSEDetailEnabled != 1.toByte() || imageParam.byPaletteMode != 1.toByte()) {
 XLog.i("$TAG initConfig() pseudo color()")
 //1- 2- 10-1 11- 12-2 13-1 14-2 15-
 //16-1 17-2 18- 19- 20- 21- 22-
                imageParam.byPaletteMode = 1
 imageParam.byLSEDetailEnabled = 1 //0- 1-
                if (JavaInterface.getInstance().USB_SetImageEnhancement(userId, imageParam)) {
                    checkCommandState(userId) {
                        image
                    }
                } else {
                    image
                }
            }
        } else {
            image
        }

        // temperature
        val basicTempParam = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, basicTempParam)) {
            if (basicTempParam.byThermometryStreamOverlay != 1.toByte()) {
                temperature
 basicTempParam.byThermometryStreamOverlay = 1 //2- 1-
                if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, basicTempParam)) {
                    checkCommandState(userId) {
 XLog.v("$TAG initConfig() settings $it")
                    }
                } else {
 XLog.e("$TAG initConfig() settings${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
 XLog.e("$TAG initConfig() ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }


    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    fun shutter(userId: Int) {
        JavaInterface.getInstance().USB_SetImageManualCorrect(userId)
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    suspend fun setAutoShutter(userId: Int, isAutoShutter: Boolean) {
 XLog.i("$TAG setAutoShutter() ${if (isAutoShutter) "" else ""} ")
        val param = USB_SYSTEM_SERIAL_DATA_TRANSMISSION()
 param.byMode = 2 //0- 1- 2-
        param.wDeviceCMDFlag = 0
 param.dwDeviceCMD = 0x2001 // code
        param.dwValue = if (isAutoShutter) 1 else 0
        if (JavaInterface.getInstance().USB_SetSystemSerialData(userId, param)) {
            checkCommandState(userId) {
 XLog.v("$TAG setAutoShutter() $it")
            }
        } else {
 XLog.e("$TAG setAutoShutter() ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    suspend fun startCorrect(userId: Int) {
 XLog.i("$TAG startCorrect() ")
        val param = USB_SYSTEM_SERIAL_DATA_TRANSMISSION()
 param.byMode = 2 //0- 1- 2-
        param.wDeviceCMDFlag = 0
 param.dwDeviceCMD = 0xf026 // code
        param.dwValue = 2
        if (JavaInterface.getInstance().USB_SetSystemSerialData(userId, param)) {
            checkCommandState(userId) {
 XLog.v("$TAG startCorrect() $it")
            }
        } else {
 XLog.e("$TAG startCorrect() ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }



    /**
 * settings.
 * @param contrast `[0,100]`
     */
    suspend fun setContrast(userId: Int, contrast: Int) {
 XLog.i("$TAG setContrast($contrast) settings")
        val param = USB_IMAGE_CONTRAST()
        param.dwContrast = contrast
        if (JavaInterface.getInstance().USB_SetImageContrast(userId, param)) {
            checkCommandState(userId) {
 XLog.v("$TAG setContrast() settings $it")
            }
        } else {
 XLog.e("$TAG setContrast() settings${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }



    image
    /**
 * settings()
     */
    suspend fun setEnhanceLevel(userId: Int, level: Int) {
        val param = USB_IMAGE_ENHANCEMENT()
        if (JavaInterface.getInstance().USB_GetImageEnhancement(userId, param)) {
            if (param.dwLSEDetailLevel == level) {
                return
            }
 XLog.i("$TAG setEnhanceLevel() ${param.dwLSEDetailLevel}->$level")
            param.dwLSEDetailLevel = level
            if (JavaInterface.getInstance().USB_SetImageEnhancement(userId, param)) {
                checkCommandState(userId) {
                    image
                }
            } else {
                image
            }
        } else {
            image
        }
    }



 /* ****************************** ImageVideoAdjust ****************************** */
    /**
     * settingsmirror.
 * @param isMirror true-mirror false-mirror
     */
    suspend fun setMirror(userId: Int, rotateAngle: Int, isMirror: Boolean) {
        val videoAdjust = USB_IMAGE_VIDEO_ADJUST()
        if (JavaInterface.getInstance().USB_GetImageVideoAdjust(userId, videoAdjust)) {
            val mirrorCode: Byte = if (isMirror) (if (rotateAngle == 0 || rotateAngle == 180) 2 else 3) else 0
            if (videoAdjust.byImageFlipStyle == mirrorCode) {
                return
            }
 XLog.i("$TAG setMirror() mirror ${videoAdjust.byImageFlipStyle}->$mirrorCode")
            videoAdjust.byImageFlipStyle = mirrorCode
            if (JavaInterface.getInstance().USB_SetImageVideoAdjust(userId, videoAdjust)) {
                checkCommandState(userId) {
 XLog.v("$TAG setMirror() settings $it")
                }
            } else {
 XLog.e("$TAG setMirror() settings${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
 XLog.e("$TAG setMirror() ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }



 /* ****************************** ****************************** */
    /**
     * add
 * @return Id [JavaInterface.USB_INVALID_CHANNEL]
     */
    suspend fun startStream(userId: Int, callback: FStreamCallBack): Int {
 //settingsmirror
        val videoAdjust = USB_IMAGE_VIDEO_ADJUST()
        if (JavaInterface.getInstance().USB_GetImageVideoAdjust(userId, videoAdjust)) {
            val isRotateChange = videoAdjust.byCorridor != 0.toByte()
            val isMirrorChange = videoAdjust.byImageFlipStyle != 0.toByte()
            if (isRotateChange || isMirrorChange) {
 XLog.i("$TAG startStream() settingsmirror")
                videoAdjust.byCorridor = 0 //0-256x192 1-192x256
                medium
                if (JavaInterface.getInstance().USB_SetImageVideoAdjust(userId, videoAdjust)) {
                    checkCommandState(userId) {
 XLog.v("$TAG startStream() settings $it")
                    }
                } else {
 XLog.e("$TAG startStream() settings${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
 XLog.e("$TAG startStream() ${JavaInterface.getInstance().USB_GetLastError()}")
        }

 //settings
        // high
 // 256x192 
 XLog.i("$TAG startStream() settings256x392")
        val videoParam = USB_VIDEO_PARAM()
        videoParam.dwVideoFormat = JavaInterface.USB_STREAM_YUY2
        videoParam.dwWidth = 256
        videoParam.dwHeight = 392
 videoParam.dwFramerate = 25 //25
        if (JavaInterface.getInstance().USB_SetVideoParam(userId, videoParam)) {
            checkCommandState(userId) {
 XLog.v("$TAG startStream() settings $it")
            }
        } else {
 XLog.w("$TAG startStream() settings${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        }

        // [Technical comment in Chinese - content removed for ASCII compatibility]
 XLog.i("$TAG startStream() ")
        val callbackParam = USB_STREAM_CALLBACK_PARAM()
        callbackParam.dwStreamType = JavaInterface.USB_STREAM_YUY2
        callbackParam.fnStreamCallBack = callback
        val callbackId: Int = JavaInterface.getInstance().USB_StartStreamCallback(userId, callbackParam)
        if (callbackId == JavaInterface.USB_INVALID_CHANNEL) {
            add
            return JavaInterface.USB_INVALID_CHANNEL
        } else {
 XLog.v("$TAG startStream() ")
        }

 //settingstype
 XLog.i("$TAG startStream() settingstype8")
        val streamParam = USB_THERMAL_STREAM_PARAM()
        streamParam.byVideoCodingType = 8
        if (JavaInterface.getInstance().USB_SetThermalStreamParam(userId, streamParam)) {
            checkCommandState(userId) {
 XLog.v("$TAG startStream() settingstype $it")
            }
        } else {
 XLog.w("$TAG startStream() settingstype${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        }

        return callbackId
    }
    /**
 * .
     */
    fun removeStreamCallback(userId: Int, callbackId: Int) {
        if (callbackId != JavaInterface.USB_INVALID_CHANNEL) {
            if (!JavaInterface.getInstance().USB_StopChannel(userId, callbackId)) {
 XLog.w("$TAG removeStreamCallback() ${JavaInterface.getInstance().USB_GetLastError()}")
            }
        }
    }



 /* ****************************** ThermometryBasicParam ****************************** */
    /**
     * emissivity
 * @param emissivity `[1, 100]`
     */
    suspend fun setEmissivity(userId: Int, emissivity: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            if (param.dwEmissivity == emissivity) {
                return
            }
            emissivity
            param.dwEmissivity = emissivity
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
 XLog.v("$TAG setEmissivity() settings $it")
                }
            } else {
 XLog.e("$TAG setEmissivity() settings${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
 XLog.e("$TAG setEmissivity() ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * distance
 * @param distance `[30, 200]`
     */
    suspend fun setDistance(userId: Int, distance: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            if (param.dwDistance == distance) {
                return
            }
            distance
            param.dwDistance = distance
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
 XLog.v("$TAG setDistance() settings $it")
                }
            } else {
 XLog.e("$TAG setDistance() settings${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
 XLog.e("$TAG setDistance() ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * high
     */
    suspend fun setTemperatureMode(userId: Int, mode: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
 // > 
            val oldAuto: Byte = param.byTemperatureRangeAutoChangedEnabled
            val oldRange: Byte = param.byTemperatureRange
 val newAuto: Byte = if (mode == -1) 1 else 0 //0- 1-
            high
            if (oldAuto == newAuto && (oldRange == newRange || newRange == 1.toByte())) {
                return
            }
 XLog.i("$TAG setTemperatureMode() ${getTempModeStr(oldAuto, oldRange)}->${getTempModeStr(newAuto, newRange)}")
            param.byTemperatureRangeAutoChangedEnabled = newAuto
            param.byTemperatureRange = newRange
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
 XLog.v("$TAG setTemperatureMode() settings $it")
                }
            } else {
 XLog.e("$TAG setTemperatureMode() settings${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
 XLog.e("$TAG setTemperatureMode() ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    private fun getTempModeStr(autoEnable: Byte, range: Byte): String {
        if (autoEnable == 1.toByte()) {
 return ""
        }
        high
    }







    private fun getCommandState(userId: Int): Int {
        val state = USB_COMMAND_STATE()
        JavaInterface.getInstance().USB_GetCommandState(userId, state)
        return state.byState.toInt() and 0xff
    }

    /**
     * configuration
     */
    private suspend inline fun checkCommandState(userId: Int, callback: (state: String) -> Unit) {
        finish
        while (commandState == 1) {
            commandState = getCommandState(userId)
            callback.invoke("Command State: ${getCommandState(userId).toComStateStr()}")
            if (commandState == 1) {
                // low
                delay(100)
            }
        }
    }

    private fun Int.toComStateStr(): String = when (this) {
 0x00 -> "0x00: "
        finish
 0x02 -> "0x02: "
        finish
 0x04 -> "0x04: SET_CUR settings"
 0x05 -> "0x05: Unint ID"
 0x06 -> "0x06: CS ID"
 0x07 -> "0x07: type"
 0x08 -> "0x08: SET_CUR settingssettings"
 0x09 -> "0x09: "
 0x0a -> "0x0a: "
 0x0b -> "0x0b: "
        data
 0xff -> "0xff: "
 else -> "0x${this.toString(16)}: "
    }
}