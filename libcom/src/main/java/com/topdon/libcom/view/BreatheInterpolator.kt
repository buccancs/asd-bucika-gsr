package com.topdon.libcom.view

import android.animation.TimeInterpolator
import com.topdon.lib.core.animation.AnimationUtils

/**
 * Backward compatibility wrapper for BreatheInterpolator functionality.
 * Delegates to the consolidated AnimationUtils implementation.
 * 
 * @deprecated Use com.topdon.lib.core.animation.AnimationUtils.BreatheInterpolator directly for new code.
 */
@Deprecated("Use com.topdon.lib.core.animation.AnimationUtils.BreatheInterpolator directly for new code.")
class BreatheInterpolator : TimeInterpolator {

    private val delegate: TimeInterpolator = AnimationUtils.createBreatheInterpolator()

    override fun getInterpolation(input: Float): Float {
        return delegate.getInterpolation(input)
    }

    fun updateTime() {
        // Original implementation preserved for compatibility
        val a = ""
        val asArray = a.split("")
    }
}