package com.topdon.lib.core.thermal

import android.graphics.Point
import kotlin.math.*

/**
 * Consolidated thermal data processing utilities.
 * Combines functionality from libhik ByteArrayUtil, libir TempUtil, and related thermal processing.
 */
object ThermalUtils {
    
    // === Original libhik ByteArrayUtil functionality ===
    
    /**
     * Convert 4 bytes from byte array to Int using little-endian format.
     * @param array Source byte array
     * @param index Starting index
     * @return Converted Int value or 0 if index out of bounds
     */
    @JvmStatic
    fun bytesToIntLE(array: ByteArray, index: Int): Int = try {
        (array[index].toInt() and 0xff) or 
        (array[index + 1].toInt() and 0xff shl 8) or 
        (array[index + 2].toInt() and 0xff shl 16) or 
        (array[index + 3].toInt() and 0xff shl 24)
    } catch (_: IndexOutOfBoundsException) {
        0
    }

    /**
     * Convert 4 bytes from byte array to Float using little-endian format.
     * @param array Source byte array
     * @param index Starting index
     * @return Converted Float value or 0f if index out of bounds
     */
    @JvmStatic
    fun bytesToFloatLE(array: ByteArray, index: Int): Float = try {
        val intBits = (array[index].toInt() and 0xff) or 
                     (array[index + 1].toInt() and 0xff shl 8) or 
                     (array[index + 2].toInt() and 0xff shl 16) or 
                     (array[index + 3].toInt() and 0xff shl 24)
        java.lang.Float.intBitsToFloat(intBits)
    } catch (_: IndexOutOfBoundsException) {
        0f
    }

    /**
     * Extract null-terminated string from byte array.
     * @param array Source byte array
     * @param startIndex Starting index
     * @param maxSize Maximum size to read
     * @return Extracted string
     */
    @JvmStatic
    fun bytesToString(array: ByteArray, startIndex: Int, maxSize: Int): String = try {
        var validCount = 0
        for (i in startIndex until (startIndex + maxSize)) {
            if (array[i] == 0.toByte()) {
                break
            }
            validCount++
        }
        val stringBytes = ByteArray(validCount)
        System.arraycopy(array, startIndex, stringBytes, 0, validCount)
        String(stringBytes)
    } catch (_: IndexOutOfBoundsException) {
        ""
    }

    /**
     * Convert byte array range to hex string representation.
     * @param array Source byte array
     * @param startIndex Starting index
     * @param size Number of bytes to convert
     * @return Hex string representation
     */
    @JvmStatic
    fun bytesToHexString(array: ByteArray, startIndex: Int, size: Int): String = try {
        val stringBuilder = StringBuilder()
        for (i in startIndex until (startIndex + size)) {
            val hex = (array[i].toInt() and 0xff).toString(16)
            if (hex.length < 2) {
                stringBuilder.append("0")
            }
            stringBuilder.append(hex)
            if (i < startIndex + size - 1) {
                stringBuilder.append(" ")
            }
        }
        stringBuilder.toString().uppercase()
    } catch (_: IndexOutOfBoundsException) {
        "Array index out of bounds"
    }
    
    // === Enhanced thermal processing functionality ===
    
    /**
     * Convert short array to little-endian byte array.
     */
    @JvmStatic
    fun shortArrayToBytes(src: ShortArray): ByteArray {
        val dest = ByteArray(src.size * 2)
        for (i in src.indices) {
            val value = src[i].toInt()
            dest[i * 2] = (value and 0xFF).toByte()         // Low byte first (little-endian)
            dest[i * 2 + 1] = ((value shr 8) and 0xFF).toByte() // High byte second
        }
        return dest
    }
    
    /**
     * Convert little-endian byte array back to short array.
     */
    @JvmStatic
    fun bytesToShortArray(src: ByteArray): ShortArray {
        require(src.size % 2 == 0) { "Byte array length must be even for short conversion" }
        val dest = ShortArray(src.size / 2)
        for (i in dest.indices) {
            val lowByte = src[i * 2].toInt() and 0xFF
            val highByte = src[i * 2 + 1].toInt() and 0xFF
            dest[i] = (highByte shl 8 or lowByte).toShort()
        }
        return dest
    }

    // === New functionality from libir TempUtil consolidation ===
    
