package com.topdon.lib.core.animation

import android.animation.TimeInterpolator
import android.view.animation.*
import kotlin.math.*

/**
 * Consolidated animation interpolators and utilities.
 * Combines animation functionality from libcom BreatheInterpolator and libir AnimaUtils.
 */
object AnimationUtils {

    // === Constants ===
    const val DEFAULT_ANIMATION_DURATION: Long = 400

    // === Interpolators ===
    
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

    // === Factory methods for interpolators ===

    @JvmStatic
    fun createBreatheInterpolator(): TimeInterpolator = BreatheInterpolator()

    @JvmStatic
    fun createSmoothFadeInterpolator(): TimeInterpolator = SmoothFadeInterpolator()

    @JvmStatic
    fun createElasticBounceInterpolator(amplitude: Float = 1.0f, period: Float = 0.3f): TimeInterpolator = 
        ElasticBounceInterpolator(amplitude, period)

    // === View animation utilities (consolidated from libir AnimaUtils) ===

    /**
     * Create rotation animation around center.
     */
    @JvmStatic
    fun getRotateAnimation(
        fromDegrees: Float,
        toDegrees: Float,
        pivotXType: Int,
        pivotXValue: Float,
        pivotYType: Int,
        pivotYValue: Float,
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): RotateAnimation {
        val rotateAnimation = RotateAnimation(
            fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue
        )
        rotateAnimation.duration = durationMillis
        animationListener?.let { rotateAnimation.setAnimationListener(it) }
        return rotateAnimation
    }

    /**
     * Create rotation animation around view center.
     */
    @JvmStatic
    fun getRotateAnimationByCenter(
        durationMillis: Long = DEFAULT_ANIMATION_DURATION,
        animationListener: Animation.AnimationListener? = null
    ): RotateAnimation {
        return getRotateAnimation(
            0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f, durationMillis, animationListener
        )
    }

    /**
     * Create alpha animation.
     */
    @JvmStatic
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long = DEFAULT_ANIMATION_DURATION,
        animationListener: Animation.AnimationListener? = null
    ): AlphaAnimation {
        val alphaAnimation = AlphaAnimation(fromAlpha, toAlpha)
        alphaAnimation.duration = durationMillis
        animationListener?.let { alphaAnimation.setAnimationListener(it) }
        return alphaAnimation
    }

    /**
     * Create fade out animation.
     */
    @JvmStatic
    fun getHiddenAlphaAnimation(
        durationMillis: Long = DEFAULT_ANIMATION_DURATION,
        animationListener: Animation.AnimationListener? = null
    ): AlphaAnimation {
        return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener)
    }

    /**
     * Create fade in animation.
     */
    @JvmStatic
    fun getShowAlphaAnimation(
        durationMillis: Long = DEFAULT_ANIMATION_DURATION,
        animationListener: Animation.AnimationListener? = null
    ): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, animationListener)
    }

    /**
     * Create scale down animation.
     */
    @JvmStatic
    fun getLessenScaleAnimation(
        durationMillis: Long = DEFAULT_ANIMATION_DURATION,
        animationListener: Animation.AnimationListener? = null
    ): ScaleAnimation {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.0f, 1.0f, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = durationMillis
        animationListener?.let { scaleAnimation.setAnimationListener(it) }
        return scaleAnimation
    }

    /**
     * Create scale up animation.
     */
    @JvmStatic
    fun getAmplificationAnimation(
        durationMillis: Long = DEFAULT_ANIMATION_DURATION,
        animationListener: Animation.AnimationListener? = null
    ): ScaleAnimation {
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = durationMillis
        animationListener?.let { scaleAnimation.setAnimationListener(it) }
        return scaleAnimation
    }

    // === Convenience properties for common animations ===

    @JvmStatic
    val rotateAnimationByCenter: RotateAnimation
        get() = getRotateAnimationByCenter()

    @JvmStatic
    val hiddenAlphaAnimation: AlphaAnimation
        get() = getHiddenAlphaAnimation()

    @JvmStatic
    val showAlphaAnimation: AlphaAnimation
        get() = getShowAlphaAnimation()
}