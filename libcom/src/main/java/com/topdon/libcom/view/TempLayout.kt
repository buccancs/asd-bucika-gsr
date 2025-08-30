package com.topdon.libcom.view

import android.content.Context
import android.util.AttributeSet
import com.topdon.lib.core.view.EnhancedTempLayout

/**
 * Backward compatibility wrapper for TempLayout
 * Delegates to EnhancedTempLayout in libapp.core
 */
class TempLayout : EnhancedTempLayout {
    
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    
    // Legacy constants maintained for compatibility
    companion object {
        val TYPE_HOT = EnhancedTempLayout.TYPE_HOT
        val TYPE_LT = EnhancedTempLayout.TYPE_COLD  
        val TYPE_A = EnhancedTempLayout.TYPE_ALTERNATE
    }
}