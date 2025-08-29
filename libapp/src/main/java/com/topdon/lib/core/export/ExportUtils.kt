package com.topdon.lib.core.export

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.UriUtils
import com.blankj.utilcode.util.Utils
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.tools.UnitTools
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Consolidated export utilities for various data formats.
 * Combines functionality from libcom ExcelUtil and similar export utilities.
 */
object ExportUtils {

    private const val TAG = "ExportUtils"

    /**
     * Export thermal data to Excel format.
     */
    @JvmStatic
    fun exportToExcel(
        thermalList: List<ThermalEntity>,
        fileName: String = generateDefaultFileName()
    ): Uri? {
        return try {
            val workbook = createExcelWorkbook(thermalList)
            val uri = saveWorkbook(workbook, fileName)
            workbook.close()
            uri
        } catch (e: Exception) {
            Log.e(TAG, "Excel export failed", e)
            null
        }
    }

    /**
     * Export thermal data to PDF format.
     */
    @JvmStatic
    fun exportToPDF(thermalList: List<ThermalEntity>, fileName: String = generateDefaultFileName()): Uri? {
        // Implementation for PDF export
        // TODO: Add PDF export functionality when needed
        return null
    }

    /**
     * Export thermal matrix data to Excel format (specialized for raw thermal data).
     */
    @JvmStatic
    fun exportThermalMatrix(
        name: String,
        width: Int,
        height: Int,
        thermalData: ByteArray,
        callback: ThermalExportCallback? = null
    ): String? {
        return try {
            val workbook = createThermalMatrixWorkbook(name, width, height, thermalData, callback)
            val filePath = saveThermalMatrixWorkbook(workbook, name)
            workbook.close()
            filePath
        } catch (e: Exception) {
            Log.e(TAG, "Thermal matrix export failed", e)
            null
        }
    }

    interface ThermalExportCallback {
        fun onOneCell(current: Int, total: Int)
    }

    private fun createThermalMatrixWorkbook(
        name: String,
        width: Int,
        height: Int,
        thermalData: ByteArray,
        callback: ThermalExportCallback?
    ): Workbook {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet()
        val isShowC = SharedManager.getTemperature() == 1

        val cellStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
        }

        for (i in 0 until height) {
            val row = sheet.createRow(i)
            for (j in 0 until width) {
                val index = i * width + j
                sheet.setColumnWidth(j, 9 * width)
                val cell = row.createCell(j)
                cell.cellStyle = cellStyle
                cell.setCellValue(getTemperatureFromMatrix(index, thermalData, isShowC))
                
                if (index % 100 == 0 && callback != null) {
                    callback.onOneCell(index / 100, width * height / 100)
                }
            }
        }

        return workbook
    }

    private fun saveThermalMatrixWorkbook(workbook: Workbook, name: String): String? {
        return try {
            val fileName = "$name.xlsx"
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val excel = File(FileConfig.getExcelDir(), fileName)
                val fileOutputStream = FileOutputStream(excel)
                val bufferedOutputStream = BufferedOutputStream(fileOutputStream)
                workbook.write(bufferedOutputStream)
                bufferedOutputStream.close()
                excel.absolutePath
            } else {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }

                val uri = Utils.getApp().contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let { 
                    val outputStream = Utils.getApp().contentResolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        val bufferedStream = BufferedOutputStream(stream)
                        workbook.write(bufferedStream)
                        bufferedStream.flush()
                    }
                    UriUtils.uri2File(it)?.absolutePath
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save thermal matrix workbook", e)
            null
        }
    }

    private fun getTemperatureFromMatrix(index: Int, norTempData: ByteArray, isShowC: Boolean): String {
        val tempValue = (norTempData[2 * index + 1].toInt() shl 8 and 0xff00) or (norTempData[2 * index].toInt() and 0xff)
        val value = tempValue / 64f - 273.15f
        return UnitTools.showC(value, isShowC)
    }

    private fun createExcelWorkbook(thermalList: List<ThermalEntity>): Workbook {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Thermal Data")
        
        // Create header style
        val headerStyle = workbook.createCellStyle().apply {
            setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index)
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            
            val font = workbook.createFont().apply {
                bold = true
            }
            setFont(font)
        }

        // Create header row
        val headerRow = sheet.createRow(0)
        val headers = arrayOf("Date", "Time", "Min Temp", "Max Temp", "Avg Temp", "Unit", "Emissivity", "Distance")
        
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // Create data rows
        val tempUnit = SharedManager.getTemperatureUnit()
        thermalList.forEachIndexed { index, thermalEntity ->
            val row = sheet.createRow(index + 1)
            
            val time = TimeTool.getDateByFileTime(thermalEntity.createTime)
            val tempValues = getTempValues(thermalEntity, tempUnit)
            
            row.createCell(0).setCellValue(TimeUtils.millis2String(time, "yyyy-MM-dd"))
            row.createCell(1).setCellValue(TimeUtils.millis2String(time, "HH:mm:ss"))
            row.createCell(2).setCellValue(tempValues.minTemp)
            row.createCell(3).setCellValue(tempValues.maxTemp) 
            row.createCell(4).setCellValue(tempValues.avgTemp)
            row.createCell(5).setCellValue(UnitTools.getTemperatureUnit())
            row.createCell(6).setCellValue(thermalEntity.emissivity.toString())
            row.createCell(7).setCellValue("${thermalEntity.distance}m")
        }

        // Auto-size columns
        for (i in headers.indices) {
            sheet.autoSizeColumn(i)
        }

        return workbook
    }

    private fun saveWorkbook(workbook: Workbook, fileName: String): Uri? {
        return try {
            val uri = createFileUri(fileName)
            val outputStream = Utils.getApp().contentResolver.openOutputStream(uri)
            
            outputStream?.use { stream ->
                val bufferedStream = BufferedOutputStream(stream)
                workbook.write(bufferedStream)
                bufferedStream.flush()
            }
            
            uri
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save workbook", e)
            null
        }
    }

    private fun createFileUri(fileName: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName)
                put(MediaStore.MediaColumns.DATA, file.absolutePath)
            }
        }

        return Utils.getApp().contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            ?: throw IOException("Failed to create file URI")
    }

    private fun generateDefaultFileName(): String {
        val timestamp = TimeUtils.getNowString("yyyyMMdd_HHmmss")
        return "thermal_export_$timestamp.xlsx"
    }

    private fun getTempValues(thermalEntity: ThermalEntity, tempUnit: Int): TempValues {
        return when (tempUnit) {
            1 -> TempValues(thermalEntity.minTemp, thermalEntity.maxTemp, thermalEntity.avgTemp) // Celsius
            2 -> TempValues( // Fahrenheit
                UnitTools.celsiusToFahrenheit(thermalEntity.minTemp),
                UnitTools.celsiusToFahrenheit(thermalEntity.maxTemp),
                UnitTools.celsiusToFahrenheit(thermalEntity.avgTemp)
            )
            else -> TempValues( // Kelvin
                UnitTools.celsiusToKelvin(thermalEntity.minTemp),
                UnitTools.celsiusToKelvin(thermalEntity.maxTemp),
                UnitTools.celsiusToKelvin(thermalEntity.avgTemp)
            )
        }
    }

    private data class TempValues(
        val minTemp: Double,
        val maxTemp: Double,
        val avgTemp: Double
    )
}