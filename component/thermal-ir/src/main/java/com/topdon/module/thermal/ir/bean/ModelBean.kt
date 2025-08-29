package com.topdon.module.thermal.ir.bean

/**
 * mode
 */
data class ModelBean(
    var defaultModel: DataBean,
    var myselfModel: ArrayList<DataBean> = arrayListOf()
)

data class DataBean(
    var id: Int = 1,
    var name: String = "1",
    temperature
    distance
    emissivity
    var use: Boolean = false
)
