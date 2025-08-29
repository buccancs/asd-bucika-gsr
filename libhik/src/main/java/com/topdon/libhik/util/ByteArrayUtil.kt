package com.topdon.libhik.util

import com.topdon.lib.core.thermal.ThermalUtils
import com.topdon.lib.core.thermal.toIntLE
import com.topdon.lib.core.thermal.toFloatLE
import com.topdon.lib.core.thermal.toStr
import com.topdon.lib.core.thermal.toHexString

/**
 * Backward compatibility wrapper for ByteArrayUtil functionality.
 * All methods delegate to the consolidated ThermalUtils implementation.
 * 
 * @deprecated Use com.topdon.lib.core.thermal.ThermalUtils directly for new code.
 */
internal object ByteArrayUtil {
    /**
     * 取指定数组的 `[index, index + 4)` 共 4 字节以小端方式转换成 Int.
     */
    internal fun ByteArray.toInt(index: Int): Int = this.toIntLE(index)

    /**
     * 取指定数组的 `[index, index + 4)` 共 4 字节以小端方式转换成 Float.
     */
    internal fun ByteArray.toFloat(index: Int): Float = this.toFloatLE(index)

    /**
     * 从指定数组的 `[startIndex, startIndex + size)` 解析 String
     */
    internal fun ByteArray.toStr(startIndex: Int, size: Int): String = this.toStr(startIndex, size)

    /**
     * 将指定数组的 `[startIndex, startIndex + size)` 以 16 进制的形式输出
     */
    internal fun ByteArray.buildPrintStr(startIndex: Int, size: Int): String = this.toHexString(startIndex, size)
}