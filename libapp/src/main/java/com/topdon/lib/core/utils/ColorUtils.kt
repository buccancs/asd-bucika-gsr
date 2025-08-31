package com.topdon.lib.core.utils

import android.graphics.Color
import java.util.*

/**
 * Consolidated color utility functions.
 * Combines functionality from libir ColorUtils and other color processing utilities.
 */
object ColorUtils {
    
    // === Basic color component extraction ===
    
    @JvmStatic
    fun getRed(color: Int): Int = color shr 16 and 0xFF
    
    @JvmStatic
    fun getGreen(color: Int): Int = color shr 8 and 0xFF
    
    @JvmStatic
    fun getBlue(color: Int): Int = color and 0xFF
    
    @JvmStatic
    fun getAlpha(color: Int): Int = color ushr 24 and 0xFF
    
    // === Color manipulation ===
    
    /**
     * Blend two colors with specified alpha ratio.
     */
    @JvmStatic
    fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val clampedRatio = ratio.coerceIn(0f, 1f)
        val inverseRatio = 1f - clampedRatio
        
        val r = (Color.red(color1) * inverseRatio + Color.red(color2) * clampedRatio).toInt()
        val g = (Color.green(color1) * inverseRatio + Color.green(color2) * clampedRatio).toInt()
        val b = (Color.blue(color1) * inverseRatio + Color.blue(color2) * clampedRatio).toInt()
        val a = (Color.alpha(color1) * inverseRatio + Color.alpha(color2) * clampedRatio).toInt()
        
        return Color.argb(a, r, g, b)
    }
    
    /**
     * Adjust color brightness.
     */
    @JvmStatic
    fun adjustBrightness(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= factor.coerceIn(0f, 2f) // Brightness
        return Color.HSVToColor(Color.alpha(color), hsv)
    }
    
    /**
     * Adjust color saturation.
     */
    @JvmStatic
    fun adjustSaturation(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] *= factor.coerceIn(0f, 2f) // Saturation
        return Color.HSVToColor(Color.alpha(color), hsv)
    }
    
    // === Color format conversions ===
    
    /**
     * Convert color to hex string representation.
     */
    @JvmStatic
    fun toHexString(color: Int, includeAlpha: Boolean = false): String {
        return if (includeAlpha) {
            String.format(Locale.ENGLISH, "#%08X", color)
        } else {
            String.format(Locale.ENGLISH, "#%06X", color and 0xFFFFFF)
        }
    }
    
    /**
     * Parse hex string to color.
     */
    @JvmStatic
    fun parseColor(hexColor: String): Int? {
        return try {
            Color.parseColor(hexColor)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    // === Thermal/temperature color mapping ===
    
    /**
     * Map temperature value to color using a thermal palette.
     */
    @JvmStatic
    fun temperatureToColor(temp: Float, minTemp: Float, maxTemp: Float): Int {
        val normalized = ((temp - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f)
        
        return when {
            normalized <= 0.25f -> {
                // Blue to cyan
                val ratio = normalized * 4
                blendColors(Color.BLUE, Color.CYAN, ratio)
            }
            normalized <= 0.5f -> {
                // Cyan to green
                val ratio = (normalized - 0.25f) * 4
                blendColors(Color.CYAN, Color.GREEN, ratio)
            }
            normalized <= 0.75f -> {
                // Green to yellow
                val ratio = (normalized - 0.5f) * 4
                blendColors(Color.GREEN, Color.YELLOW, ratio)
            }
            else -> {
                // Yellow to red
                val ratio = (normalized - 0.75f) * 4
                blendColors(Color.YELLOW, Color.RED, ratio)
            }
        }
    }
    
    // === Number formatting (from original libir ColorUtils) ===
    
    /**
     * Format float value to single decimal place.
     */
    @JvmStatic
    fun to01(value: Float): String {
        return String.format(Locale.ENGLISH, "%.1f", value)
    }
    
    /**
     * Format float value to specified decimal places.
     */
    @JvmStatic
    fun toDecimalPlaces(value: Float, decimalPlaces: Int): String {
        return String.format(Locale.ENGLISH, "%.${decimalPlaces}f", value)
    }
    
    // === Color distance and similarity ===
    
    /**
     * Calculate Euclidean distance between two colors in RGB space.
     */
    @JvmStatic
    fun colorDistance(color1: Int, color2: Int): Float {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        
        val dr = r1 - r2
        val dg = g1 - g2
        val db = b1 - b2
        
        return kotlin.math.sqrt((dr * dr + dg * dg + db * db).toFloat())
    }
    
    /**
     * Check if two colors are similar within tolerance.
     */
    @JvmStatic
    fun areColorsSimilar(color1: Int, color2: Int, tolerance: Float = 50f): Boolean {
        return colorDistance(color1, color2) <= tolerance
    }
}