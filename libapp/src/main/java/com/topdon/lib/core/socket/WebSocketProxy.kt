package com.topdon.lib.core.socket

import android.net.Network
import android.Manifest
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.os.postDelayed
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.Utils
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.bean.event.SocketStateEvent
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.utils.WifiUtil
import com.topdon.lib.core.utils.WifiUtil.getWifiName
import com.topdon.lib.core.utils.WsCmdConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import org.greenrobot.eventbus.EventBus

class WebSocketProxy {

    companion object {
        private const val TS004_URL = "ws://192.168.40.1:888"

        private const val TC007_URL = "ws://192.168.40.1:63206/v1/thermal/temp/template/data"


        @JvmStatic
        private var mWebSocketProxy: WebSocketProxy? = null

        fun getInstance(): WebSocketProxy {
            if (mWebSocketProxy == null) {
                synchronized(WebSocketProxy::class) {
                    if (mWebSocketProxy == null) {
                        mWebSocketProxy = WebSocketProxy()
                    }
                }
            }
            return mWebSocketProxy!!
        }
    }


    private var currentSSID: String? = null
    private var mWsManager: WsManager? = null
    private var webSocketListener: MyWebSocketListener? = null
    private var reconnectHandler = ReconnectHandler()
    private var network : Network ?= null

