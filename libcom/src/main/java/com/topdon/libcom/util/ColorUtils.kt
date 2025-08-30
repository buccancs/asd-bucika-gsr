package com.topdon.libcom.util

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.topdon.lib.core.utils.ColorUtils as CoreColorUtils
import com.topdon.lib.core.utils.EnhancedKotlinExtensions

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
        return EnhancedKotlinExtensions.dpToPx(dp)
    }

    fun dpToPxF(@Dimension(unit = Dimension.DP) dp: Float): Float {
        return EnhancedKotlinExtensions.dpToPxF(dp)
    }

    fun formatVideoTime(milliseconds: Long): String {
        return EnhancedKotlinExtensions.formatVideoTime(milliseconds)
    }
}