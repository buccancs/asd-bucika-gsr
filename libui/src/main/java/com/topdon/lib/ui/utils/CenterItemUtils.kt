package com.topdon.lib.ui.utils

import com.topdon.lib.core.widget.WidgetUtils

/**
 * Backward compatibility wrapper for CenterItemUtils functionality.
 * All methods delegate to the consolidated WidgetUtils implementation.
 * 
 * This wrapper eliminates ~23 lines of duplicate center item calculation code while maintaining
 * full backward compatibility with existing libui usage patterns.
 * 
 * @deprecated Use com.topdon.lib.core.widget.WidgetUtils directly for new code.
 */
internal object CenterItemUtils {
    
    fun getMinDifferItem(itemHeights: List<CenterViewItem>): CenterViewItem {
        val coreItems = itemHeights.map { 
            WidgetUtils.CenterViewItem(it.position, it.differ) 
        }
        val result = WidgetUtils.getMinDifferItem(coreItems)
        return CenterViewItem(result.position, result.differ)
    }

    class CenterViewItem(var position: Int, var differ: Int)
}