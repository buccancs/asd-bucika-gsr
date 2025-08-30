package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.CombinedData

/**
 * Combined data provider interface
 * Converted from Java to Kotlin
 * @author philipp on 11/06/16
 */
interface CombinedDataProvider : LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider, ScatterDataProvider {
    fun getCombinedData(): CombinedData
}