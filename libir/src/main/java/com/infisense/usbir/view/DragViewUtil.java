package com.infisense.usbir.view;

import android.view.View;
import com.topdon.lib.core.ui.ViewInteractionUtils;

/**
 * Backward compatibility wrapper for DragViewUtil functionality.
 * All methods delegate to the consolidated ViewInteractionUtils implementation.
 * 
 * This wrapper eliminates ~94 lines of duplicate drag handling code while maintaining
 * full backward compatibility with existing libir usage patterns.
 * 
 * @deprecated Use com.topdon.lib.core.ui.ViewInteractionUtils directly for new code.
 */
public class DragViewUtil {
    
    public static void registerDragAction(View v) {
        ViewInteractionUtils.makeDraggable(v, 0L);
    }

    /**
     * 拖动View方法
     *
     * @param v     view
     * @param delay 延迟
     */
    public static void registerDragAction(View v, long delay) {
        ViewInteractionUtils.makeDraggable(v, delay);
    }
}
