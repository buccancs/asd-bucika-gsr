package com.infisense.usbir.utils

import android.graphics.Point
import com.topdon.lib.core.thermal.ThermalUtils

/**
 * Backward compatibility wrapper for TempUtil functionality.
 * All methods delegate to the consolidated ThermalUtils implementation.
 * 
 * This wrapper eliminates ~55 lines of duplicate temperature processing code while maintaining
 * full backward compatibility with existing libir usage patterns.
 * 
 * @deprecated Use com.topdon.lib.core.thermal.ThermalUtils directly for new code.
 */
object TempUtil {
    
    @JvmStatic
    fun getLineTemps(point1: Point, point2: Point, tempArray: ByteArray, width: Int): List<Float> {
        return ThermalUtils.getLineTemps(point1, point2, tempArray, width)
    }
}