package com.github.mikephil.charting.exception

/**
 * Drawing data set not created exception
 * Converted from Java to Kotlin
 */
class DrawingDataSetNotCreatedException(message: String = "Have to create a new drawing set first. Call ChartData.addDataSet() or create a new ChartData object with a list of DataSets in the constructor.") : Exception(message)