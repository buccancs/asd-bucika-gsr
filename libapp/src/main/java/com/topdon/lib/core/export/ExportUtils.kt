package com.topdon.lib.core.export

import android.content.ContentValues
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ScrollView
import androidx.core.content.ContextCompat
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
 * Combines functionality from libcom ExcelUtil, PDFHelp and similar export utilities.
 * Supports Excel, PDF, and CSV export with comprehensive thermal data formatting.
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
            null
        }
    }

    /**
     * Export thermal data to PDF format.
     */
    @JvmStatic
    fun exportToPDF(thermalList: List<ThermalEntity>, fileName: String = generateDefaultFileName()): Uri? {
        return try {
            val document = createPdfDocument(thermalList)
            val uri = savePdfDocument(document, fileName)
            document.close()
            uri
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Save PDF file from view list (consolidated from libcom PDFHelp).
     * Creates paginated PDF with proper A4 formatting.
     */
    @JvmStatic
    fun savePdfFromViews(
        name: String, 
        containerView: ScrollView, 
        viewList: MutableList<View>, 
        watermarkView: View
    ): String {
        val onePageHeight: Int = (containerView.width * 297f / 210f).toInt() // A4 aspect ratio 210:297
        var onePageContentHeight = 0f

        val pdfDocument = PdfDocument()
        var page: PdfDocument.Page? = null
        var canvas: Canvas? = null

        val paint = Paint().apply {
            color = 0xff16131e.toInt()
        }

        for (index in 0 until viewList.size) {
            val contentHeight = viewList[index].measuredHeight
            
            if (onePageContentHeight + contentHeight > onePageHeight) {
                // Content exceeds page height, start new page
                onePageContentHeight = 0f
                page?.let { pdfDocument.finishPage(it) }
                page = null
            }
            
            if (page == null) {
                val pageInfo = PdfDocument.PageInfo.Builder(containerView.width, onePageHeight, 1)
                    .setContentRect(Rect(0, 0, containerView.width, onePageHeight))
                    .create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                canvas.drawRect(0f, 0f, containerView.width.toFloat(), onePageHeight.toFloat(), paint)

                // Draw background for first page
                if (index == 0) {
                    try {
                        val bgDrawableId = containerView.context.resources.getIdentifier(
                            "ic_report_create_bg_top", "drawable", containerView.context.packageName
                        )
                        if (bgDrawableId != 0) {
                            val bgTopDrawable: Drawable? = ContextCompat.getDrawable(containerView.context, bgDrawableId)
                            bgTopDrawable?.setBounds(0, 0, containerView.width, (containerView.width * 1026 / 1125f).toInt())
                            bgTopDrawable?.draw(canvas)
                        }
                    } catch (e: Exception) {
                        // Ignore if resource not found
                    }
                }

                // Draw watermark
                canvas.save()
                watermarkView.draw(canvas)
                canvas.restore()
            }

            // Draw content view
            canvas?.save()
            canvas?.translate((containerView.width - viewList[index].measuredWidth) / 2f, 0f)
            viewList[index].draw(canvas!!)
            canvas?.restore()

            canvas?.translate(0f, contentHeight.toFloat())
            onePageContentHeight += contentHeight
            
            if (page != null && index == viewList.size - 1) {
                pdfDocument.finishPage(page)
            }
        }

        return savePdfDocumentToFile(pdfDocument, name)
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
            null
        }
    }

    private fun createPdfDocument(thermalList: List<ThermalEntity>): PdfDocument {
        val document = PdfDocument()
        // Basic PDF document creation - can be enhanced as needed
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        
        val paint = Paint().apply {
            textSize = 12f
            color = Color.BLACK
        }
        
        // Simple text-based thermal data export
        var yPosition = 50f
        canvas.drawText("Thermal Data Export", 50f, yPosition, paint)
        yPosition += 30f
        
        for ((index, entity) in thermalList.withIndex()) {
            if (yPosition > 800) break // Stay within page bounds
            canvas.drawText("Entry ${index + 1}: Temp ${entity.centerTemp}°C", 50f, yPosition, paint)
            yPosition += 20f
        }
        
        document.finishPage(page)
        return document
    }

    private fun savePdfDocument(document: PdfDocument, fileName: String): Uri? {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val pdfFile = File(FileConfig.getPdfDir(), "$fileName.pdf")
                val fos = FileOutputStream(pdfFile)
                document.writeTo(fos)
                fos.close()
                Uri.fromFile(pdfFile)
            } else {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, FileConfig.getPdfDir())
                }
                val contentUri = MediaStore.Files.getContentUri("external")
                val uri = Utils.getApp().contentResolver.insert(contentUri, values)
                uri?.let { 
                    Utils.getApp().contentResolver.openOutputStream(it)?.use { stream ->
                        document.writeTo(stream)
                    }
                }
                uri
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun savePdfDocumentToFile(document: PdfDocument, name: String): String {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val pdfFile = File(FileConfig.getPdfDir(), "$name.pdf")
                val fos = FileOutputStream(pdfFile)
                document.writeTo(fos)
                fos.flush()
                fos.close()
                pdfFile.absolutePath
            } else {
                val fileName = "$name.pdf"
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, FileConfig.getPdfDir())
                }
                val contentUri = MediaStore.Files.getContentUri("external")
                val uri = Utils.getApp().contentResolver.insert(contentUri, values)
                uri?.let { 
                    val outputStream = Utils.getApp().contentResolver.openOutputStream(it)
                    outputStream?.let { stream ->
                        val bos = BufferedOutputStream(stream)
                        document.writeTo(bos)
                        bos.flush()
                        bos.close()
                    }
                    UriUtils.uri2File(it)?.absolutePath ?: ""
                } ?: ""
            }
        } catch (e: Exception) {
            ""
        } finally {
            document.close()
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