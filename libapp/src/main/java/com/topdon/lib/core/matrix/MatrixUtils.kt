package com.topdon.lib.core.matrix

import java.text.DecimalFormat
import java.util.*
import kotlin.experimental.and

/**
 * Enhanced matrix data processing utilities consolidating libmatrix functionality.
 * Provides comprehensive byte array manipulation, data type conversion, and matrix operations.
 */
object MatrixUtils {

    private const val TAG = "MatrixUtils"
    private var df: DecimalFormat? = null

    /**
     * Convert short to little-endian byte array.
     */
    @JvmStatic
    fun convertShortToLittleEndianByteArray(value: Short): ByteArray {
        val shortByteArray = ByteArray(2)
        shortByteArray[0] = (value and 0xff).toByte()
        shortByteArray[1] = (value.toInt() ushr 8 and 0xff).toByte()
        return shortByteArray
    }

    /**
     * Convert short to big-endian byte array.
     */
    @JvmStatic
    fun convertShortToBigEndianByteArray(value: Short): ByteArray {
        val shortByteArray = ByteArray(2)
        shortByteArray[0] = (value.toInt() ushr 8 and 0xff).toByte()
        shortByteArray[1] = (value and 0xff).toByte()
        return shortByteArray
    }

    /**
     * Convert int to little-endian byte array.
     */
    @JvmStatic
    fun convertIntToLittleEndianByteArray(value: Int): ByteArray {
        val intByteArray = ByteArray(4)
        intByteArray[0] = (value and 0xff).toByte()
        intByteArray[1] = (value ushr 8 and 0xff).toByte()
        intByteArray[2] = (value ushr 16 and 0xff).toByte()
        intByteArray[3] = (value ushr 24 and 0xff).toByte()
        return intByteArray
    }

    /**
     * Convert int to big-endian byte array.
     */
    @JvmStatic
    fun convertIntToBigEndianByteArray(value: Int): ByteArray {
        val intByteArray = ByteArray(4)
        intByteArray[0] = (value ushr 24 and 0xff).toByte()
        intByteArray[1] = (value ushr 16 and 0xff).toByte()
        intByteArray[2] = (value ushr 8 and 0xff).toByte()
        intByteArray[3] = (value and 0xff).toByte()
        return intByteArray
    }

    /**
     * Convert byte array to integer.
     */
    @JvmStatic
    fun byteArrayToInt(bytes: ByteArray): Int {
        var count = 0
        for (i in bytes.size - 1 downTo 0) {
            val b = bytes[i].toInt() and 0xff
            count += b shl (8 * (bytes.size - i - 1))
        }
        return count
    }

    /**
     * Convert byte array to hex string.
     * [0x01, 0x02] => "01 02"
     */
    @JvmStatic
    fun ByteArray.toHexString(): String = joinToString(" ") { 
        (it.toInt() and 0xff).toString(16).padStart(2, '0').uppercase(Locale.getDefault()) 
    }

    /**
     * Convert hex string to byte array.
     * "0102" => [0x01, 0x02]
     */
    @JvmStatic
    fun String.hexStringToByteArray(): ByteArray {
        val cleanHex = this.replace(" ", "")
        return ByteArray(cleanHex.length / 2) { 
            cleanHex.substring(it * 2, it * 2 + 2).toInt(16).toByte() 
        }
    }

    /**
     * Extract tag from UUID.
     */
    @JvmStatic
    fun UUID.getTag(): String = toString().substring(4, 8)

    /**
     * Convert little-endian byte array to short.
     */
    @JvmStatic
    fun convertLittleEndianByteArrayToShort(byteArray: ByteArray): Short {
        return if (byteArray.size >= 2) {
            ((byteArray[1].toInt() and 0xff) shl 8 or (byteArray[0].toInt() and 0xff)).toShort()
        } else 0
    }

    /**
     * Convert big-endian byte array to short.
     */
    @JvmStatic
    fun convertBigEndianByteArrayToShort(byteArray: ByteArray): Short {
        return if (byteArray.size >= 2) {
            ((byteArray[0].toInt() and 0xff) shl 8 or (byteArray[1].toInt() and 0xff)).toShort()
        } else 0
    }

    /**
     * Convert little-endian byte array to int.
     */
    @JvmStatic
    fun convertLittleEndianByteArrayToInt(byteArray: ByteArray): Int {
        return when (byteArray.size) {
            1 -> byteArray[0].toInt() and 0xff
            2 -> (byteArray[1].toInt() and 0xff) shl 8 or (byteArray[0].toInt() and 0xff)
            3 -> (byteArray[2].toInt() and 0xff) shl 16 or 
                 (byteArray[1].toInt() and 0xff) shl 8 or 
                 (byteArray[0].toInt() and 0xff)
            4 -> (byteArray[3].toInt() and 0xff) shl 24 or
                 (byteArray[2].toInt() and 0xff) shl 16 or
                 (byteArray[1].toInt() and 0xff) shl 8 or
                 (byteArray[0].toInt() and 0xff)
            else -> 0
        }
    }

