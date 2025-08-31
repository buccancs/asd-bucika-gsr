package com.guide.zm04c.matrix.utils

import com.topdon.lib.core.matrix.MatrixUtils
import java.util.*

/**
 * Backward compatibility wrapper for ByteUtils.
 * Delegates to enhanced MatrixUtils in libapp core.
 */
@Deprecated("Use MatrixUtils in libapp core instead", ReplaceWith("MatrixUtils"))
object ByteUtils {

    /**
     * byte[] => int
     */
    @JvmStatic
    fun byteToInt(bytes: ByteArray): Int {
        return MatrixUtils.byteArrayToInt(bytes)
    }

    /**
     * byte[] => string
     * [0x01, 0x02] => 01 02
     */
    @JvmStatic
    fun ByteArray.toHexString(): String = MatrixUtils.run { toHexString() }

    /**
     * string => byte[]
     * 0102 => [0x01, 0x02]
     */
    @JvmStatic
    @OptIn(ExperimentalUnsignedTypes::class)
    fun String.hexStringToByteArray(): ByteArray = MatrixUtils.run { hexStringToByteArray() }

    /**
     * UUID => ff01
     */
    @JvmStatic
    fun UUID.getTag(): String = MatrixUtils.run { getTag() }
}