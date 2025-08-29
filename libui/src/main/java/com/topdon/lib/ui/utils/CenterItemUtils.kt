package com.topdon.lib.ui.utils

/**
 * @author: CaiSongL
 * @date: 2023/3/31 9:56
 */
internal object CenterItemUtils {
    fun getMinDifferItem(itemHeights: List<CenterViewItem>): CenterViewItem {
 var minItem = itemHeights[0] //
        for (i in itemHeights.indices) {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            if (itemHeights[i].differ <= minItem.differ) {
                minItem = itemHeights[i]
            }
        }
        return minItem
    }

    class CenterViewItem
 //Item
    // medium
        (var position: Int, var differ: Int)
}