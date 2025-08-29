package com.topdon.lib.core.db.entity

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.Utils
import com.topdon.lib.core.R

/**
 * - reportbar.
 *
 * Created by LCG on 2024/1/15.
 */
open class HouseBase {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
 * report
     */
    @ColumnInfo
    var name: String = ""

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @ColumnInfo
    var inspectorName: String = ""

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @ColumnInfo
    var address: String = ""

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @ColumnInfo
    var imagePath: String = ""

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @ColumnInfo
    var year: Int? = null

    /**
 * .
     */
    @ColumnInfo
    var houseSpace: String = ""

    /**
 * 0- 1- 2-
     */
    @ColumnInfo
    var houseSpaceUnit: Int = 0

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @ColumnInfo
    var cost: String = ""

    /**
 * 0-USD 1-EUR 2-GBP 3-AUD 4-JPY 5-CAD 6-NZD 7-RMB 8-HKD
     */
    @ColumnInfo
    var costUnit: Int = 0

    /**
     * time
     */
    @ColumnInfo
    var detectTime: Long = 0

    /**
     * create
     */
    @ColumnInfo
    var createTime: Long = 0

    /**
     * time
     */
    @ColumnInfo
    var updateTime: Long = 0




    override fun equals(other: Any?): Boolean = other is HouseBase && other.id == id

    override fun hashCode(): Int = id.toInt()


    /**
 * .
     */
    fun getSpaceUnitStr(): String = when (houseSpaceUnit) {
        0 -> "ac"
        1 -> "m²"
        else -> "ha"
    }

    /**
 * .
     */
    fun getCostUnitStr(): String = when (costUnit) {
 1 -> "EUR" //EUR
 2 -> "GBP" //GBP
 3 -> "AUD" //AUD
 4 -> "JPY" //JPY
 5 -> "CAD" //CAD
 6 -> "NZD" //NZD
 7 -> "RMB" //RMB
 8 -> "HKD" //HKD
 else -> "USD" //USD
    }

    /**
 * report PDF 
     */
    fun getPdfFileName(): String = "TC_${TimeUtils.millis2String(createTime, "yyyyMMdd_HHmmss")}.pdf"
}



/**
 * - .
 */
@Entity
class HouseDetect : HouseBase() {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @Ignore
    var dirList: ArrayList<DirDetect> = ArrayList()

    /**
     * add
     */
    fun copyOne(): HouseDetect {
        val newDetect = HouseDetect()
        newDetect.id = 0
        newDetect.name = "$name(1)"
        newDetect.inspectorName = inspectorName
        newDetect.address = address
        newDetect.imagePath = imagePath
        newDetect.year = year
        newDetect.houseSpace = houseSpace
        newDetect.houseSpaceUnit = houseSpaceUnit
        newDetect.cost = cost
        newDetect.costUnit = costUnit
        newDetect.detectTime = detectTime
        newDetect.createTime = createTime
        newDetect.updateTime = updateTime
        return newDetect
    }

    fun toHouseReport(): HouseReport {
        val houseReport = HouseReport()
        houseReport.id = 0
        houseReport.name = name
        houseReport.inspectorName = inspectorName
        houseReport.address = address
        houseReport.imagePath = imagePath
        houseReport.year = year
        houseReport.houseSpace = houseSpace
        houseReport.houseSpaceUnit = houseSpaceUnit
        houseReport.cost = cost
        houseReport.costUnit = costUnit
        houseReport.detectTime = detectTime
        houseReport.createTime = createTime
        houseReport.updateTime = updateTime

        val newDirList: ArrayList<DirReport> = ArrayList(dirList.size)
        for (dirDetect in dirList) {
            if (dirDetect.itemList.isNotEmpty()) {
                val dirRepost: DirReport = dirDetect.toDirReport()
                if (dirRepost.itemList.isNotEmpty()) {
                    newDirList.add(dirRepost)
                }
            }
        }
        houseReport.dirList = newDirList
        return houseReport
    }
}



/**
 * - report.
 */
@Entity
class HouseReport : HouseBase() {
    /**
 * （）
     */
    @ColumnInfo
    var inspectorWhitePath: String = ""
    /**
 * （）
     */
    @ColumnInfo
    var inspectorBlackPath: String = ""


    /**
 * （）
     */
    @ColumnInfo
    var houseOwnerWhitePath: String = ""
    /**
 * （）
     */
    @ColumnInfo
    var houseOwnerBlackPath: String = ""



    /**
 * report
     */
    @Ignore
    var dirList: ArrayList<DirReport> = ArrayList()
}