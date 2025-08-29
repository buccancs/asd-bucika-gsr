package com.topdon.lib.core.thermal

/**
 * Consolidated thermal data processing utilities.
 * Combines functionality from libhik ByteArrayUtil and similar utilities.
 */
object ThermalUtils {
    
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