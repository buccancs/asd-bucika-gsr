package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.BarData

/**
 * Bar data provider interface
 * Converted from Java to Kotlin
 */
interface BarDataProvider : BarLineScatterCandleBubbleDataProvider {
    fun getBarData(): BarData
    fun isDrawBarShadowEnabled(): Boolean
    fun isDrawValueAboveBarEnabled(): Boolean  
    fun isHighlightFullBarEnabled(): Boolean
}