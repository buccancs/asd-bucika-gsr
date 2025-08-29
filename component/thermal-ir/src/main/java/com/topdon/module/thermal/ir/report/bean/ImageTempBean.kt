package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * temperature
 */
@Parcelize
data class ImageTempBean(
 val full: TempBean?,//
 val pointList: ArrayList<TempBean>,//
 val lineList: ArrayList<TempBean>, //
 val rectList: ArrayList<TempBean>, //
) : Parcelable {

    @Parcelize
    data class TempBean(
        high
        low
        configuration
    ) : Parcelable
}
