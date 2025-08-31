package com.topdon.lib.core.usb

import android.app.PendingIntent
import android.content.Context
import android.hardware.usb.*
import android.os.Build
import androidx.annotation.RequiresApi
import com.topdon.lib.core.matrix.MatrixUtils
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Enhanced USB Manager for thermal camera and device management
 * Consolidated from GuideUsbManager with improved functionality and error handling
 */
class EnhancedUsbManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "EnhancedUsbManager"
        
        // USB Endpoint addresses
        const val ADDRESS_ENDPOINT_DATA_IN = 129
        const val ADDRESS_ENDPOINT_CONTROL_OUT = 2
        const val ADDRESS_ENDPOINT_CONTROL_IN = 131
        
        // USB device constants
        const val VENDOR_ID_GUIDE = 0x0B05
        const val PRODUCT_ID_GUIDE = 0x4500
        
        // Result codes
        const val RESULT_SUCCESS = 0
        const val RESULT_ERROR_CONNECT_DEVICE_FAILED = -1
        const val RESULT_ERROR_NO_PERMISSION = -2
        const val RESULT_ERROR_DEVICE_NOT_FOUND = -3
        const val RESULT_ERROR_INTERFACE_NOT_FOUND = -4
        
        @Volatile
        private var INSTANCE: EnhancedUsbManager? = null
        
        fun getInstance(context: Context): EnhancedUsbManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EnhancedUsbManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // USB components
    private var usbManager: UsbManager? = null
    private var usbDevice: UsbDevice? = null
    private var connection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null
    private var endpointDataIn: UsbEndpoint? = null
    private var endpointControlOut: UsbEndpoint? = null
    private var endpointControlIn: UsbEndpoint? = null
    
    // Device management
    private val connectedDevices = ConcurrentHashMap<String, UsbDevice>()
    private val deviceListeners = mutableSetOf<DeviceConnectionListener>()
    
    // Coroutine scope for async operations
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Buffer management
    private var dataBuffer: ByteArray? = null
    private var bufferSize: Int = 8192
    
    // Connection state
    private var isConnected: Boolean = false
    private var isInitialized: Boolean = false

    init {
        initializeUsbManager()
    }

    private fun initializeUsbManager() {
        try {
            usbManager = context.getSystemService(Context.USB_SERVICE) as? UsbManager
            isInitialized = true
            scanForDevices()
        } catch (e: Exception) {
            // Handle initialization error
            isInitialized = false
        }
    }

    /**
     * Scan for available USB devices
     */
    fun scanForDevices(): List<UsbDevice> {
        val deviceList = mutableListOf<UsbDevice>()
        usbManager?.deviceList?.values?.forEach { device ->
            if (isSupportedDevice(device)) {
                deviceList.add(device)
                connectedDevices[device.deviceName] = device
            }
        }
        return deviceList
    }

    /**
     * Check if device is supported (thermal cameras, etc.)
     */
    private fun isSupportedDevice(device: UsbDevice): Boolean {
        return device.vendorId == VENDOR_ID_GUIDE || 
               device.productId == PRODUCT_ID_GUIDE ||
               isThermalCamera(device)
    }
    
    private fun isThermalCamera(device: UsbDevice): Boolean {
        // Check for common thermal camera vendor IDs
        val thermalVendors = listOf(0x0B05, 0x1234, 0x5678) // Add actual vendor IDs
        return device.vendorId in thermalVendors
    }

    /**
     * Connect to a USB device
     */
    suspend fun connectToDevice(device: UsbDevice): Int = withContext(Dispatchers.IO) {
        try {
            if (!hasPermission(device)) {
                return@withContext RESULT_ERROR_NO_PERMISSION
            }

            connection = usbManager?.openDevice(device)
            if (connection == null) {
                return@withContext RESULT_ERROR_CONNECT_DEVICE_FAILED
            }

            // Find the appropriate interface
            usbInterface = findInterface(device)
            if (usbInterface == null) {
                return@withContext RESULT_ERROR_INTERFACE_NOT_FOUND
            }

            // Claim the interface
            val claimed = connection!!.claimInterface(usbInterface, true)
            if (!claimed) {
                return@withContext RESULT_ERROR_CONNECT_DEVICE_FAILED
            }

            // Setup endpoints
            setupEndpoints(usbInterface!!)

            usbDevice = device
            isConnected = true
            
            // Initialize data buffer
            dataBuffer = ByteArray(bufferSize)

            // Notify listeners
            notifyDeviceConnected(device)

            return@withContext RESULT_SUCCESS
        } catch (e: Exception) {
            disconnect()
            return@withContext RESULT_ERROR_CONNECT_DEVICE_FAILED
        }
    }

    private fun findInterface(device: UsbDevice): UsbInterface? {
        for (i in 0 until device.interfaceCount) {
            val intf = device.getInterface(i)
            if (intf.interfaceClass == UsbConstants.USB_CLASS_VENDOR_SPEC) {
                return intf
            }
        }
        return device.getInterface(0) // Fallback to first interface
    }

    private fun setupEndpoints(usbInterface: UsbInterface) {
        for (i in 0 until usbInterface.endpointCount) {
            val endpoint = usbInterface.getEndpoint(i)
            when (endpoint.address) {
                ADDRESS_ENDPOINT_DATA_IN -> endpointDataIn = endpoint
                ADDRESS_ENDPOINT_CONTROL_OUT -> endpointControlOut = endpoint
                ADDRESS_ENDPOINT_CONTROL_IN -> endpointControlIn = endpoint
            }
        }
    }

    /**
     * Check if we have permission for the device
     */
    fun hasPermission(device: UsbDevice): Boolean {
        return usbManager?.hasPermission(device) ?: false
    }

    /**
     * Request permission for a device
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(device: UsbDevice, pendingIntent: PendingIntent) {
        usbManager?.requestPermission(device, pendingIntent)
    }

    /**
     * Read data from the device
     */
    suspend fun readData(timeout: Int = 5000): ByteArray? = withContext(Dispatchers.IO) {
        if (!isConnected || connection == null || endpointDataIn == null || dataBuffer == null) {
            return@withContext null
        }

        try {
            val bytesRead = connection!!.bulkTransfer(endpointDataIn!!, dataBuffer!!, dataBuffer!!.size, timeout)
            if (bytesRead > 0) {
                return@withContext dataBuffer!!.copyOf(bytesRead)
            }
        } catch (e: Exception) {
            // Handle read error
        }
        return@withContext null
    }

    /**
     * Write data to the device
     */
    suspend fun writeData(data: ByteArray, timeout: Int = 5000): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected || connection == null || endpointControlOut == null) {
            return@withContext false
        }

        try {
            val bytesWritten = connection!!.bulkTransfer(endpointControlOut!!, data, data.size, timeout)
            return@withContext bytesWritten == data.size
        } catch (e: Exception) {
            return@withContext false
        }
    }

    /**
     * Send control command
     */
    suspend fun sendControlCommand(
        requestType: Int,
        request: Int,
        value: Int,
        index: Int,
        buffer: ByteArray? = null,
        timeout: Int = 5000
    ): Int = withContext(Dispatchers.IO) {
        if (!isConnected || connection == null) {
            return@withContext -1
        }

        try {
            return@withContext connection!!.controlTransfer(
                requestType, request, value, index, buffer, buffer?.size ?: 0, timeout
            )
        } catch (e: Exception) {
            return@withContext -1
        }
    }

    /**
     * Disconnect from the current device
     */
    fun disconnect() {
        scope.launch {
            try {
                usbInterface?.let { intf ->
                    connection?.releaseInterface(intf)
                }
                connection?.close()
                
                val device = usbDevice
                
                connection = null
                usbInterface = null
                usbDevice = null
                endpointDataIn = null
                endpointControlOut = null
                endpointControlIn = null
                isConnected = false
                dataBuffer = null

                device?.let { notifyDeviceDisconnected(it) }
            } catch (e: Exception) {
                // Handle disconnect error
            }
        }
    }

    /**
     * Get connected device info
     */
    fun getConnectedDeviceInfo(): DeviceInfo? {
        return usbDevice?.let { device ->
            DeviceInfo(
                deviceName = device.deviceName,
                vendorId = device.vendorId,
                productId = device.productId,
                serialNumber = device.serialNumber,
                manufacturerName = device.manufacturerName,
                productName = device.productName
            )
        }
    }

    /**
     * Set buffer size for data operations
     */
    fun setBufferSize(size: Int) {
        if (size > 0) {
            bufferSize = size
            if (isConnected) {
                dataBuffer = ByteArray(bufferSize)
            }
        }
    }

    /**
     * Add device connection listener
     */
    fun addDeviceConnectionListener(listener: DeviceConnectionListener) {
        deviceListeners.add(listener)
    }

    /**
     * Remove device connection listener
     */
    fun removeDeviceConnectionListener(listener: DeviceConnectionListener) {
        deviceListeners.remove(listener)
    }

    private fun notifyDeviceConnected(device: UsbDevice) {
        deviceListeners.forEach { it.onDeviceConnected(device) }
    }

    private fun notifyDeviceDisconnected(device: UsbDevice) {
        deviceListeners.forEach { it.onDeviceDisconnected(device) }
    }

    /**
     * Check connection status
     */
    fun isDeviceConnected(): Boolean = isConnected

    /**
     * Get all available devices
     */
    fun getAvailableDevices(): Map<String, UsbDevice> {
        return connectedDevices.toMap()
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        disconnect()
        scope.cancel()
        deviceListeners.clear()
        connectedDevices.clear()
        INSTANCE = null
    }

    /**
     * Device information data class
     */
    data class DeviceInfo(
        val deviceName: String,
        val vendorId: Int,
        val productId: Int,
        val serialNumber: String?,
        val manufacturerName: String?,
        val productName: String?
    )

    /**
     * Device connection listener interface
     */
    interface DeviceConnectionListener {
        fun onDeviceConnected(device: UsbDevice)
        fun onDeviceDisconnected(device: UsbDevice)
    }

    /**
     * Enhanced data reading with thermal processing
     */
    suspend fun readThermalData(timeout: Int = 5000): ThermalData? = withContext(Dispatchers.IO) {
        val rawData = readData(timeout) ?: return@withContext null
        
        try {
            // Process thermal data using MatrixUtils
            val processedData = MatrixUtils.processThermalData(rawData)
            return@withContext ThermalData(
                rawData = rawData,
                processedData = processedData,
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            return@withContext null
        }
    }

    /**
     * Thermal data container
     */
    data class ThermalData(
        val rawData: ByteArray,
        val processedData: FloatArray,
        val timestamp: Long
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ThermalData

            if (!rawData.contentEquals(other.rawData)) return false
            if (!processedData.contentEquals(other.processedData)) return false
            if (timestamp != other.timestamp) return false

            return true
        }

        override fun hashCode(): Int {
            var result = rawData.contentHashCode()
            result = 31 * result + processedData.contentHashCode()
            result = 31 * result + timestamp.hashCode()
            return result
        }
    }
}