    /**
     * Extract temperature values along a line between two points.
     * Consolidated from libir TempUtil for thermal line analysis.
     */
    @JvmStatic
    fun getLineTemps(point1: Point, point2: Point, tempArray: ByteArray, width: Int): List<Float> {
        if (point1 == point2) {
            return emptyList() // No temperature data for identical points
        }

        val pointList = generateLinePoints(point1, point2)
        val tempList = mutableListOf<Float>()
        
        pointList.forEach { point ->
            val index = (point.y * width + point.x) * 2
            if (index + 1 < tempArray.size) {
                val tempInt = (tempArray[index + 1].toInt() shl 8 and 0xff00) or 
                             (tempArray[index].toInt() and 0xff)
                val tempValue = tempInt / 64f - 273.15f // Convert to Celsius
                tempList.add(tempValue)
            }
        }

        return tempList
    }
    
    /**
     * Generate all points along a line using Bresenham-like algorithm.
     */
    private fun generateLinePoints(point1: Point, point2: Point): List<Point> {
        val pointList = mutableListOf<Point>()
        
        when {
            point1.x == point2.x -> {
                // Vertical line
                val startY = minOf(point1.y, point2.y)
                val endY = maxOf(point1.y, point2.y)
                for (y in startY..endY) {
                    pointList.add(Point(point1.x, y))
                }
            }
            else -> {
                // Sloped line using slope-intercept form
                val k = (point1.y - point2.y).toFloat() / (point1.x - point2.x).toFloat()
                val b = point1.y - k * point1.x
                
                if (abs(k) <= 1) {
                    // More x-axis steps than y-axis
                    val startX = minOf(point1.x, point2.x)
                    val endX = maxOf(point1.x, point2.x)
                    for (x in startX..endX) {
                        pointList.add(Point(x, (k * x + b).toInt()))
                    }
                } else {
                    // More y-axis steps than x-axis
                    if (k >= 0) {
                        // Top-left to bottom-right
                        val startY = minOf(point1.y, point2.y)
                        val endY = maxOf(point1.y, point2.y)
                        for (y in startY..endY) {
                            pointList.add(Point(((y - b) / k).toInt(), y))
                        }
                    } else {
                        // Bottom-left to top-right
                        val startY = maxOf(point1.y, point2.y)
                        val endY = minOf(point1.y, point2.y)
                        for (y in startY downTo endY) {
                            pointList.add(Point(((y - b) / k).toInt(), y))
                        }
                    }
                }
            }
        }
        
        return pointList
    }
    
    /**
     * Convert raw temperature data to Celsius.
     */
    @JvmStatic
    fun rawToCelsius(rawTemp: Int): Float {
        return rawTemp / 64f - 273.15f
    }
    
    /**
     * Convert raw temperature data to Fahrenheit.
     */
    @JvmStatic
    fun rawToFahrenheit(rawTemp: Int): Float {
        val celsius = rawToCelsius(rawTemp)
        return celsius * 9f / 5f + 32f
    }
    
    /**
     * Extract temperature from byte array at specific coordinates.
     */
    @JvmStatic
    fun getTempAtPoint(tempArray: ByteArray, x: Int, y: Int, width: Int): Float? {
        val index = (y * width + x) * 2
        return if (index + 1 < tempArray.size) {
            val tempInt = (tempArray[index + 1].toInt() shl 8 and 0xff00) or 
                         (tempArray[index].toInt() and 0xff)
            rawToCelsius(tempInt)
        } else {
            null
        }
    }
    
    /**
     * Find min and max temperatures in a region.
     */
    @JvmStatic
    fun getMinMaxTemp(tempArray: ByteArray, width: Int, height: Int): Pair<Float, Float> {
        var minTemp = Float.MAX_VALUE
        var maxTemp = Float.MIN_VALUE
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                getTempAtPoint(tempArray, x, y, width)?.let { temp ->
                    minTemp = minOf(minTemp, temp)
                    maxTemp = maxOf(maxTemp, temp)
                }
            }
        }
        
        return Pair(minTemp, maxTemp)
    }
}

// Extension functions for Kotlin convenience (maintaining libhik API style)
/**
 * Convert 4 bytes to Int using little-endian format.
 */
fun ByteArray.toIntLE(index: Int): Int = ThermalUtils.bytesToIntLE(this, index)

/**
 * Convert 4 bytes to Float using little-endian format.
 */
fun ByteArray.toFloatLE(index: Int): Float = ThermalUtils.bytesToFloatLE(this, index)

/**
 * Extract null-terminated string from byte array.
 */
fun ByteArray.toStr(startIndex: Int, size: Int): String = ThermalUtils.bytesToString(this, startIndex, size)

/**
 * Convert byte array range to hex string representation.
 */
fun ByteArray.toHexString(startIndex: Int, size: Int): String = ThermalUtils.bytesToHexString(this, startIndex, size)