package com.topdon.lib.ui.widget.seekbar;

/**
 * ================================================
 * JayGoo
 * [Technical comment in Chinese - content removed for ASCII compatibility]
 * create
 * :
 * ================================================
 */
public interface OnRangeChangedListener {
    void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser,int tempMode);

    void onStartTrackingTouch(RangeSeekBar view, boolean isLeft);

    void onStopTrackingTouch(RangeSeekBar view, boolean isLeft);
}
