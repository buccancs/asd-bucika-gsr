package com.topdon.libcom.view

import android.content.Context
import com.topdon.lib.core.view.EnhancedLoadMoreView

/**
 * Backward compatibility wrapper for CommLoadMoreView
 * Delegates to EnhancedLoadMoreView in libapp.core
 */
class CommLoadMoreView(context: Context = getDefaultContext()) : EnhancedLoadMoreView(context) {
    
    companion object {
        private fun getDefaultContext(): Context {
            // This would need to be properly implemented based on your application context management
            // For now, this is a placeholder
            throw IllegalStateException("Context required for CommLoadMoreView")
        }
    }
    
    // Constructor for legacy usage patterns
    constructor() : this(getDefaultContext())
}