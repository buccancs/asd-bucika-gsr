package com.topdon.lib.ui.widget.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import com.topdon.lib.core.widget.BaseRangeSeekBar;

/**
 * Backward compatibility wrapper for libui RangeSeekBar.
 * Extends the consolidated BaseRangeSeekBar while preserving the original API.
 * 
 * @deprecated Use com.topdon.lib.core.widget.BaseRangeSeekBar directly for new code.
 */
public class RangeSeekBarWrapper extends BaseRangeSeekBar {

    // Maintain original interface for backward compatibility
    private OnRangeChangedListener mOriginalListener;

    public RangeSeekBarWrapper(Context context) {
        super(context);
    }

    public RangeSeekBarWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RangeSeekBarWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnRangeChangedListener(com.topdon.lib.core.widget.OnRangeChangedListener listener) {
        // Wrap the consolidated listener to work with original interface
        super.setOnRangeChangedListener(new com.topdon.lib.core.widget.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(BaseRangeSeekBar view, float leftValue, float rightValue, boolean isFromUser, int tempMode) {
                if (mOriginalListener != null) {
                    // Convert BaseRangeSeekBar to RangeSeekBar for backward compatibility
                    mOriginalListener.onRangeChanged(RangeSeekBar.this, leftValue, rightValue, isFromUser, tempMode);
                }
            }
            
            @Override
            public void onStartTrackingTouch(BaseRangeSeekBar view, boolean isLeft) {
                if (mOriginalListener != null) {
                    mOriginalListener.onStartTrackingTouch(RangeSeekBar.this, isLeft);
                }
            }
            
            @Override
            public void onStopTrackingTouch(BaseRangeSeekBar view, boolean isLeft) {
                if (mOriginalListener != null) {
                    mOriginalListener.onStopTrackingTouch(RangeSeekBar.this, isLeft);
                }
            }
        });
    }

    // Original libui interface method
    public void setOnRangeChangedListener(OnRangeChangedListener listener) {
        mOriginalListener = listener;
        
        if (listener != null) {
            // Set up adapter to consolidated interface
            setOnRangeChangedListener((com.topdon.lib.core.widget.OnRangeChangedListener) null);
            setOnRangeChangedListener((com.topdon.lib.core.widget.OnRangeChangedListener) new com.topdon.lib.core.widget.OnRangeChangedListener() {
                @Override
                public void onRangeChanged(BaseRangeSeekBar view, float leftValue, float rightValue, boolean isFromUser, int tempMode) {
                    listener.onRangeChanged(RangeSeekBar.this, leftValue, rightValue, isFromUser, tempMode);
                }
                
                @Override
                public void onStartTrackingTouch(BaseRangeSeekBar view, boolean isLeft) {
                    listener.onStartTrackingTouch(RangeSeekBar.this, isLeft);
                }
                
                @Override
                public void onStopTrackingTouch(BaseRangeSeekBar view, boolean isLeft) {
                    listener.onStopTrackingTouch(RangeSeekBar.this, isLeft);
                }
            });
        }
    }

    /**
     * Original libui OnRangeChangedListener interface maintained for backward compatibility
     */
    public interface OnRangeChangedListener {
        void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser, int tempMode);
        void onStartTrackingTouch(RangeSeekBar view, boolean isLeft);
        void onStopTrackingTouch(RangeSeekBar view, boolean isLeft);
    }
}