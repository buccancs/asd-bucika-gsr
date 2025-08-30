package com.topdon.lib.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import com.topdon.lib.core.BaseApplication

/**
 * des: WiFi utility class for managing WiFi connections
 * author: CaiSongL
 * date: 2024/5/23 17:39
 */
object EasyWifi {
    private const val TAG = "EasyWifi"
    
    private var wifiConnectCallback: WifiConnectCallback? = null
    private val wifiManager: WifiManager = 
        BaseApplication.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val connectivityManager: ConnectivityManager = 
        BaseApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    enum class WiFiEncryptionStandard {
        WEP,
        WPA_EAP,
        WPA_PSK,
        WPA2,
        WPA3
    }

    enum class WifiCapability {
        WIFI_CIPHER_WEP,
        WIFI_CIPHER_WPA,
        WIFI_CIPHER_NO_PASS
    }

    interface WifiConnectCallback {
        fun onFailure()
        fun onSuccess(network: Network)
    }

    fun useWifiFirst() {
        @Suppress("DEPRECATION")
        connectivityManager.setNetworkPreference(1)
    }

    fun setWifiConnectCallback(callback: WifiConnectCallback?) {
        this.wifiConnectCallback = callback
    }

    fun isWifiEnabled(): Boolean = wifiManager.isWifiEnabled

    fun getWifiManager(): WifiManager = wifiManager

    fun getConnectivityManager(): ConnectivityManager = connectivityManager

    fun connectByNew(ssid: String, password: String) {
        connectByNew(ssid, password, WiFiEncryptionStandard.WPA2)
    }

    fun connectByNew(ssid: String, password: String, standard: WiFiEncryptionStandard) {
        val networkSpecifier = when (standard) {
            WiFiEncryptionStandard.WPA3 -> WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa3Passphrase(password)
                .build()
            else -> WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .setNetworkSpecifier(networkSpecifier)
            .build()

        connectivityManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                wifiConnectCallback?.onSuccess(network)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                wifiConnectCallback?.onFailure()
            }
        })
    }

    @Suppress("DEPRECATION")
    fun connectByOld(ssid: String, password: String, capability: WifiCapability): Boolean {
        val networkId = wifiManager.addNetwork(createWifiConfig(ssid, password, capability))
        return if (networkId == -1) {
            false
        } else {
            wifiManager.enableNetwork(networkId, true)
        }
    }

    @Suppress("DEPRECATION")
    private fun isExist(ssid: String): WifiConfiguration? {
        return wifiManager.configuredNetworks?.find { 
            it.SSID == "\"$ssid\"" 
        }
    }

    @Suppress("DEPRECATION")
    private fun createWifiConfig(ssid: String, password: String, capability: WifiCapability): WifiConfiguration {
        return WifiConfiguration().apply {
            allowedAuthAlgorithms.clear()
            allowedGroupCiphers.clear()
            allowedKeyManagement.clear()
            allowedPairwiseCiphers.clear()
            allowedProtocols.clear()
            SSID = "\"$ssid\""

            // Check if exists and remove if needed
            isExist(ssid)?.let { /* Handle existing configuration */ }

            when (capability) {
                WifiCapability.WIFI_CIPHER_NO_PASS -> {
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                }
                WifiCapability.WIFI_CIPHER_WEP -> {
                    hiddenSSID = true
                    wepKeys[0] = "\"$password\""
                    allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    wepTxKeyIndex = 0
                }
                WifiCapability.WIFI_CIPHER_WPA -> {
                    preSharedKey = "\"$password\""
                    hiddenSSID = true
                    allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                    allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                    allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                    allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                    status = WifiConfiguration.Status.ENABLED
                    priority = 100000
                }
            }
        }
    }

    fun isNetConnected(connectivityManager: ConnectivityManager): Boolean {
        return connectivityManager.activeNetwork != null
    }

    fun isWifi(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    fun setNetworkType(netType: NetType) {
        val builder = NetworkRequest.Builder()
        when (netType) {
            NetType.WIFI -> builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            else -> builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
        
        connectivityManager.requestNetwork(builder.build(), object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                try {
                    connectivityManager.bindProcessToNetwork(network)
                } catch (e: Exception) {
                    // Handle exception silently
                }
            }
        })
    }

    fun getConnectSSID(): String {
        return wifiManager.connectionInfo.ssid
    }
}