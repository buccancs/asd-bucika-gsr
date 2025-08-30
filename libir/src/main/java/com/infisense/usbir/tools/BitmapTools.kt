package com.infisense.usbir.tools

import android.graphics.Bitmap
import androidx.annotation.ColorInt
import com.topdon.lib.core.utils.EnhancedBitmapUtils

/**
 * Backward compatibility wrapper for BitmapTools.
 * Delegates to enhanced EnhancedBitmapUtils in libapp core.
 * @author: CaiSongL
 * @date: 2023/4/13 9:33
 */
@Deprecated("Use EnhancedBitmapUtils in libapp core instead", ReplaceWith("EnhancedBitmapUtils"))
object BitmapTools {

    @JvmStatic
    fun replaceBitmapColor(
        imageBytes: ByteArray,
        tempBytes: ByteArray,
        max: Float = 40f,
        min: Float = 20f,
        @ColorInt maxColor: Int,
        @ColorInt minColor: Int
    ) {
        EnhancedBitmapUtils.replaceBitmapColorWithThermal(
            imageBytes, tempBytes, max, min, maxColor, minColor
        )
    }

    @JvmStatic
    fun createThermalOverlay(width: Int, height: Int, temperatureData: ByteArray): Bitmap? {
        return EnhancedBitmapUtils.createThermalOverlay(width, height, temperatureData)
    }

    @JvmStatic
    fun applyThermalColorMap(grayscaleBitmap: Bitmap): Bitmap? {
        return EnhancedBitmapUtils.applyThermalColorMap(grayscaleBitmap)
    }

    @JvmStatic
    fun blendBitmaps(baseBitmap: Bitmap, overlayBitmap: Bitmap, alpha: Float = 0.5f): Bitmap? {
        return EnhancedBitmapUtils.blendBitmaps(baseBitmap, overlayBitmap, alpha)
    }
}