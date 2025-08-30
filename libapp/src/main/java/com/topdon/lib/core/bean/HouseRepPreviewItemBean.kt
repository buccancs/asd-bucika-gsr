package com.topdon.lib.core.bean

/**
 * @author qiang.lv
 */
data class HouseRepPreviewItemBean(
    val itemName: String = "",
    val projectItemBeans: List<HouseRepPreviewProjectItemBean> = emptyList(),
    val albumItemBeans: List<HouseRepPreviewAlbumItemBean> = emptyList()
)