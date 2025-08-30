package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.CandleData

/**
 * Candle data provider interface
 * Converted from Java to Kotlin
 */
interface CandleDataProvider : BarLineScatterCandleBubbleDataProvider {
    fun getCandleData(): CandleData
}