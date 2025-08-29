package com.topdon.lib.core.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * Consolidated widget utility functions.
 * Combines functionality from libui seekbar utils, CenterItemUtils, and other widget helpers.
 */
object WidgetUtils {
    
    private const val TAG = "WidgetUtils"
    
    // === Logging utilities ===
    
    @JvmStatic
    fun log(message: String) {
        Log.d(TAG, message)
    }
    
    @JvmStatic
    fun log(vararg messages: Any) {
        val combined = messages.joinToString(" ")
        Log.d(TAG, combined)
    }
    
    // === Bitmap and drawable utilities ===
    
    @JvmStatic
    fun drawableToBitmap(context: Context?, width: Int, height: Int, drawableId: Int): Bitmap? {
        if (context == null || width <= 0 || height <= 0 || drawableId == 0) return null
        
        val drawable = ContextCompat.getDrawable(context, drawableId)
        return drawable?.let { drawableToBitmap(width, height, it) }
    }
    
    @JvmStatic
    fun drawableToBitmap(width: Int, height: Int, drawable: Drawable): Bitmap? {
        return try {
            when (drawable) {
                is BitmapDrawable -> {
                    val bitmap = drawable.bitmap
                    if (bitmap != null && bitmap.height > 0) {
                        val matrix = Matrix()
                        val scaleWidth = width * 1.0f / bitmap.width
                        val scaleHeight = height * 1.0f / bitmap.height
                        matrix.postScale(scaleWidth, scaleHeight)
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    } else {
                        createBitmapFromDrawable(width, height, drawable)
                    }
                }
                else -> createBitmapFromDrawable(width, height, drawable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createBitmapFromDrawable(width: Int, height: Int, drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    
    @JvmStatic
    fun verifyBitmap(bitmap: Bitmap?): Boolean {
        return bitmap != null && !bitmap.isRecycled && bitmap.width > 0 && bitmap.height > 0
    }
    
    // === Nine-patch drawing ===
    
    @JvmStatic
    fun drawNinePatch(canvas: Canvas, bitmap: Bitmap, rect: Rect) {
        val chunk = bitmap.ninePatchChunk
        if (NinePatch.isNinePatchChunk(chunk)) {
            val patch = NinePatch(bitmap, chunk, null)
            patch.draw(canvas, rect)
        }
    }
    
    @JvmStatic
    fun drawBitmap(canvas: Canvas, paint: Paint, bitmap: Bitmap, rect: Rect) {
        try {
            val chunk = bitmap.ninePatchChunk
            if (NinePatch.isNinePatchChunk(chunk)) {
                drawNinePatch(canvas, bitmap, rect)
                return
            }
        } catch (e: Exception) {
            // Fallback to regular bitmap drawing
        }
        canvas.drawBitmap(bitmap, rect.left.toFloat(), rect.top.toFloat(), paint)
    }
    
    // === Measurement utilities ===
    
    @JvmStatic
    fun measureText(text: String, textSize: Float): Rect {
        val paint = Paint()
        val textRect = Rect()
        paint.textSize = textSize
        paint.getTextBounds(text, 0, text.length, textRect)
        return textRect
    }
    
    @JvmStatic
    fun dp2px(context: Context?, dpValue: Float): Int {
        if (context == null || compareFloat(0f, dpValue) == 0) return 0
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
    
    // === Float comparison utilities ===
    
    @JvmStatic
    fun compareFloat(a: Float, b: Float): Int {
        val ta = (a * 1000000).toInt()
        val tb = (b * 1000000).toInt()
        return when {
            ta > tb -> 1
            ta < tb -> -1
            else -> 0
        }
    }
    
    @JvmStatic
    fun compareFloat(a: Float, b: Float, degree: Int): Int {
        val threshold = Math.pow(0.1, degree.toDouble())
        return when {
            Math.abs(a - b) < threshold -> 0
            a < b -> -1
            else -> 1
        }
    }
    
    @JvmStatic
    fun parseFloat(s: String): Float {
        return try {
            s.toFloat()
        } catch (e: NumberFormatException) {
            0f
        }
    }
    
    // === Color utilities ===
    
    @JvmStatic
    fun getColor(context: Context?, colorId: Int): Int {
        return context?.let { 
            ContextCompat.getColor(it.applicationContext, colorId)
        } ?: Color.WHITE
    }
    
    // === Center item selection utilities (consolidated from libui CenterItemUtils) ===
    
    /**
     * Find the item with minimum difference from center position.
     */
    @JvmStatic
    fun getMinDifferItem(itemHeights: List<CenterViewItem>): CenterViewItem {
        if (itemHeights.isEmpty()) {
            throw IllegalArgumentException("Item list cannot be empty")
        }
        
        return itemHeights.minByOrNull { it.differ } ?: itemHeights[0]
    }
    
    /**
     * Data class representing an item position and its difference from center.
     */
    data class CenterViewItem(
        val position: Int,   // Current item index
        val differ: Int      // Difference from center position
    )
}