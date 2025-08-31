package com.topdon.libcom.util

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.topdon.lib.core.utils.ColorUtils as CoreColorUtils
import com.topdon.lib.core.utils.EnhancedSizeUtils

/**
 * Backward compatibility wrapper for ColorUtils.
 * Delegates to enhanced ColorUtils in libapp core.
 * @author : litao
 * @date   : 2023/2/21 16:41
 */
@Deprecated("Use ColorUtils in libapp core instead")
object ColorUtils {

    fun setColorAlpha(@ColorInt color: Int, alpha: Float): Int {
        return CoreColorUtils.blendColors(color, color and 0x00ffffff, alpha)
    }

    fun toHexColorString(@ColorInt color: Int): String {
        return CoreColorUtils.toHexString(color, false)
    }

    fun dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
        return EnhancedSizeUtils.dpToPx(dp)
    }

    fun dpToPxF(@Dimension(unit = Dimension.DP) dp: Float): Float {
        return EnhancedSizeUtils.dpToPxF(dp)
    }

    fun formatVideoTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
            else -> String.format("%02d:%02d", minutes % 60, seconds % 60)
        }
    }
}