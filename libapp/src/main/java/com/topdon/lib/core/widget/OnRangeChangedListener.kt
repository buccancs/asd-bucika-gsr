package com.topdon.lib.core.widget

/**
 * Consolidated OnRangeChangedListener interface supporting all variations:
 * - libui version with tempMode parameter and tracking methods
 * - CommonComponent version (simpler)
 * - jaygoo version with additional callbacks
 */
interface OnRangeChangedListener {
    
    /**
     * Called when range values change
     * @param view The RangeSeekBar that was changed
     * @param leftValue The left/minimum value  
     * @param rightValue The right/maximum value
     * @param isFromUser True if change was from user interaction
     */
    fun onRangeChanged(view: BaseRangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
        // Default implementation for backward compatibility
    }
    
    /**
     * Called when range values change with temperature mode (libui version)
     * @param view The RangeSeekBar that was changed
     * @param leftValue The left/minimum value
     * @param rightValue The right/maximum value  
     * @param isFromUser True if change was from user interaction
     * @param tempMode Current temperature mode
     */
    fun onRangeChanged(view: BaseRangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean, tempMode: Int) {
        // Delegate to simpler version by default
        onRangeChanged(view, leftValue, rightValue, isFromUser)
    }

    /**
     * Called when user starts tracking/touching the seekbar
     * @param view The RangeSeekBar being tracked
     * @param isLeft True if touching left thumb, false for right thumb
     */
    fun onStartTrackingTouch(view: BaseRangeSeekBar, isLeft: Boolean) {
        // Default empty implementation
    }

    /**
     * Called when user stops tracking/touching the seekbar  
     * @param view The RangeSeekBar that was tracked
     * @param isLeft True if was touching left thumb, false for right thumb
     */
    fun onStopTrackingTouch(view: BaseRangeSeekBar, isLeft: Boolean) {
        // Default empty implementation
    }
}