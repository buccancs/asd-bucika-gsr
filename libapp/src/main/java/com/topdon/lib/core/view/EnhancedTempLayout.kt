package com.topdon.lib.core.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.topdon.lib.core.animation.BreatheInterpolator

/**
 * Enhanced temperature layout with breathing animation for alarm visualization
 * Consolidated from libcom.view.TempLayout with enhanced capabilities
 */
class EnhancedTempLayout : LinearLayout {

    companion object {
        const val TYPE_HOT = 1       // High temperature warning
        const val TYPE_COLD = 2      // Low temperature warning  
        const val TYPE_ALTERNATE = 3 // High/low temperature alternating warning
        const val TYPE_CUSTOM = 4    // Custom animation type
    }

    private var alphaAnimator: ObjectAnimator? = null
    private var rootView: View? = null
    private var backgroundView: View? = null
    private var isHotMode: Boolean = true
    private var currentType = -1
    private var animatorAlpha = 1f

    // Enhanced properties
    private var customAnimationDuration = 500L
    private var customInterpolator = BreatheInterpolator()
    private var onAnimationStateListener: AnimationStateListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        // Create a default view if no custom layout is provided
        rootView = createDefaultView()
        backgroundView = rootView?.findViewById(getBackgroundViewId())

        setupAnimator()
    }

    private fun createDefaultView(): View {
        // Create a simple default view structure
        val view = View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            alpha = 0f
        }
        addView(view)
        return view
    }

    private fun getBackgroundViewId(): Int {
        // Try to find a background view ID, return 0 if not found
        return try {
            context.resources.getIdentifier("bg", "id", context.packageName)
        } catch (e: Exception) {
            0
        }
    }

    private fun setupAnimator() {
        alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
            duration = customAnimationDuration
            interpolator = customInterpolator
            repeatCount = ValueAnimator.INFINITE
            
            addUpdateListener { animation ->
                animatorAlpha = animation.getAnimatedValue("alpha") as Float
                onAnimationStateListener?.onAlphaChanged(animatorAlpha)
            }
        }
    }

    /**
     * Start animation with specified type
     */
    fun startAnimation(type: Int) {
        visibility = View.VISIBLE
        onAnimationStateListener?.onAnimationStarted(type)

        if (currentType != type) {
            alphaAnimator?.cancel()
            alphaAnimator?.removeAllListeners()
            
            configureAnimationForType(type)
            currentType = type
        }

        alphaAnimator?.start()
    }

    private fun configureAnimationForType(type: Int) {
        when (type) {
            TYPE_HOT -> {
                isHotMode = true
                alphaAnimator?.repeatCount = ValueAnimator.INFINITE
                setBackgroundForHot()
            }
            TYPE_COLD -> {
                isHotMode = false
                alphaAnimator?.repeatCount = ValueAnimator.INFINITE
                setBackgroundForCold()
            }
            TYPE_ALTERNATE -> {
                alphaAnimator?.repeatCount = 0
                alphaAnimator?.addListener(alternatingAnimatorListener)
                setBackgroundForHot() // Start with hot
            }
            TYPE_CUSTOM -> {
                configureCustomAnimation()
            }
        }
    }

    private fun setBackgroundForHot() {
        try {
            val hotBgRes = context.resources.getIdentifier("ic_ir_read_bg", "drawable", context.packageName)
            if (hotBgRes != 0) {
                backgroundView?.setBackgroundResource(hotBgRes)
            } else {
                // Fallback to color
                setBackgroundColor(0xFFFF4444.toInt())
            }
        } catch (e: Exception) {
            setBackgroundColor(0xFFFF4444.toInt())
        }
    }

    private fun setBackgroundForCold() {
        try {
            val coldBgRes = context.resources.getIdentifier("ic_ir_blue_bg", "drawable", context.packageName)
            if (coldBgRes != 0) {
                backgroundView?.setBackgroundResource(coldBgRes)
            } else {
                // Fallback to color
                setBackgroundColor(0xFF4444FF.toInt())
            }
        } catch (e: Exception) {
            setBackgroundColor(0xFF4444FF.toInt())
        }
    }

    private fun configureCustomAnimation() {
        // Allow for custom configuration
        onAnimationStateListener?.onCustomAnimationConfigured(this)
    }

    private val alternatingAnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            onAnimationStateListener?.onAlternationStarted()
        }

        override fun onAnimationEnd(animation: Animator) {
            if (visibility == View.VISIBLE) {
                isHotMode = !isHotMode
                if (isHotMode) {
                    setBackgroundForHot()
                } else {
                    setBackgroundForCold()
                }
                alphaAnimator?.start()
                onAnimationStateListener?.onAlternationCycled(isHotMode)
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            onAnimationStateListener?.onAnimationCancelled()
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }

    /**
     * Stop all animations and hide the view
     */
    fun stopAnimation() {
        currentType = -1
        alphaAnimator?.removeAllListeners()
        alphaAnimator?.cancel()
        visibility = View.GONE
        onAnimationStateListener?.onAnimationStopped()
    }

    /**
     * Start breathing alpha animation (legacy method)
     */
    fun startAlphaBreathAnimation() {
        alphaAnimator?.start()
    }

    /**
     * Set custom animation duration
     */
    fun setAnimationDuration(duration: Long) {
        customAnimationDuration = duration
        alphaAnimator?.duration = duration
    }

    /**
     * Set custom interpolator for animation
     */
    fun setAnimationInterpolator(interpolator: android.view.animation.Interpolator) {
        customInterpolator = interpolator as android.animation.TimeInterpolator
        alphaAnimator?.interpolator = customInterpolator
    }

    /**
     * Get current animation state
     */
    fun getCurrentType(): Int = currentType

    /**
     * Check if animation is currently running
     */
    fun isAnimationRunning(): Boolean = alphaAnimator?.isRunning == true

    /**
     * Get current alpha value from animation
     */
    fun getAnimatorAlpha(): Float = animatorAlpha

    /**
     * Set listener for animation state changes
     */
    fun setAnimationStateListener(listener: AnimationStateListener) {
        onAnimationStateListener = listener
    }

    /**
     * Enhanced capabilities for temperature visualization
     */
    fun setTemperatureColors(hotColor: Int, coldColor: Int) {
        // Store colors for dynamic background changes
        post {
            if (isHotMode) {
                setBackgroundColor(hotColor)
            } else {
                setBackgroundColor(coldColor)
            }
        }
    }

    /**
     * Pulse animation for immediate alerts
     */
    fun pulseOnce(type: Int) {
        visibility = View.VISIBLE
        configureAnimationForType(type)
        alphaAnimator?.repeatCount = 1
        alphaAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
                alphaAnimator?.removeListener(this)
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        alphaAnimator?.start()
    }

    /**
     * Interface for listening to animation state changes
     */
    interface AnimationStateListener {
        fun onAnimationStarted(type: Int) {}
        fun onAnimationStopped() {}
        fun onAnimationCancelled() {}
        fun onAlphaChanged(alpha: Float) {}
        fun onAlternationStarted() {}
        fun onAlternationCycled(isHotMode: Boolean) {}
        fun onCustomAnimationConfigured(view: EnhancedTempLayout) {}
    }
}