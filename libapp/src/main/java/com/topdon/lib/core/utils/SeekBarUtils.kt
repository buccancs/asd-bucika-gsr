package com.topdon.lib.core.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Shared utility class for SeekBar/RangeSeekBar components
 * Consolidated from duplicate implementations across modules
 * ================================================
 * 作    者：JayGoo
 * 版    本：
 * 创建日期：2018/5/8
 * 描    述: Shared SeekBar utilities
 * ================================================
 */
object SeekBarUtils {
    
    private const val TAG = "RangeSeekBar"

    /**
     * dp 转 px
     *
     * @param context 上下文
     * @param dpVal   dp 值
     * @return px 值
     */
    fun dp2px(context: Context, dpVal: Float): Int {
        return (dpVal * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    /**
     * px 转 dp
     *
     * @param context 上下文
     * @param pxVal   px 值
     * @return dp 值
     */
    fun px2dp(context: Context, pxVal: Float): Float {
        return pxVal / context.resources.displayMetrics.density
    }

    /**
     * sp 转 px
     *
     * @param context 上下文
     * @param spVal   sp 值
     * @return px 值
     */
    fun sp2px(context: Context, spVal: Float): Int {
        return (spVal * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    /**
     * px 转 sp
     *
     * @param context 上下文
     * @param pxVal   px 值
     * @return sp 值
     */
    fun px2sp(context: Context, pxVal: Float): Float {
        return pxVal / context.resources.displayMetrics.scaledDensity
    }

    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    /**
     * Drawable to Bitmap
     *
     * @param drawable
     * @return
     */
    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(
            if (intrinsicWidth <= 0) 1 else intrinsicWidth,
            if (intrinsicHeight <= 0) 1 else intrinsicHeight, 
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Bitmap to Drawable
     *
     * @param context
     * @param bitmap
     * @return
     */
    fun bitmapToDrawable(context: Context, bitmap: Bitmap): BitmapDrawable {
        return BitmapDrawable(context.resources, bitmap)
    }

    fun scaleImage(bitmap: Bitmap, scale: Float): Bitmap {
        val matrix = Matrix().apply {
            postScale(scale, scale)
        }
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }

    /**
     * 获得NinePatch
     * 要先把图片放在drawable-nodpi 文件夹下
     *
     * @param context
     * @param resId
     * @return
     */
    fun getNinePatch(context: Context, resId: Int): NinePatch {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        return NinePatch(bitmap, bitmap.ninePatchChunk, null)
    }

    /**
     * 转换为.9格式的图片
     *
     * @param context
     * @param resId
     * @return
     */
    fun createNinePatchDrawable(context: Context, resId: Int): BitmapDrawable {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10.0+ 适配
            BitmapDrawable(context.resources, bitmap)
        } else {
            val np = NinePatch(bitmap, bitmap.ninePatchChunk, null)
            BitmapDrawable(context.resources, np.bitmap)
        }
    }

    fun log(o: Any?) {
        // Logging removed
    }

    /**
     * 判断颜色的深浅，深色返回true，浅色返回false
     *
     * @param color
     * @return
     */
    fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    /**
     * 测量文本宽度
     *
     * @param paint
     * @param text
     * @return
     */
    fun measureText(paint: Paint, text: String): Float {
        return paint.measureText(text)
    }

    /**
     * 测量文本高度
     *
     * @param paint
     * @return
     */
    fun measureTextHeight(paint: Paint): Float {
        val fontMetrics = paint.fontMetrics
        return fontMetrics.descent - fontMetrics.ascent
    }

    /**
     * 根据文本内容获取文本区域
     *
     * @param paint
     * @param text
     * @param textBound
     */
    fun measureTextBound(paint: Paint, text: String, textBound: Rect) {
        paint.getTextBounds(text, 0, text.length, textBound)
    }
}