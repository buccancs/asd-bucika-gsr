package com.topdon.libhik.util

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.hcusbsdk.Interface.FStreamCallBack
import com.hcusbsdk.Interface.JavaInterface
import com.hcusbsdk.Interface.USB_FRAME_INFO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays

/**
 * help
 *
 * data
 * data
 * temperature
 *
 * settingssettings
 * - [onReadyListener]
 *
 * settings
 * - [onTimeoutListener]
 *
 * [Technical comment in Chinese - content removed for ASCII compatibility]
 * - [init] 
 * - [startStream] 
 * - [stopStream] 
 * - [release] 
 *
 * [ComponentActivity] 
 * [bind] 4
 *
 * class
 *
 * Created by LCG on 2024/11/19.
 */
object HikHelper {
    /**
 * Id.
     */
    private var userId: Int = JavaInterface.USB_INVALID_USER_ID
    /**
 * Id.
     */
    private var callbackId: Int = JavaInterface.USB_INVALID_CHANNEL

    /**
 * Job
     */
    private var timeoutJob: Job? = null
    /**
     * data
     */
    private val streamCallBack = MyFStreamCallBack()


    /**
 * Activity .
     */
    fun bind(activity: ComponentActivity) {
        init(activity)
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                owner.lifecycleScope.launch {
                    startStream()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                stopStream()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                release()
            }
        })
    }

    /**
 * Fragment .
     */
    fun bind(fragment: Fragment) {
        init(fragment.requireContext())
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                owner.lifecycleScope.launch {
                    startStream()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                stopStream()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                release()
            }
        })
    }

    /**
 * USB_Cleanup
     */
    private var hasInit = false
    /**
 * .
 * @return true- false-
     */
    fun init(context: Context): Boolean {
 if (userId != JavaInterface.USB_INVALID_USER_ID) {//
            return true
        }

        // [Technical comment in Chinese - content removed for ASCII compatibility]
        if (!hasInit) {
 if (!HikCmdUtil.init()) {//
                return false
            }
            hasInit = true
        }

        // log
        HikCmdUtil.setLogPath(context.getExternalFilesDir("hkLog"))

        // [Technical comment in Chinese - content removed for ASCII compatibility]
        userId = HikCmdUtil.login(context)
        return userId != JavaInterface.USB_INVALID_USER_ID
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @Volatile
    private var wantCheckTimeout = false
    /**
 * .
     */
    suspend fun startStream() {
        if (callbackId == JavaInterface.USB_INVALID_CHANNEL) {
            callbackId = HikCmdUtil.startStream(userId, streamCallBack)
            wantCheckTimeout = true
            timeoutJob = CoroutineScope(Dispatchers.Main).launch {
                while (wantCheckTimeout) {
 delay(5000) //5
                    if (System.currentTimeMillis() - streamCallBack.beforeFrameTime > 5000) {
                        wantCheckTimeout = false
                        onTimeoutListener?.invoke()
                    }
                }
            }
            onReadyListener?.invoke()
        }
    }

    /**
     * temperature
     */
    fun stopStream() {
        if (callbackId != JavaInterface.USB_INVALID_CHANNEL) {
            timeoutJob?.cancel()
            HikCmdUtil.removeStreamCallback(userId, callbackId)
            callbackId = JavaInterface.USB_INVALID_CHANNEL
            wantCheckTimeout = false
        }
    }

    /**
 * .
     */
    fun release() {
 if (userId != JavaInterface.USB_INVALID_USER_ID) {//
            JavaInterface.getInstance().USB_Logout(userId)
            userId = JavaInterface.USB_INVALID_USER_ID
        }
        if (hasInit) {
            JavaInterface.getInstance().USB_Cleanup()
            hasInit = false
        }
        streamCallBack.onTempListener = null
        streamCallBack.onFrameListener = null
        onTimeoutListener = null
    }

    /**
     * data
     */
    fun setFrameListener(listener : ((ByteArray, ByteArray) -> Unit)) {
        streamCallBack.onFrameListener = listener
    }
    /**
     * temperature
     */
    fun setTempListener(listener : ((ByteArray) -> Unit)) {
        streamCallBack.onTempListener = listener
    }

    /**
     * time
     */
    var onTimeoutListener: (() -> Unit)? = null
    /**
     * configuration
     */
    var onReadyListener: (() -> Unit)? = null

    /**
     * temperature
     */
    fun copyFrameData(): ByteArray = streamCallBack.copyFrameArray()


    private class MyFStreamCallBack : FStreamCallBack {
        /**
 * YUV YUY2 
         */
        private val yuvArray = ByteArray(256 * 192 * 2)
        /**
         * temperature
         */
        private val tempArray = ByteArray(256 * 192 * 2)
        /**
         * temperature
         */
        @Volatile
        var onTempListener: ((ByteArray) -> Unit)? = null
        /**
         * temperature
         */
        @Volatile
        var onFrameListener: ((ByteArray, ByteArray) -> Unit)? = null

        /**
         * time
         */
        @Volatile
        var beforeFrameTime: Long = 0

        /**
         * temperature
         */
        fun copyFrameArray(): ByteArray = synchronized(this) {
            val frameArray = ByteArray(256 * 192 * 4)
            System.arraycopy(yuvArray, 0, frameArray, 0, yuvArray.size)
            System.arraycopy(tempArray, 0, frameArray, yuvArray.size, tempArray.size)
            frameArray
        }

        override fun invoke(handle: Int, frameInfo: USB_FRAME_INFO) {
            beforeFrameTime = System.currentTimeMillis()
            if (frameInfo.dwBufSize != 4640 + 256 * 192 * 4) {
                data
                return
            }
            val dataArray: ByteArray = Arrays.copyOf(frameInfo.pBuf, frameInfo.dwBufSize)


            /*val frameHead = FrameHead(dataArray)
            XLog.d(frameHead.toString())
            for (i in frameHead.tempRuleList.indices) {
                XLog.v(frameHead.tempRuleList[i].toString())
                XLog.v(dataArray.buildPrintStr(216 + i * 208, 88))
            }*/

            synchronized(this) {
                System.arraycopy(dataArray, dataArray.size - yuvArray.size, yuvArray, 0, yuvArray.size)
                for (i in tempArray.indices step 2) {
                    val newValue = ((dataArray[4640 + i].toInt() and 0xff) or (dataArray[4640 + i + 1].toInt() and 0xff shl 8)) + 14272
                    tempArray[i] = (newValue and 0xff).toByte()
                    tempArray[i + 1] = (newValue shr 8 and 0xff).toByte()
                }
            }
            onTempListener?.invoke(tempArray)
            onFrameListener?.invoke(yuvArray, tempArray)
        }
    }

    /**
     * temperature
     */
    suspend fun initConfig() = withContext(Dispatchers.IO) {
        HikCmdUtil.initConfig(userId)
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    suspend fun shutter() = withContext(Dispatchers.IO) {
        HikCmdUtil.shutter(userId)
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    suspend fun setAutoShutter(isAutoShutter: Boolean) = withContext(Dispatchers.IO) {
        HikCmdUtil.setAutoShutter(userId, isAutoShutter)
    }

    /**
 * settings `[0,100]`
     */
    suspend fun setContrast(value: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setContrast(userId, value)
    }

    /**
 * `[0,100]`
     */
    suspend fun setEnhanceLevel(value: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setEnhanceLevel(userId, value)
    }

    /**
     * settingsmirror
     */
    suspend fun setMirror(rotateAngle: Int, isMirror: Boolean) = withContext(Dispatchers.IO) {
        HikCmdUtil.setMirror(userId, rotateAngle, isMirror)
    }

    /**
     * emissivity
 * @param emissivity `[1, 100]`
     */
    suspend fun setEmissivity(emissivity: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setEmissivity(userId, emissivity)
    }

    /**
     * distance
 * @param distance `[30, 200]`
     */
    suspend fun setDistance(distance: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setDistance(userId, distance)
    }

    /**
     * high
     */
    suspend fun setTemperatureMode(@IntRange(-1, 1) mode: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setTemperatureMode(userId, mode)
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    suspend fun startCorrect() = withContext(Dispatchers.IO) {
        HikCmdUtil.startCorrect(userId)
    }
}