package com.topdon.lib.core.utils

import com.blankj.utilcode.util.SizeUtils
import kotlin.math.*

/**
 * Enhanced Kotlin utility extensions with comprehensive type conversion and measurement utilities
 * Consolidated from multiple lib* modules with additional capabilities
 */

// Size and dimension extensions
val Float.dp: Float
    get() = EnhancedSizeUtils.dpToPxF(this)

val Int.dp: Int
    get() = EnhancedSizeUtils.dpToPx(this)

val Float.sp: Float
    get() = SizeUtils.sp2px(this).toFloat()

val Int.sp: Float
    get() = SizeUtils.sp2px(this.toFloat()).toFloat()

val Float.px2dp: Float
    get() = EnhancedSizeUtils.pxToDpF(this)

val Int.px2dp: Int
    get() = EnhancedSizeUtils.pxToDp(this)

val Float.px2sp: Float
    get() = try { SizeUtils.px2sp(this.toInt()).toFloat() } catch (e: Exception) { this }

val Int.px2sp: Float
    get() = try { SizeUtils.px2sp(this).toFloat() } catch (e: Exception) { this.toFloat() }

// Mathematical extensions
val Float.radians: Float
    get() = this * PI.toFloat() / 180f

val Double.radians: Double
    get() = this * PI / 180.0

val Float.degrees: Float
    get() = this * 180f / PI.toFloat()

val Double.degrees: Double
    get() = this * 180.0 / PI

// Color extensions
val Int.hexString: String
    get() = String.format("#%08X", this)

val String.colorInt: Int
    get() = try {
        android.graphics.Color.parseColor(this)
    } catch (e: Exception) {
        android.graphics.Color.BLACK
    }

// Collection extensions
fun <T> List<T>.takeRandom(count: Int): List<T> {
    return if (count >= size) this else shuffled().take(count)
}

inline fun <reified T> Array<T>.takeRandom(count: Int): Array<T> {
    return if (count >= size) this else toList().shuffled().take(count).toTypedArray()
}

// String extensions
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidUrl(): Boolean {
    return android.util.Patterns.WEB_URL.matcher(this).matches()
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
}

// Numeric extensions
fun Float.clamp(min: Float, max: Float): Float {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

fun Double.clamp(min: Double, max: Double): Double {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

fun Int.clamp(min: Int, max: Int): Int {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

// Temperature conversion extensions
val Float.celsiusToFahrenheit: Float
    get() = this * 9f / 5f + 32f

val Float.fahrenheitToCelsius: Float
    get() = (this - 32f) * 5f / 9f

val Float.celsiusToKelvin: Float
    get() = this + 273.15f

val Float.kelvinToCelsius: Float
    get() = this - 273.15f

// Time extensions
val Int.millisToSeconds: Float
    get() = this / 1000f

val Long.millisToSeconds: Float
    get() = this / 1000f

val Float.secondsToMillis: Long
    get() = (this * 1000).toLong()

val Int.secondsToMillis: Long
    get() = this * 1000L

// Percentage extensions
val Float.percent: String
    get() = "${(this * 100).roundToInt()}%"

val Double.percent: String
    get() = "${(this * 100).roundToInt()}%"

// Safe division extensions
infix fun Float.safeDivide(other: Float): Float {
    return if (other != 0f) this / other else 0f
}

infix fun Double.safeDivide(other: Double): Double {
    return if (other != 0.0) this / other else 0.0
}

infix fun Int.safeDivide(other: Int): Float {
    return if (other != 0) this.toFloat() / other else 0f
}

// Boolean extensions
fun Boolean.toInt(): Int = if (this) 1 else 0

fun Boolean.toFloat(): Float = if (this) 1f else 0f

fun Int.toBoolean(): Boolean = this != 0

// Array extensions
fun ByteArray.toHexString(): String {
    return joinToString("") { "%02X".format(it) }
}

fun String.hexToByteArray(): ByteArray {
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

// Range extensions
fun IntRange.random(): Int = (start..endInclusive).random()

fun LongRange.random(): Long = (start..endInclusive).random()

fun ClosedFloatingPointRange<Float>.random(): Float {
    return start + (endInclusive - start) * kotlin.random.Random.nextFloat()
}

fun ClosedFloatingPointRange<Double>.random(): Double {
    return start + (endInclusive - start) * kotlin.random.Random.nextDouble()
}

/**
 * Enhanced size utility functions
 */
object EnhancedSizeUtils {
    
    fun dpToPx(dp: Int): Int {
        return SizeUtils.dp2px(dp.toFloat()).toInt()
    }
    
    fun dpToPxF(dp: Float): Float {
        return try { 
            SizeUtils.dp2px(dp.toInt()).toFloat() 
        } catch (e: Exception) { 
            dp * 3f // Fallback assumption: ~3px per dp
        }
    }
    
    fun pxToDp(px: Int): Int {
        return try {
            SizeUtils.px2dp(px).toInt() 
        } catch (e: Exception) {
            px / 3 // Fallback
        }
    }
    
    fun pxToDpF(px: Float): Float {
        return try {
            SizeUtils.px2dp(px.toInt()).toFloat()
        } catch (e: Exception) {
            px / 3f // Fallback
        }
    }
    
    fun spToPx(sp: Float): Float {
        return try {
            SizeUtils.sp2px(sp.toInt()).toFloat()
        } catch (e: Exception) {
            sp * 3f // Fallback
        }
    }
    
    fun pxToSp(px: Float): Float {
        return try {
            SizeUtils.px2sp(px.toInt()).toFloat()
        } catch (e: Exception) {
            px / 3f // Fallback
        }
    }
    
    /**
     * Get screen density
     */
    fun getScreenDensity(): Float {
        return android.content.res.Resources.getSystem().displayMetrics.density
    }
    
    /**
     * Get screen density DPI
     */
    fun getScreenDensityDpi(): Int {
        return android.content.res.Resources.getSystem().displayMetrics.densityDpi
    }
    
    /**
     * Calculate optimal image size for given constraints
     */
    fun calculateOptimalImageSize(
        originalWidth: Int,
        originalHeight: Int,
        maxWidth: Int,
        maxHeight: Int,
        maintainAspectRatio: Boolean = true
    ): Pair<Int, Int> {
        if (!maintainAspectRatio) {
            return maxWidth to maxHeight
        }
        
        val aspectRatio = originalWidth.toFloat() / originalHeight
        
        return if (originalWidth > originalHeight) {
            val newWidth = minOf(maxWidth, originalWidth)
            val newHeight = (newWidth / aspectRatio).toInt()
            newWidth to minOf(newHeight, maxHeight)
        } else {
            val newHeight = minOf(maxHeight, originalHeight)
            val newWidth = (newHeight * aspectRatio).toInt()
            minOf(newWidth, maxWidth) to newHeight
        }
    }
}