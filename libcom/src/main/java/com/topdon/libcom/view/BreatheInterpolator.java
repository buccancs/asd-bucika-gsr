package com.topdon.libcom.view;

import android.animation.TimeInterpolator;
import com.topdon.lib.core.animation.AnimationUtils;

/**
 * Backward compatibility wrapper for BreatheInterpolator functionality.
 * Delegates to the consolidated AnimationUtils implementation.
 * 
 * @deprecated Use com.topdon.lib.core.animation.AnimationUtils.BreatheInterpolator directly for new code.
 */
class BreatheInterpolator implements TimeInterpolator {

    private final TimeInterpolator delegate;

    public BreatheInterpolator() {
        this.delegate = AnimationUtils.createBreatheInterpolator();
    }

    @Override
    public float getInterpolation(float input) {
        return delegate.getInterpolation(input);
    }

    public void updateTime() {
        // Original implementation preserved for compatibility
        String a = "";
        String[] as = a.split("");
    }
}