    private fun getOKHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            //.pingInterval(3, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val originalRequest = chain.request()
                val builder: Request.Builder = originalRequest.newBuilder()
                val compressedRequest: Request = builder.build()
                chain.proceed(compressedRequest)
            })
            .retryOnConnectionFailure(true)
        network?.socketFactory?.let {
            builder.socketFactory(it)
        }
        return builder.build()
    }

    /**
     * TC007 Socket 一帧数据回调，由于没有同时监听多个回调的需求，这里只搞一个就行了。
     */
    private var onFrameListener: ((frame: SocketFrameBean) -> Unit)? = null
    fun setOnFrameListener(activity: ComponentActivity, listener: (frame: SocketFrameBean) -> Unit) {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                onFrameListener = listener
            }

            override fun onDestroy(owner: LifecycleOwner) {
                onFrameListener = null
            }
        })
    }

    var onMessageListener: ((text: String) -> Unit)? = null

    fun startWebSocket(ssid: String, network: Network? = null) {
        if (ssid == currentSSID) {
            if (mWsManager != null) {
                return
            }
            this.network = network
        } else {
            if (reconnectHandler.isReconnecting) {
                EventBus.getDefault().post(SocketStateEvent(false, ssid.startsWith(DeviceConfig.TS004_NAME_START)))
            }
            this.network = network
            currentSSID = ssid
            reconnectHandler.currentSSID = ssid
            stopWebSocket()
        }


        if (mWsManager == null) {
            webSocketListener = MyWebSocketListener(ssid, reconnectHandler, onMessageListener) {
                onFrameListener?.invoke(it)
            }
            mWsManager = WsManager.Builder()
                .client(getOKHttpClient())
                .wsUrl(if (ssid.startsWith(DeviceConfig.TS004_NAME_START)) TS004_URL else TC007_URL)
                .setWsStatusListener(webSocketListener)
                .build()
        }
        mWsManager?.startConnect()
    }

    /**
     * 断开 Socket 连接.
     */
    fun stopWebSocket() {
        webSocketListener?.isNeedReconnect = false
        webSocketListener = null

        mWsManager?.stopConnect()
        mWsManager = null
    }

    fun isConnected(): Boolean = isTS004Connect() || isTC007Connect()

    fun isTS004Connect(): Boolean {
        return currentSSID?.startsWith(DeviceConfig.TS004_NAME_START) == true && mWsManager?.isConnect() == true
    }

    fun isTC007Connect(): Boolean {
        return currentSSID?.startsWith(DeviceConfig.TC007_NAME_START) == true && mWsManager?.isConnect() == true
    }

    fun sendMessage(cmd: String?) {
        mWsManager?.sendMessage(cmd)
    }


    private class MyWebSocketListener(
        val ssid: String,
        val handler: ReconnectHandler,
        val onMessageListener: ((text: String) -> Unit)?,
        val onFrameListener: (frame: SocketFrameBean) -> Unit
    ) : WsManager.IWebSocketListener() {

        /**
         * onFailure 时是否需要重连。
         * 使用该变量是因为，恢复出厂、格式化存储等操作后，由于需要重启会主动断开与设备的连接。
         * 而主动断开操作触发 onFailure 又触发重连从而导致逻辑存在问题。
         * 使用该变量进行区分，当主动断开连接触发 onFailure 时，需不需要执行重连。
         */
        var isNeedReconnect = true

        override fun onOpen(webSocket: WebSocket, response: Response) {
            isNeedReconnect = true
            handler.reset()
            EventBus.getDefault().post(SocketStateEvent(true, ssid.startsWith(DeviceConfig.TS004_NAME_START)))
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (SocketCmdUtil.getCmdResponse(text) == WsCmdConstants.APP_EVENT_HEART_BEATS) {
            } else {
            }
            onMessageListener?.invoke(text)
        }

        /**
         * TC007 温度帧一秒两帧，每帧都输出太过频繁，用该变量控制
         */
        private var needPrint = false
        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            if (ssid.startsWith(DeviceConfig.TC007_NAME_START) && bytes.size == 254 ) {
                val frameBean = SocketFrameBean(bytes.toByteArray())
                onFrameListener.invoke(frameBean)
                needPrint = !needPrint
                if (needPrint) {
                }
            } else {
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (handler.isReconnecting) {
            } else {
                handler.reset()
                EventBus.getDefault().post(SocketStateEvent(false, ssid.startsWith(DeviceConfig.TS004_NAME_START)))
            }
            mWebSocketProxy?.currentSSID = ""
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (checkNeedReconnect()) {
                handler.handleFail(ssid)
                if (!handler.isReconnecting) {
                    EventBus.getDefault().post(SocketStateEvent(false, ssid.startsWith(DeviceConfig.TS004_NAME_START)))
                }
            } else {
                handler.reset()
                getInstance().stopWebSocket()
                EventBus.getDefault().post(SocketStateEvent(false, ssid.startsWith(DeviceConfig.TS004_NAME_START)))
            }
            mWebSocketProxy?.currentSSID = ""
        }

        override fun onHeartBeat(): String? = SocketCmdUtil.getSocketCmd(WsCmdConstants.APP_EVENT_HEART_BEATS)

        override fun onHeartBeatTimeout() {
            handler.handleFail(ssid)
        }

        /**
         * 判断当前是否需要重连
         */
        private fun checkNeedReconnect(): Boolean {
            if (!isNeedReconnect) {
                return false
            }
            if (!XXPermissions.isGranted(Utils.getApp(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                return true
            }
            val wifiName: String = WifiUtil.getCurrentWifiSSID(Utils.getApp()) ?: return true
            return wifiName == ssid
        }
    }

    private class ReconnectHandler : Handler(Looper.getMainLooper()) {
        companion object {
            /**
             * 最大重连次数.
             */
            private const val MAX_RECONNECT_COUNT = 3
            /**
             * 每次重连间隔，单位毫秒.
             */
            private const val RECONNECT_MILLIS = 3000L
        }

        var currentSSID: String = ""
            set(value) {
                if (value != field) {
                    field = value
                    reset()
                }
            }

        var reconnectCount: Int = 0
        var isReconnecting: Boolean = false

        fun reset() {
            reconnectCount = 0
            isReconnecting = false
            removeCallbacksAndMessages(null)
        }

        fun handleFail(currentSSID: String) {
            if (this.currentSSID != currentSSID) {
                return
            }
            if (isReconnecting) {
                reconnectCount++
                if (reconnectCount < MAX_RECONNECT_COUNT) {

                    getInstance().stopWebSocket()
                    removeCallbacksAndMessages(null)
                    postDelayed(RECONNECT_MILLIS) {
                        getInstance().startWebSocket(currentSSID)
                    }
                } else {
                    reconnectCount = 0
                    isReconnecting = false
                    removeCallbacksAndMessages(null)
                    getInstance().stopWebSocket()
                }
            } else {
                reconnectCount = 0
                isReconnecting = true

                getInstance().stopWebSocket()
                removeCallbacksAndMessages(null)
                postDelayed(RECONNECT_MILLIS) {
                    getInstance().startWebSocket(currentSSID)
                }
            }
        }
    }
}