package com.topdon.lib.core.bean

import com.topdon.lib.core.utils.TemperatureUtil

/**
 * @author qiang.lv
 */
data class CarDetectChildBean(
    val type: Int,
    val pos: Int,
    val description: String,
    val item: String,
    val temperature: String,
    var isSelected: Boolean = false
) {
    fun buildString(): String {
        val temperatures = temperature.split("~")
        return item + TemperatureUtil.getTempStr(
            temperatures[0].toInt(),
            temperatures[1].toInt()
        )
    }
}