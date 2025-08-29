package com.topdon.lib.core.utils

import android.graphics.*
import android.util.Log
import androidx.annotation.ColorInt
import com.topdon.lib.core.matrix.MatrixUtils

/**
 * Enhanced bitmap and image processing utilities consolidating libir BitmapTools and ImageTools.
 * Provides comprehensive image manipulation, thermal overlay, and color processing capabilities.
 */
object EnhancedBitmapUtils {

    private const val TAG = "EnhancedBitmapUtils"

    /**
     * Replace bitmap colors based on temperature data and thresholds.
     * Consolidated from libir BitmapTools.
     */
    @JvmStatic
    fun replaceBitmapColorWithThermal(
        imageBytes: ByteArray,
        tempBytes: ByteArray,
        maxTemp: Float = 40f,
        minTemp: Float = 20f,
        @ColorInt maxColor: Int = Color.RED,
        @ColorInt minColor: Int = Color.BLUE
    ) {
        if (maxTemp < minTemp) return

        try {
            val len = imageBytes.size / 4
            
            for (i in 0 until len) {
                val tempData = tempBytes.copyOfRange(i * 2, i * 2 + 2)
                val temperature = readTemperatureValue(tempData)
                
                if (temperature > maxTemp || temperature < minTemp) {
                    // Apply color based on temperature threshold
                    val colorToApply = if (temperature > maxTemp) maxColor else minColor
                    
                    val r = (colorToApply shr 16) and 0xff
                    val g = (colorToApply shr 8) and 0xff
                    val b = colorToApply and 0xff
                    
                    imageBytes[i * 4] = r.toByte()
                    imageBytes[i * 4 + 1] = g.toByte()
                    imageBytes[i * 4 + 2] = b.toByte()
                    // Alpha channel remains unchanged
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error replacing bitmap colors", e)
        }
    }

    /**
     * Create thermal overlay bitmap from temperature data.
     */
    @JvmStatic
    fun createThermalOverlay(
        width: Int,
        height: Int,
        temperatureData: ByteArray,
        colorMap: IntArray = getDefaultThermalColorMap()
    ): Bitmap? {
        return try {
            val pixels = IntArray(width * height)
            
            for (i in 0 until width * height) {
                if (i * 2 + 1 < temperatureData.size) {
                    val tempValue = readTemperatureValue(
                        temperatureData.copyOfRange(i * 2, i * 2 + 2)
                    )
                    pixels[i] = mapTemperatureToColor(tempValue, colorMap)
                }
            }
            
            Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating thermal overlay", e)
            null
        }
    }

    /**
     * Apply thermal color mapping to grayscale bitmap.
     */
    @JvmStatic
    fun applyThermalColorMap(
        grayscaleBitmap: Bitmap,
        colorMap: IntArray = getDefaultThermalColorMap()
    ): Bitmap? {
        return try {
            val width = grayscaleBitmap.width
            val height = grayscaleBitmap.height
            val pixels = IntArray(width * height)
            grayscaleBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            
            for (i in pixels.indices) {
                val grayValue = pixels[i] and 0xff
                pixels[i] = colorMap[minOf(grayValue, colorMap.size - 1)]
            }
            
            Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            Log.e(TAG, "Error applying thermal color map", e)
            null
        }
    }

    /**
     * Blend two bitmaps with specified alpha.
     */
    @JvmStatic
    fun blendBitmaps(
        baseBitmap: Bitmap,
        overlayBitmap: Bitmap,
        alpha: Float = 0.5f
    ): Bitmap? {
        return try {
            val result = baseBitmap.copy(baseBitmap.config, true)
            val canvas = Canvas(result)
            val paint = Paint().apply {
                this.alpha = (alpha * 255).toInt()
            }
            canvas.drawBitmap(overlayBitmap, 0f, 0f, paint)
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error blending bitmaps", e)
            null
        }
    }

    /**
     * Convert bitmap to grayscale.
     */
    @JvmStatic
    fun convertToGrayscale(bitmap: Bitmap): Bitmap? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            
            for (i in pixels.indices) {
                val pixel = pixels[i]
                val r = (pixel shr 16) and 0xff
                val g = (pixel shr 8) and 0xff
                val b = pixel and 0xff
                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                pixels[i] = Color.argb(255, gray, gray, gray)
            }
            
            Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            Log.e(TAG, "Error converting to grayscale", e)
            null
        }
    }

    /**
     * Scale bitmap maintaining aspect ratio.
     */
    @JvmStatic
    fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            
            val scaleX = maxWidth.toFloat() / width
            val scaleY = maxHeight.toFloat() / height
            val scale = minOf(scaleX, scaleY)
            
            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()
            
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } catch (e: Exception) {
            Log.e(TAG, "Error scaling bitmap", e)
            null
        }
    }

    /**
     * Extract region from bitmap.
     */
    @JvmStatic
    fun extractRegion(bitmap: Bitmap, rect: Rect): Bitmap? {
        return try {
            Bitmap.createBitmap(
                bitmap, 
                rect.left, 
                rect.top, 
                rect.width(), 
                rect.height()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting region", e)
            null
        }
    }

    private fun readTemperatureValue(bytes: ByteArray): Float {
        val tempInt = MatrixUtils.convertLittleEndianByteArrayToInt(bytes) / 4
        return (tempInt.toDouble() / 16.0 - 273.15).toFloat()
    }

    private fun mapTemperatureToColor(temperature: Float, colorMap: IntArray): Int {
        // Map temperature to color index (simplified mapping)
        val normalizedTemp = ((temperature + 20) / 60).coerceIn(0f, 1f)
        val colorIndex = (normalizedTemp * (colorMap.size - 1)).toInt()
        return colorMap[colorIndex.coerceIn(0, colorMap.size - 1)]
    }

    private fun getDefaultThermalColorMap(): IntArray {
        // Classic thermal color map: black -> red -> yellow -> white
        return intArrayOf(
            Color.BLACK,
            Color.parseColor("#330000"), Color.parseColor("#660000"), Color.parseColor("#990000"),
            Color.parseColor("#CC0000"), Color.RED,
            Color.parseColor("#FF3300"), Color.parseColor("#FF6600"), Color.parseColor("#FF9900"),
            Color.parseColor("#FFCC00"), Color.YELLOW,
            Color.parseColor("#FFFF33"), Color.parseColor("#FFFF66"), Color.parseColor("#FFFF99"),
            Color.parseColor("#FFFFCC"), Color.WHITE
        )
    }
}