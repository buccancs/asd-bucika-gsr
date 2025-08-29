package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * .
 *
 * info
 */
@Parcelize
data class ReportConditionBean(
 val ambient_humidity: String?, //
 val is_ambient_humidity: Int, //0 1
    temperature
    temperature
    emissivity
    emissivity
    distance
    distance
) : Parcelable