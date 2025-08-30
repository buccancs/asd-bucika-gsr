/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.topdon.lib.core.utils

/**
 * Shared HexDump utility class consolidating duplicate implementations
 * from multiple modules. Contains all functionality from both libir and libmatrix versions.
 */
object HexDump {
    
    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_LOWER_CASE_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun dumpHexString(array: ByteArray?): String {
        if (array == null) return "(null)"
        return dumpHexString(array, 0, array.size)
    }

    fun dumpHexString(array: ByteArray?, offset: Int, length: Int): String {
        if (array == null) return "(null)"
        val result = StringBuilder()

        val line = ByteArray(16)
        var lineIndex = 0

        result.append("\n0x")
        result.append(toHexString(offset))

        for (i in offset until offset + length) {
            if (lineIndex == 16) {
                result.append(" ")

                for (j in 0 until 16) {
                    if (line[j] > ' '.code.toByte() && line[j] < '~'.code.toByte()) {
                        result.append(String(line, j, 1))
                    } else {
                        result.append(".")
                    }
                }

                result.append("\n0x")
                result.append(toHexString(i))
                lineIndex = 0
            }

            val b = array[i]
            result.append(" ")
            result.append(HEX_DIGITS[(b.toInt() ushr 4) and 0x0F])
            result.append(HEX_DIGITS[b.toInt() and 0x0F])

            line[lineIndex++] = b
        }

        if (lineIndex != 0) {
            var count = (16 - lineIndex) * 3
            count++
            for (i in 0 until count) {
                result.append(" ")
            }

            for (i in 0 until lineIndex) {
                if (line[i] > ' '.code.toByte() && line[i] < '~'.code.toByte()) {
                    result.append(String(line, i, 1))
                } else {
                    result.append(".")
                }
            }
        }

        return result.toString()
    }

    fun toHexString(b: Byte): String {
        return toHexString(toByteArray(b))
    }

    fun toHexString(array: ByteArray): String {
        return toHexString(array, 0, array.size)
    }

    fun toHexString(array: ByteArray, offset: Int, length: Int): String {
        val buf = CharArray(length * 2)

        var bufIndex = 0
        for (i in offset until offset + length) {
            val b = array[i]
            buf[bufIndex++] = HEX_DIGITS[(b.toInt() ushr 4) and 0x0F]
            buf[bufIndex++] = HEX_DIGITS[b.toInt() and 0x0F]
        }

        return String(buf)
    }

    fun toHexString(i: Int): String {
        return toHexString(toByteArray(i))
    }

    fun toByteArray(b: Byte): ByteArray {
        return byteArrayOf(b)
    }

    fun toByteArray(i: Int): ByteArray {
        return byteArrayOf(
            (i shr 24 and 0xFF).toByte(),
            (i shr 16 and 0xFF).toByte(),
            (i shr 8 and 0xFF).toByte(),
            (i and 0xFF).toByte()
        )
    }

    private fun toByte(c: Char): Int {
        return when {
            c in '0'..'9' -> c - '0'
            c in 'A'..'F' -> c - 'A' + 10
            c in 'a'..'f' -> c - 'a' + 10
            else -> throw RuntimeException("Invalid hex char '$c'")
        }
    }

    fun hexStringToByteArray(hexString: String): ByteArray {
        val length = hexString.length
        val buffer = ByteArray(length / 2)

        for (i in 0 until length step 2) {
            buffer[i / 2] = ((toByte(hexString[i]) shl 4) or toByte(hexString[i + 1])).toByte()
        }

        return buffer
    }

    fun toHexString(value: Long): String {
        return value.toString(16)
    }

    fun toHexString(f: Float): String {
        return f.toHexString()
    }

    fun toHexString(d: Double): String {
        return d.toHexString()
    }

    /**
     * Convert byte array to int value (little-endian: low byte first)
     */
    fun bytesToInt(src: ByteArray, offset: Int): Int {
        return ((src[offset].toInt() and 0xFF) or
                ((src[offset + 1].toInt() and 0xFF) shl 8) or
                ((src[offset + 2].toInt() and 0xFF) shl 16) or
                ((src[offset + 3].toInt() and 0xFF) shl 24))
    }

    /**
     * Convert int value to byte array (little-endian: low byte first)
     * Compatible with bytesToInt()
     *
     * @param value int value to convert
     * @return byte array
     */
    fun intToBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            (value shr 8 and 0xFF).toByte(),
            (value shr 16 and 0xFF).toByte(),
            (value shr 24 and 0xFF).toByte()
        )
    }

    /**
     * Convert int value to byte array (big-endian: high byte first)
     * Compatible with bytesToInt2()
     */
    fun intToBytes2(value: Int): ByteArray {
        return byteArrayOf(
            (value shr 24 and 0xFF).toByte(),
            (value shr 16 and 0xFF).toByte(),
            (value shr 8 and 0xFF).toByte(),
            (value and 0xFF).toByte()
        )
    }

    /**
     * Convert float to byte array (little-endian)
     */
    fun float2byte(num: Float, numbyte: ByteArray) {
        val fbit = num.toRawBits()

        for (i in 0 until 4) {
            numbyte[i] = (fbit shr (i * 8)).toByte() // little-endian
        }
    }
}