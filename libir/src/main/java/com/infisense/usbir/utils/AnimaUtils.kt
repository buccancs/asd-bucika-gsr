package com.infisense.usbir.utils

import android.view.animation.*
import com.topdon.lib.core.animation.AnimationUtils

/**
 * Backward compatibility wrapper for AnimaUtils functionality.
 * All methods delegate to the consolidated AnimationUtils implementation.
 * 
 * This wrapper eliminates ~349 lines of duplicate animation code while maintaining
 * full backward compatibility with existing libir usage patterns.
 * 
 * @deprecated Use com.topdon.lib.core.animation.AnimationUtils directly for new code.
 */
@Suppress("UNUSED")
object AnimaUtils {
    
    /**
     * Default animation duration
     */
    const val DEFAULT_ANIMATION_DURATION: Long = AnimationUtils.DEFAULT_ANIMATION_DURATION

    // === Rotation animations ===
    
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
    ) = AnimationUtils.getRotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, 
        pivotYType, pivotYValue, durationMillis, animationListener)

    @JvmStatic
    fun getRotateAnimationByCenter(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ) = AnimationUtils.getRotateAnimationByCenter(durationMillis, animationListener)

    @JvmStatic
    fun getRotateAnimationByCenter(duration: Long) = 
        AnimationUtils.getRotateAnimationByCenter(duration, null)

    @JvmStatic
    fun getRotateAnimationByCenter(animationListener: Animation.AnimationListener?) = 
        AnimationUtils.getRotateAnimationByCenter(DEFAULT_ANIMATION_DURATION, animationListener)

    @JvmStatic
    val rotateAnimationByCenter: RotateAnimation
        get() = AnimationUtils.rotateAnimationByCenter

    // === Alpha animations ===
    
    @JvmStatic
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ) = AnimationUtils.getAlphaAnimation(fromAlpha, toAlpha, durationMillis, animationListener)

    @JvmStatic
    fun getAlphaAnimation(fromAlpha: Float, toAlpha: Float, durationMillis: Long) = 
        AnimationUtils.getAlphaAnimation(fromAlpha, toAlpha, durationMillis, null)

    @JvmStatic
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        animationListener: Animation.AnimationListener?
    ) = AnimationUtils.getAlphaAnimation(fromAlpha, toAlpha, DEFAULT_ANIMATION_DURATION, animationListener)

    @JvmStatic
    fun getAlphaAnimation(fromAlpha: Float, toAlpha: Float) = 
        AnimationUtils.getAlphaAnimation(fromAlpha, toAlpha, DEFAULT_ANIMATION_DURATION, null)

    // === Hide animations ===
    
    @JvmStatic
    fun getHiddenAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ) = AnimationUtils.getHiddenAlphaAnimation(durationMillis, animationListener)

    @JvmStatic
    fun getHiddenAlphaAnimation(durationMillis: Long) = 
        AnimationUtils.getHiddenAlphaAnimation(durationMillis, null)

    @JvmStatic
    fun getHiddenAlphaAnimation(animationListener: Animation.AnimationListener?) = 
        AnimationUtils.getHiddenAlphaAnimation(DEFAULT_ANIMATION_DURATION, animationListener)

    @JvmStatic
    val hiddenAlphaAnimation: AlphaAnimation
        get() = AnimationUtils.hiddenAlphaAnimation

    // === Show animations ===
    
    @JvmStatic
    fun getShowAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ) = AnimationUtils.getShowAlphaAnimation(durationMillis, animationListener)

    @JvmStatic
    fun getShowAlphaAnimation(durationMillis: Long) = 
        AnimationUtils.getShowAlphaAnimation(durationMillis, null)

    @JvmStatic
    fun getShowAlphaAnimation(animationListener: Animation.AnimationListener?) = 
        AnimationUtils.getShowAlphaAnimation(DEFAULT_ANIMATION_DURATION, animationListener)

    @JvmStatic
    val showAlphaAnimation: AlphaAnimation
        get() = AnimationUtils.showAlphaAnimation

    // === Scale animations ===
    
    @JvmStatic
    fun getLessenScaleAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ) = AnimationUtils.getLessenScaleAnimation(durationMillis, animationListener)

    @JvmStatic
    fun getLessenScaleAnimation(durationMillis: Long) = 
        AnimationUtils.getLessenScaleAnimation(durationMillis, null)

    @JvmStatic
    fun getLessenScaleAnimation(animationListener: Animation.AnimationListener?) = 
        AnimationUtils.getLessenScaleAnimation(DEFAULT_ANIMATION_DURATION, animationListener)

    @JvmStatic
    fun getAmplificationAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ) = AnimationUtils.getAmplificationAnimation(durationMillis, animationListener)

    @JvmStatic
    fun getAmplificationAnimation(durationMillis: Long) = 
        AnimationUtils.getAmplificationAnimation(durationMillis, null)

    @JvmStatic
    fun getAmplificationAnimation(animationListener: Animation.AnimationListener?) = 
        AnimationUtils.getAmplificationAnimation(DEFAULT_ANIMATION_DURATION, animationListener)
}