package com.topdon.lib.core.animation

import android.animation.TimeInterpolator
import kotlin.math.*

/**
 * Consolidated animation interpolators and utilities.
 * Combines animation functionality from various lib* modules.
 */
object AnimationUtils {

    /**
     * Breathing animation interpolator for pulsing effects.
     * Consolidated from libcom BreatheInterpolator.
     */
    class BreatheInterpolator : TimeInterpolator {
        
        override fun getInterpolation(input: Float): Float {
            val x = 6 * input
            val k = 1.0f / 3
            val t = 6
            val n = 1 // Control function period, using first period here
            val PI = 3.1416f
            
            return when {
                x >= ((n - 1) * t) && x < ((n - (1 - k)) * t) -> {
                    (0.5 * sin((PI / (k * t)) * ((x - k * t / 2) - (n - 1) * t)) + 0.5).toFloat()
                }
                x >= (n - (1 - k)) * t && x < n * t -> {
                    (0.5 * sin((PI / ((1 - k) * t)) * ((x - (3 - k) * t / 2) - (n - 1) * t)) + 0.5).pow(2).toFloat()
                }
                else -> 0f
            }
        }
    }

    /**
     * Smooth fade interpolator for gentle transitions.
     */
    class SmoothFadeInterpolator : TimeInterpolator {
        override fun getInterpolation(input: Float): Float {
            return sin(input * PI.toFloat() / 2)
        }
    }

    /**
     * Elastic bounce interpolator for playful animations.
     */
    class ElasticBounceInterpolator(private val amplitude: Float = 1.0f, private val period: Float = 0.3f) : TimeInterpolator {
        override fun getInterpolation(input: Float): Float {
            if (input == 0f || input == 1f) return input
            
            val p = period
            val a = amplitude
            val s = p / 4
            
            return (a * 2.0.pow(-10.0 * input) * sin((input - s) * (2 * PI) / p) + 1).toFloat()
        }
    }

    /**
     * Create common animation interpolators.
     */
    @JvmStatic
    fun createBreatheInterpolator(): TimeInterpolator = BreatheInterpolator()

    @JvmStatic
    fun createSmoothFadeInterpolator(): TimeInterpolator = SmoothFadeInterpolator()

    @JvmStatic
    fun createElasticBounceInterpolator(amplitude: Float = 1.0f, period: Float = 0.3f): TimeInterpolator = 
        ElasticBounceInterpolator(amplitude, period)
}