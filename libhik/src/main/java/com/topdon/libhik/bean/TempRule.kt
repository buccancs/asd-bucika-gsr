package com.topdon.libhik.bean

import android.graphics.Point
import com.topdon.libhik.util.ByteArrayUtil.toFloat
import com.topdon.libhik.util.ByteArrayUtil.toInt
import com.topdon.libhik.util.ByteArrayUtil.toStr

/**
 * expert 208 byte.
 * @param enable 
 * @param regionId Id
 * distance
 * @param regionType type 1- 2- 3-
 * @param regionName 
 * emissivity
 * low
 * high
 * @param aveTemp 
 * @param diffTemp 
 * high
 * high
 * low
 * low
 * @param pointCount 
 * @param pointList 
 */
data class TempRule(
    val enable: Boolean,
    val regionId: Int,
    val distance: Int,
    val regionType: Int,
    val regionName: String,
    val emissivity: Int,
    val minTemp: Float,
    val maxTemp: Float,
    val aveTemp: Float,
    val diffTemp: Float,
    val maxX: Int,
    val maxY: Int,
    val minX: Int,
    val minY: Int,
    val pointCount: Int,
    val pointList: ArrayList<Point>,
) {
    constructor(byteArray: ByteArray, index: Int) : this(
        enable = byteArray[index] == 1.toByte(),
        regionId = byteArray[index + 1].toInt() and 0xff,
        distance = byteArray.toFloat(index + 28).toInt(),
        regionType = byteArray.toInt(index + 36),
        regionName = byteArray.toStr(index + 40, 32),
        emissivity = byteArray.toFloat(index + 72).toInt(),
        minTemp = byteArray.toFloat(index + 76),
        maxTemp = byteArray.toFloat(index + 80),
        aveTemp = byteArray.toFloat(index + 84),
        diffTemp = byteArray.toFloat(index + 88),
        maxX = byteArray.toInt(index + 92),
        maxY = byteArray.toInt(index + 96),
        minX = byteArray.toInt(index + 100),
        minY = byteArray.toInt(index + 104),
        pointCount = byteArray.toInt(index + 108),
        pointList = byteArray.toPointList(index + 112, byteArray.toInt(index + 108)),
    )

    companion object {
        /**
 * index count 
         */
        private fun ByteArray.toPointList(index: Int, count: Int): ArrayList<Point> = try {
            val resultList: ArrayList<Point> = ArrayList(count)
            for (i in 0 until count) {
                val x: Int = toInt(index + i * 8)
                val y: Int = toInt(index + i * 8 + 4)
                resultList.add(Point(x, y))
            }
            resultList
        } catch (_: IndexOutOfBoundsException) {
            ArrayList(0)
        }
    }

 override fun toString(): String = "$regionId ${if (enable) "" else ""} " +
            distance
            emissivity
            low
            high
 ":${aveTemp}°C" +
 ":$pointCount"
}
