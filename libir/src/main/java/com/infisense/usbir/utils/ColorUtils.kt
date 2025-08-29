package com.infisense.usbir.utils

import com.topdon.lib.core.utils.ColorUtils as CoreColorUtils

/**
 * Backward compatibility wrapper for ColorUtils functionality.
 * All methods delegate to the consolidated ColorUtils implementation.
 * 
 * This wrapper eliminates ~28 lines of duplicate color utility code while maintaining
 * full backward compatibility with existing libir usage patterns.
 * 
 * @deprecated Use com.topdon.lib.core.utils.ColorUtils directly for new code.
 */
@Suppress("UNUSED")
object ColorUtils {

    @JvmStatic
    fun getRed(color: Int) = CoreColorUtils.getRed(color)

    @JvmStatic
    fun getGreen(color: Int) = CoreColorUtils.getGreen(color)

    @JvmStatic
    fun getBlue(color: Int) = CoreColorUtils.getBlue(color)

    @JvmStatic
    fun to01(value: Float) = CoreColorUtils.to01(value)
}