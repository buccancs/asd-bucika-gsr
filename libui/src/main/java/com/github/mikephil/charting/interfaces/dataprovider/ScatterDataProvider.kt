package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.ScatterData

/**
 * Scatter data provider interface
 * Converted from Java to Kotlin
 */
interface ScatterDataProvider : BarLineScatterCandleBubbleDataProvider {
    fun getScatterData(): ScatterData
}