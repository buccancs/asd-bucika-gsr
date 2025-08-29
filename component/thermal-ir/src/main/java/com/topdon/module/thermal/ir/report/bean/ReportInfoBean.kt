package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.topdon.lib.core.utils.CommUtils
import kotlinx.android.parcel.Parcelize

/**
 * info
 *
 * info
 */
@Parcelize
data class ReportInfoBean(
 val report_name: String?, //report
    author
    author
 val report_date: String?, //report
 val is_report_date: Int, //report0 1
 val report_place: String?, //report
 val is_report_place: Int, //report0 1
 val report_watermark: String?,//report
 val is_report_watermark: Int, //report0 1
) : Parcelable {

 val is_report_name: Int = 1//report0 1
    val report_type: Int = 1     //reporttype，1、point line areareport
 val report_version: String = "V1.00"//report V1.00
    time
}