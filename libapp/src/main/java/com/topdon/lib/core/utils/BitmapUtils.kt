package com.topdon.lib.core.utils

import android.content.Context
import android.graphics.*
import android.media.MediaScannerConnection
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.listener.BitmapViewListener
import java.io.*

/**
 * Bitmap utility functions converted from Java to Kotlin
 * Enhanced with null safety and modern Kotlin idioms
 */
object BitmapUtils {

    fun mirror(rawBitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
    }

    fun rotateBitmap(bm: Bitmap, degree: Int): Bitmap {
        var returnBm: Bitmap? = null

        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            // Handle out of memory
        }
        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm.recycle()
        }
        return returnBm
    }

    /**
     * 将bitmap转换成bytes
     */
    fun bitmapToBytes(bitmap: Bitmap?, quality: Int): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val size = bitmap.width * bitmap.height * 4
        val out = ByteArrayOutputStream(size)
        return try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()
            out.toByteArray()
        } catch (e: IOException) {
            null
        }
    }

    /**
     * 将图片保存到磁盘中
     *
     * @param bitmap
     * @param file   图片保存目录——不包含图片名
     * @param path   图片保存文件路径——包含图片名
     * @return
     */
    fun saveBitmap(bitmap: Bitmap, file: File, path: File): Boolean {
        var success = false
        val bytes = bitmapToBytes(bitmap, 100) ?: return false
        var out: OutputStream? = null
        try {
            if (!file.exists() && file.isDirectory) {
                file.mkdirs()
            }
            out = FileOutputStream(path)
            out.write(bytes)
            out.flush()
            success = true
        } catch (e: Exception) {
            // Handle exception
        } finally {
            out?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    // Handle close exception
                }
            }
        }
        return success
    }

    /**
     * 高级图片质量压缩
     *
     * @param bitmap 位图
     * @param width  压缩后的宽度，单位像素
     */
    fun imageZoom(bitmap: Bitmap, width: Double): Bitmap {
        // 将bitmap放至数组中，意在获得bitmap的大小（与实际读取的原文件要大）
        val baos = ByteArrayOutputStream()
        // 格式、质量、输出流
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val b = baos.toByteArray()
        val newBitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
        // 获取bitmap大小 是允许最大大小的多少倍
        return newBitmap?.let { 
            scaleWithWH(it, width, width * it.height / it.width)
        } ?: bitmap
    }

    /**
     * 图片缩放
     * @param bitmap 位图
     * @param w 新的宽度
     * @param h 新的高度
     * @return Bitmap
     */
    fun scaleWithWH(bitmap: Bitmap?, w: Double, h: Double): Bitmap? {
        return if (w == 0.0 || h == 0.0 || bitmap == null) {
            bitmap
        } else {
            val width = bitmap.width
            val height = bitmap.height

            val matrix = Matrix()
            val scaleWidth = (w / width).toFloat()
            val scaleHeight = (h / height).toFloat()

            matrix.postScale(scaleWidth, scaleHeight)
            Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        }
    }

    /**
     * bitmap保存到指定路径
     *
     * @param file 图片的绝对路径
     * @param bmp 位图
     * @return success
     */
    fun saveFile(file: String, bmp: Bitmap?): Boolean {
        if (TextUtils.isEmpty(file) || bmp == null) return false

        val f = File(file)
        if (f.exists()) {
            f.delete()
        } else {
            val p = f.parentFile
            if (p != null && !p.exists()) {
                p.mkdirs()
            }
        }
        return try {
            FileOutputStream(f).use { out ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     *
     * @param backBitmap  在底部的位图
     * @param frontBitmap 盖在上面的位图
     * @return
     */
    fun mergeBitmap(backBitmap: Bitmap?, frontBitmap: Bitmap?, leftFront: Int, topFront: Int): Bitmap? {
        if (backBitmap == null || backBitmap.isRecycled || frontBitmap == null || frontBitmap.isRecycled) {
            return null
        }
        val bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(backBitmap, 0f, 0f, null)
        canvas.drawBitmap(frontBitmap, leftFront.toFloat(), topFront.toFloat(), null)
        return bitmap
    }

    fun mergeBitmapAlpha(backBitmap: Bitmap?, frontBitmap: Bitmap?, paint: Paint, leftFront: Int, topFront: Int): Bitmap? {
        if (backBitmap == null || backBitmap.isRecycled || frontBitmap == null || frontBitmap.isRecycled) {
            return null
        }
        val bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(backBitmap, 0f, 0f, null)
        canvas.drawBitmap(frontBitmap, leftFront.toFloat(), topFront.toFloat(), paint)
        return bitmap
    }

    fun mergeBitmapByView(backBitmap: Bitmap?, frontBitmap: Bitmap?, view: BitmapViewListener): Bitmap? {
        if (backBitmap == null || backBitmap.isRecycled || frontBitmap == null || frontBitmap.isRecycled) {
            return null
        }
        val paint = Paint()
        paint.alpha = (view.viewAlpha * 255).toInt()
        val bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(backBitmap, 0f, 0f, null)
        
        var scaledFrontBitmap = frontBitmap
        if (view.viewScale != 1f) {
            scaledFrontBitmap = scaleWithWH(frontBitmap, view.viewWidth.toDouble(), view.viewHeight.toDouble())
        }
        canvas.drawBitmap(scaledFrontBitmap!!, view.viewX, view.viewY, paint)
        scaledFrontBitmap.recycle()
        return bitmap
    }

    fun mergeBitmapByViewNonNull(backBitmap: Bitmap, frontBitmap: Bitmap?, view: BitmapViewListener): Bitmap {
        if (frontBitmap == null || frontBitmap.isRecycled) {
            return backBitmap
        }

        val bitmap = if (backBitmap.isRecycled) {
            Bitmap.createBitmap(backBitmap.width, backBitmap.height, backBitmap.config)
        } else {
            backBitmap
        }
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.alpha = (view.viewAlpha * 255).toInt()

        var scaledFrontBitmap = frontBitmap
        if (view.viewScale != 1f) {
            scaledFrontBitmap = scaleWithWH(frontBitmap, view.viewWidth.toDouble(), view.viewHeight.toDouble())
        }
        canvas.drawBitmap(scaledFrontBitmap!!, view.viewX, view.viewY, paint)
        scaledFrontBitmap.recycle()
        return bitmap
    }

    fun mergeBitmapByView(frontBitmap: Bitmap?, view: BitmapViewListener, canvas: Canvas) {
        if (frontBitmap == null || frontBitmap.isRecycled) {
            return
        }
        val paint = Paint()
        paint.alpha = (view.viewAlpha * 255).toInt()
        
        var scaledFrontBitmap = frontBitmap
        if (view.viewScale != 1f) {
            scaledFrontBitmap = scaleWithWH(frontBitmap, view.viewWidth.toDouble(), view.viewHeight.toDouble())
        }
        canvas.drawBitmap(scaledFrontBitmap!!, view.viewX, view.viewY, paint)
    }

    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     * @param bytes  在底部的位图
     * @param bytes2 盖在上面的位图
     */
    @Deprecated("Use saveRawFile instead")
    fun savaRawFile(bytes: ByteArray, bytes2: ByteArray) {
        try {
            val path = File("/sdcard")
            if (!path.exists() && path.isDirectory) {
                path.mkdirs()
            }
            val file = File("/sdcard/", "xxx.raw")
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
                fos.write(bytes2)
            }
        } catch (e: FileNotFoundException) {
            // Handle exception
        } catch (e: IOException) {
            // Handle exception
        }
    }

    /**
     * 添加水印
     * @param bmp
     * @param title
     * @param address
     * @param time
     * @param seekBarWidth : 右边伪彩控件的宽度，防止内容和控件重叠
     * @return
     */
    fun drawCenterLable(bmp: Bitmap, title: String?, address: String?, time: String?, seekBarWidth: Int): Bitmap {
        // 创建一样大小的图片
        val newBmp = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        // 创建画布
        val canvas = Canvas(newBmp)
        canvas.drawBitmap(bmp, 0f, 0f, null) // 绘制原始图片
        canvas.save()
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE // 白色半透明
        paint.textSize = SizeUtils.sp2px(12f).toFloat()
        paint.isDither = true
        paint.isFilterBitmap = true
        val rectText = Rect() // 得到text占用宽高， 单位：像素
        paint.getTextBounds("占位高度文本", 0, "占位高度文本".length, rectText)
        var beginX = SizeUtils.dp2px(10f).toDouble() // 45度角度值是1.414
        var beginY = bmp.height - SizeUtils.dp2px(10f).toDouble()
        
        if (!TextUtils.isEmpty(time)) {
            beginY = beginY - (rectText.bottom - rectText.top)
            canvas.drawText(time!!, beginX.toFloat(), beginY.toFloat(), paint)
            beginY -= SizeUtils.dp2px(6f)
        }
        
        val lineWidth = bmp.width - SizeUtils.dp2px(20f) - seekBarWidth // 一行的可显示内容宽度
        
        if (!TextUtils.isEmpty(address)) {
            val textHeight = rectText.bottom - rectText.top
            paint.getTextBounds(address, 0, address!!.length, rectText)
            if (rectText.width() > lineWidth) {
                // 字符太长，进行换行处理
                val staticLayout = StaticLayout(
                    address, paint, lineWidth,
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
                )
                beginY = beginY - (textHeight + SizeUtils.dp2px(1f)) * staticLayout.lineCount
                canvas.save()
                canvas.translate(beginX.toFloat(), beginY.toFloat() - textHeight)
                staticLayout.draw(canvas)
                canvas.restore()
            } else {
                beginY = beginY - textHeight
                canvas.drawText(address, beginX.toFloat(), beginY.toFloat(), paint)
            }
            beginY -= SizeUtils.dp2px(6f)
        }
        
        if (!TextUtils.isEmpty(title)) {
            val textHeight = rectText.bottom - rectText.top
            paint.getTextBounds(title, 0, title!!.length, rectText)
            if (rectText.width() > lineWidth) {
                // 字符太长，进行换行处理
                val staticLayout = StaticLayout(
                    title, paint, lineWidth,
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
                )
                beginY = beginY - textHeight * staticLayout.lineCount
                canvas.save()
                canvas.translate(beginX.toFloat(), beginY.toFloat() - textHeight)
                staticLayout.draw(canvas)
                canvas.restore()
            } else {
                beginY = beginY - textHeight
                canvas.drawText(title, beginX.toFloat(), beginY.toFloat(), paint)
            }
            beginY -= SizeUtils.dp2px(6f)
        }
        canvas.restore()
        return newBmp
    }

    // Additional methods consolidated from libir-demo BitmapUtils

    /**
     * Save bitmap to gallery
     * @param context Android context
     * @param bmp Bitmap to save
     * @param picName Picture name without extension
     * @return File object of saved image
     */
    fun saveBmp2Gallery(context: Context, bmp: Bitmap, picName: String): File? {
        // Storage directory, user can customize
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .toString() + File.separator + "infisense"
        val galleryPath = File(path)
        if (!galleryPath.exists()) {
            galleryPath.mkdir()
        }
        
        var file: File? = null
        try {
            // Create file with picName
            file = File(galleryPath, "$picName.jpg")
            
            // Create output stream and save
            FileOutputStream(file).use { outStream ->
                // Compress bitmap to JPEG format with 90% quality
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
                outStream.flush()
            }
            
            // Notify media scanner
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null
            )
                
        } catch (e: Exception) {
            e.printStackTrace()
            file = null
        }
        return file
    }

    /**
     * Save raw file data (from libir-demo)
     */
    fun saveRawFile(bytes: ByteArray, bytes2: ByteArray?) {
        try {
            val fileName = "raw_${System.currentTimeMillis()}.dat"
            val file = File(FileConfig.getExternalCachePath(), fileName)
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
                bytes2?.let { fos.write(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Save IR file data
     */
    fun saveIRFile(bytes: ByteArray) {
        try {
            val fileName = "ir_${System.currentTimeMillis()}.dat"
            val file = File(FileConfig.getExternalCachePath(), fileName)
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Save temperature file data
     */
    fun saveTempFile(bytes: ByteArray) {
        try {
            val fileName = "temp_${System.currentTimeMillis()}.dat"
            val file = File(FileConfig.getExternalCachePath(), fileName)
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Save byte array to file with custom title
     */
    fun saveByteFile(bytes: ByteArray, fileTitle: String) {
        try {
            val fileName = "${fileTitle}_${System.currentTimeMillis()}.dat"
            val file = File(FileConfig.getExternalCachePath(), fileName)
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Save short array to file with custom title
     */
    fun saveShortFile(shorts: ShortArray, fileTitle: String) {
        try {
            val fileName = "${fileTitle}_${System.currentTimeMillis()}.dat"
            val file = File(FileConfig.getExternalCachePath(), fileName)
            FileOutputStream(file).use { fos ->
                fos.write(toByteArray(shorts))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Convert short array to byte array
     * @param src source short array
     * @return byte array
     */
    fun toByteArray(src: ShortArray): ByteArray {
        val count = src.size
        val dest = ByteArray(count shl 1)
        for (i in 0 until count) {
            dest[i * 2] = ((src[i].toInt() shr 8) and 0xFF).toByte()
            dest[i * 2 + 1] = (src[i].toInt() and 0xFF).toByte()
        }
        return dest
    }

    /**
     * Convert byte array to short array
     * @param src source byte array
     * @return short array
     */
    fun toShortArray(src: ByteArray): ShortArray {
        val count = src.size shr 1
        val dest = ShortArray(count)
        for (i in 0 until count) {
            dest[i] = (((src[i * 2].toInt() and 0xFF) shl 8) or (src[i * 2 + 1].toInt() and 0xFF)).toShort()
        }
        return dest
    }
}