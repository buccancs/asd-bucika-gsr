package com.topdon.lib.core.bean

import android.graphics.drawable.Drawable

/**
 * Enhanced color selection bean with comprehensive color management
 * Consolidated from multiple lib* modules with extended capabilities
 */
data class ColorSelectBean(
    var colorValue: Int = 0,
    var colorName: String? = null,
    var isSelected: Boolean = false,
    var colorHex: String? = null,
    var colorResource: Int = 0,
    var image: Drawable? = null,
    var alpha: Float = 1.0f,
    var category: String? = null,
    var customData: Any? = null
) {
    
    /**
     * Get color as hex string
     */
    fun getColorHex(): String {
        return colorHex ?: String.format("#%08X", colorValue)
    }
    
    /**
     * Set color from hex string
     */
    fun setColorFromHex(hex: String) {
        colorHex = hex
        colorValue = try {
            android.graphics.Color.parseColor(hex)
        } catch (e: Exception) {
            colorValue
        }
    }
    
    /**
     * Get readable color name
     */
    fun getReadableColorName(): String {
        return colorName ?: when (colorValue) {
            0xFFFF0000.toInt() -> "Red"
            0xFF00FF00.toInt() -> "Green"
            0xFF0000FF.toInt() -> "Blue"
            0xFFFFFF00.toInt() -> "Yellow"
            0xFFFF00FF.toInt() -> "Magenta"
            0xFF00FFFF.toInt() -> "Cyan"
            0xFFFFFFFF.toInt() -> "White"
            0xFF000000.toInt() -> "Black"
            0xFFFFA500.toInt() -> "Orange"
            0xFF800080.toInt() -> "Purple"
            else -> getColorHex()
        }
    }
    
    /**
     * Check if color is dark (for UI contrast decisions)
     */
    fun isDarkColor(): Boolean {
        val red = (colorValue shr 16) and 0xFF
        val green = (colorValue shr 8) and 0xFF
        val blue = colorValue and 0xFF
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
        return luminance < 0.5
    }
    
    /**
     * Get contrast color (black or white) for text overlay
     */
    fun getContrastColor(): Int {
        return if (isDarkColor()) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
    }
    
    /**
     * Copy constructor
     */
    fun copy(): ColorSelectBean {
        return ColorSelectBean(
            colorValue = colorValue,
            colorName = colorName,
            isSelected = isSelected,
            colorHex = colorHex,
            colorResource = colorResource,
            image = image,
            alpha = alpha,
            category = category,
            customData = customData
        )
    }
    
    companion object {
        /**
         * Create from color value
         */
        fun fromColor(color: Int, name: String? = null): ColorSelectBean {
            return ColorSelectBean(
                colorValue = color,
                colorName = name,
                colorHex = String.format("#%08X", color)
            )
        }
        
        /**
         * Create from hex string
         */
        fun fromHex(hex: String, name: String? = null): ColorSelectBean {
            val color = try {
                android.graphics.Color.parseColor(hex)
            } catch (e: Exception) {
                0xFF000000.toInt()
            }
            
            return ColorSelectBean(
                colorValue = color,
                colorName = name,
                colorHex = hex
            )
        }
        
        /**
         * Create from resource
         */
        fun fromResource(context: android.content.Context, colorRes: Int, name: String? = null): ColorSelectBean {
            val color = androidx.core.content.ContextCompat.getColor(context, colorRes)
            return ColorSelectBean(
                colorValue = color,
                colorName = name,
                colorResource = colorRes,
                colorHex = String.format("#%08X", color)
            )
        }
        
        /**
         * Standard color palette
         */
        fun getStandardPalette(): List<ColorSelectBean> {
            return listOf(
                fromColor(0xFFFF0000.toInt(), "Red"),
                fromColor(0xFF00FF00.toInt(), "Green"),
                fromColor(0xFF0000FF.toInt(), "Blue"),
                fromColor(0xFFFFFF00.toInt(), "Yellow"),
                fromColor(0xFFFF00FF.toInt(), "Magenta"),
                fromColor(0xFF00FFFF.toInt(), "Cyan"),
                fromColor(0xFFFFFFFF.toInt(), "White"),
                fromColor(0xFF000000.toInt(), "Black"),
                fromColor(0xFFFFA500.toInt(), "Orange"),
                fromColor(0xFF800080.toInt(), "Purple"),
                fromColor(0xFF008000.toInt(), "Dark Green"),
                fromColor(0xFF800000.toInt(), "Maroon")
            )
        }
        
        /**
         * Thermal color palette for temperature visualization
         */
        fun getThermalPalette(): List<ColorSelectBean> {
            return listOf(
                fromColor(0xFF000080.toInt(), "Deep Blue"),
                fromColor(0xFF0000FF.toInt(), "Blue"),
                fromColor(0xFF0080FF.toInt(), "Light Blue"),
                fromColor(0xFF00FFFF.toInt(), "Cyan"),
                fromColor(0xFF00FF80.toInt(), "Green-Cyan"),
                fromColor(0xFF00FF00.toInt(), "Green"),
                fromColor(0xFF80FF00.toInt(), "Yellow-Green"),
                fromColor(0xFFFFFF00.toInt(), "Yellow"),
                fromColor(0xFFFF8000.toInt(), "Orange"),
                fromColor(0xFFFF0000.toInt(), "Red"),
                fromColor(0xFF800000.toInt(), "Dark Red"),
                fromColor(0xFFFFFFFF.toInt(), "White Hot")
            )
        }
    }
}

// Type alias for backward compatibility with existing DColorSelectBean
typealias DColorSelectBean = ColorSelectBean