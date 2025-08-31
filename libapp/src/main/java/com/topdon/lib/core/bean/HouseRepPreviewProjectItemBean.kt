package com.topdon.lib.core.bean

/**
 * @author qiang.lv
 */
data class HouseRepPreviewProjectItemBean(
    val projectName: String = "",
    ////1-没问题 2-需维修 3-需更换
    val state: Int = 1,
    val remark: String = ""
)