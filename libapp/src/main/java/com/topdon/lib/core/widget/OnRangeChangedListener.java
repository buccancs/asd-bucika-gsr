package com.topdon.lib.core.widget;

/**
 * Consolidated OnRangeChangedListener interface supporting all variations:
 * - libui version with tempMode parameter and tracking methods
 * - CommonComponent version (simpler)
 * - jaygoo version with additional callbacks
 */
public interface OnRangeChangedListener {
    
    /**
     * Called when range values change
     * @param view The RangeSeekBar that was changed
     * @param leftValue The left/minimum value  
     * @param rightValue The right/maximum value
     * @param isFromUser True if change was from user interaction
     */
    default void onRangeChanged(BaseRangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
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
    default void onRangeChanged(BaseRangeSeekBar view, float leftValue, float rightValue, boolean isFromUser, int tempMode) {
        // Delegate to simpler version by default
        onRangeChanged(view, leftValue, rightValue, isFromUser);
    }

    /**
     * Called when user starts tracking/touching the seekbar
     * @param view The RangeSeekBar being tracked
     * @param isLeft True if touching left thumb, false for right thumb
     */
    default void onStartTrackingTouch(BaseRangeSeekBar view, boolean isLeft) {
        // Default empty implementation
    }

    /**
     * Called when user stops tracking/touching the seekbar  
     * @param view The RangeSeekBar that was tracked
     * @param isLeft True if was touching left thumb, false for right thumb
     */
    default void onStopTrackingTouch(BaseRangeSeekBar view, boolean isLeft) {
        // Default empty implementation
    }
}