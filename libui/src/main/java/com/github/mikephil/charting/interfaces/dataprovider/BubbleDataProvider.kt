package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.BubbleData

/**
 * Bubble data provider interface
 * Converted from Java to Kotlin
 */
interface BubbleDataProvider : BarLineScatterCandleBubbleDataProvider {
    fun getBubbleData(): BubbleData
}