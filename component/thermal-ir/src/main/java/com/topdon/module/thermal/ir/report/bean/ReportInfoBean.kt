package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.topdon.lib.core.utils.CommUtils
import kotlinx.android.parcel.Parcelize

/**
 * report信息.
 *
 * report由 3 部分组成：report信息、检测条件、红外数据.
 */
@Parcelize
data class ReportInfoBean(
    val report_name: String?,    //report名称
    val report_author: String?, //作者名称
    val is_report_author: Int,  //是否显示作者名称，0、不显示 1、显示
    val report_date: String?,   //report日期
    val is_report_date: Int,    //是否显示report日期，0、不显示 1、显示
    val report_place: String?,  //report地点
    val is_report_place: Int,   //是否显示report地点，0、不显示 1、显示
    val report_watermark: String?,//report水印
    val is_report_watermark: Int, //是否显示report水印，0、不显示 1、显示
) : Parcelable {

    val is_report_name: Int = 1//是否显示report名称，0、不显示 1、显示
    val report_type: Int = 1     //reporttype，1、point line areareport
    val report_version: String = "V1.00"//report版本，当前为 V1.00
    val report_number: String = "${CommUtils.getAppName()}${System.currentTimeMillis()}"//report编号，APP名称 + 时间戳秒级
}