package com.topdon.lib.core.tools

import com.topdon.lib.core.common.SharedManager
import java.math.BigDecimal
import java.util.*

object UnitTools {

    /**
     * temperature
     *
     * temperature
     */
    @JvmStatic
    fun showC(float: Float): String {
        val str = if (SharedManager.getTemperature() == 1) {
            // temperature
            "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
        }
        return str
    }
    /**
     * temperature
     *
     * temperature
     */
    @JvmStatic
    fun showC(float: Float,isC: Boolean): String {
        val str = if (isC) {
            // temperature
            "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
        }
        return str
    }


    /**
     * temperature
     */
    @JvmStatic
    fun showIntervalC(min: Int, max: Int): String {
        val str = if (SharedManager.getTemperature() == 1) {
            // temperature
            "${min}~${max}°C"
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            val maxT: Int = (max * 1.8000 + 32.00).toInt()
            val minT: Int = (min * 1.8000 + 32.00).toInt()
            "${minT}~${maxT}°F"
        }
        return str
    }

    /**
     * temperature
     */
    @JvmStatic
    fun showConfigC(min: Int, max: Int): String {
        val str = if (SharedManager.getTemperature() == 1) {
            // temperature
            "(${min}~${max}°C)"
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            val maxT: Int = (max * 1.8000 + 32.00).toInt()
            val minT: Int = (min * 1.8000 + 32.00).toInt()
            "(${minT}~${maxT}°F)"
        }
        return str
    }

    /**
     * temperature
     *
     * temperature
     */
    @JvmStatic
    fun showUnit(): String {
        val str = if (SharedManager.getTemperature() == 1) {
            // temperature
            "°C"
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            "°F"
        }
        return str
    }

    /**
     * temperature
     *
     * temperature
     */
    @JvmStatic
    fun showUnitValue(value: Float): Float {
        val str = if (SharedManager.getTemperature() == 1) {
            // temperature
            value
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            toF(value)
        }
        return str.toFloat()
    }
    /**
     * temperature
     *
     * temperature
     */
    @JvmStatic
    fun showUnitValue(value: Float,showC: Boolean): Float {
        if (value == Float.MAX_VALUE || value == Float.MIN_VALUE){
            return value
        }
        val str = if (showC) {
            // temperature
            value
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            toF(value)
        }
        return str.toFloat()
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
     * temperature
     */
    @JvmStatic
    fun showToCValue(value: Float,isShowC: Boolean): Float {
        val str = if (isShowC) {
            // temperature
            value
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            toC(value)
        }
        return str.toFloat()
    }
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
     * temperature
     */
    @JvmStatic
    fun showToCValue(value: Float): Float {
        val str = if (SharedManager.getTemperature() == 1) {
            // temperature
            value
        } else {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            toC(value)
        }
        return str.toFloat()
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    fun toF(value: Float): Float {
        return value * 1.8000f + 32.00f
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
 * ,
     */
    fun toC(value: Float): Float {
        return (value - 32.0f) / 1.8000f
    }

    /**
     * input
     *
     * temperature
     */
    @JvmStatic
    fun showNoUnit(float: Float): String {
 val str = if (SharedManager.getTemperature() == 1) {//
            String.format(Locale.ENGLISH, "%.1f", float)
        } else {
            String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
        }
        return if (str.endsWith(".0")) str.substring(0, str.length - 2) else str
    }

    /**
     * input
     *
     * temperature
     */
    @JvmStatic
    fun showWithUnit(float: Float): String {
 val str = if (SharedManager.getTemperature() == 1) {//
            String.format(Locale.ENGLISH, "%.1f", float)
        } else {
            String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
        }
        return (if (str.endsWith(".0")) str.substring(0, str.length - 2) else str) + showUnit()
    }
}