package com.infisense.usbir.tools

import android.graphics.Bitmap
import android.util.Log
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
                        //灰度
                        grey = (r * 0.3f).toInt() + (g * 0.59f).toInt() + (b * 0.11f).toInt()
                        imageBytes[i * 4] = grey.toByte()
                        imageBytes[i * 4 + 1] = grey.toByte()
                        imageBytes[i * 4 + 2] = grey.toByte()
                    }
                }
            } else {
                var data: ByteArray
                val len = imageBytes.size / 4
                val maxA = ((maxColor shr 24) and 0xff).toByte()
                val maxR = ((maxColor shr 16) and 0xff).toByte()
                val maxG = ((maxColor shr 8) and 0xff).toByte()
                val maxB = ((maxColor shr 0) and 0xff).toByte()
                val minA = ((minColor shr 24) and 0xff).toByte()
                val minR = ((minColor shr 16) and 0xff).toByte()
                val minG = ((minColor shr 8) and 0xff).toByte()
                val minB = ((minColor shr 0) and 0xff).toByte()
                var value: Float
                for (i in 0 until len) {
                    data = tempBytes.copyOfRange(i * 2, i * 2 + 2)
                    value = readTempValue(data)
                    if (value > max) {
                        //max color
                        imageBytes[i * 4] = maxR //r
                        imageBytes[i * 4 + 1] = maxG //g
                        imageBytes[i * 4 + 2] = maxB //b
                        imageBytes[i * 4 + 3] = maxA //a
                    }
                    if (value < min) {
                        //min color
                        imageBytes[i * 4] = minR
                        imageBytes[i * 4 + 1] = minG
                        imageBytes[i * 4 + 2] = minB
                        imageBytes[i * 4 + 3] = minA
                    }
                }
            }
        } catch (e: Exception) {
        }
    }
}