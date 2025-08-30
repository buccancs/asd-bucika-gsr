package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData

/**
 * Line data provider interface
 * Converted from Java to Kotlin
 */
interface LineDataProvider : BarLineScatterCandleBubbleDataProvider {
    fun getLineData(): LineData
    fun getAxis(dependency: YAxis.AxisDependency): YAxis
}