    /**
     * Convert big-endian byte array to int.
     */
    @JvmStatic
    fun convertBigEndianByteArrayToInt(byteArray: ByteArray): Int {
        return when (byteArray.size) {
            1 -> byteArray[0].toInt() and 0xff
            2 -> (byteArray[0].toInt() and 0xff) shl 8 or (byteArray[1].toInt() and 0xff)
            3 -> (byteArray[0].toInt() and 0xff) shl 16 or 
                 (byteArray[1].toInt() and 0xff) shl 8 or 
                 (byteArray[2].toInt() and 0xff)
            4 -> (byteArray[0].toInt() and 0xff) shl 24 or
                 (byteArray[1].toInt() and 0xff) shl 16 or
                 (byteArray[2].toInt() and 0xff) shl 8 or
                 (byteArray[3].toInt() and 0xff)
            else -> 0
        }
    }

    /**
     * Format float value with specified decimal places.
     */
    @JvmStatic
    fun formatFloatValue(value: Float, decimalPlaces: Int = 2): String {
        if (df == null) {
            df = DecimalFormat()
        }
        df?.maximumFractionDigits = decimalPlaces
        df?.minimumFractionDigits = decimalPlaces
        return df?.format(value) ?: value.toString()
    }

    /**
     * Convert thermal raw data to temperature matrix.
     */
    @JvmStatic
    fun convertRawDataToTemperatureMatrix(
        rawData: ByteArray, 
        width: Int, 
        height: Int, 
        isLittleEndian: Boolean = true
    ): Array<FloatArray> {
        val matrix = Array(height) { FloatArray(width) }
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = (y * width + x) * 2
                if (index + 1 < rawData.size) {
                    val tempValue = if (isLittleEndian) {
                        (rawData[index + 1].toInt() shl 8 and 0xff00) or (rawData[index].toInt() and 0xff)
                    } else {
                        (rawData[index].toInt() shl 8 and 0xff00) or (rawData[index + 1].toInt() and 0xff)
                    }
                    matrix[y][x] = tempValue / 64f - 273.15f // Convert to Celsius
                }
            }
        }
        
        return matrix
    }

    /**
     * Find min and max temperatures in matrix.
     */
    @JvmStatic
    fun findTemperatureExtremes(matrix: Array<FloatArray>): Pair<Float, Float> {
        var minTemp = Float.MAX_VALUE
        var maxTemp = Float.MIN_VALUE
        
        matrix.forEach { row ->
            row.forEach { temp ->
                if (temp < minTemp) minTemp = temp
                if (temp > maxTemp) maxTemp = temp
            }
        }
        
        return Pair(minTemp, maxTemp)
    }

    /**
     * Process raw thermal data into temperature values.
     * Converts raw byte array from thermal sensors into float array of temperatures.
     */
    @JvmStatic
    fun processThermalData(rawData: ByteArray): FloatArray {
        if (rawData.isEmpty()) return floatArrayOf()
        
        // Assume each temperature value is represented by 2 bytes (16-bit)
        val tempCount = rawData.size / 2
        val temperatures = FloatArray(tempCount)
        
        for (i in 0 until tempCount) {
            val byteIndex = i * 2
            if (byteIndex + 1 < rawData.size) {
                // Convert little-endian bytes to short, then to temperature
                val rawValue = ((rawData[byteIndex + 1].toInt() and 0xFF) shl 8) or 
                              (rawData[byteIndex].toInt() and 0xFF)
                
                // Convert raw value to temperature (this conversion depends on sensor specs)
                // For example: temp = (rawValue / 100.0) - 273.15 (for Kelvin to Celsius)
                temperatures[i] = rawValue / 100.0f - 273.15f
            }
        }
        
        return temperatures
    }

    /**
     * Process thermal matrix data with noise reduction.
     */
    @JvmStatic
    fun processThermalMatrix(rawMatrix: Array<ShortArray>): Array<FloatArray> {
        return rawMatrix.map { row ->
            row.map { rawTemp ->
                // Apply calibration and noise filtering
                val temp = rawTemp / 100.0f - 273.15f
                // Apply simple noise reduction (can be enhanced with more sophisticated filtering)
                if (temp < -40.0f || temp > 200.0f) {
                    // Filter out unrealistic temperature values
                    0.0f
                } else {
                    temp
                }
            }.toFloatArray()
        }.toTypedArray()
    }

    /**
     * Apply thermal calibration to raw sensor data.
     */
    @JvmStatic
    fun applyThermalCalibration(
        rawData: FloatArray, 
        offsetCorrection: Float = 0.0f,
        gainCorrection: Float = 1.0f
    ): FloatArray {
        return rawData.map { value ->
            (value + offsetCorrection) * gainCorrection
        }.toFloatArray()
    }
}