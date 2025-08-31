package com.topdon.lib.core.bean

/**
 * @author qiang.lv
 */
data class HouseRepPreviewBean(
    val housePhoto: String = "",
    val houseAddress: String = "",
    val houseName: String = "",
    val detectTime: String = "",
    val inspectorName: String = "",
    val houseYear: String = "",
    val houseArea: String = "",
    val expenses: String = "",
    val itemBeans: List<HouseRepPreviewItemBean> = emptyList(),
    val inspectorWhitePath: String = "",
    val houseOwnerWhitePath: String = ""
)