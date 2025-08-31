package com.topdon.lib.core.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.WindowManager
import java.lang.reflect.Method

/**
 * Consolidated screen utility functions.
 * Enhanced with functionality from libir ScreenUtils while maintaining existing API compatibility.
 */
object ScreenUtil {
    @JvmStatic
    fun getScreenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    @JvmStatic
    fun getScreenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels

    @JvmStatic
    fun isPortrait(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    @JvmStatic
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    // === Enhanced functionality from libir ScreenUtils consolidation ===

    /**
     * Get status bar height in pixels
     */
    @JvmStatic
    fun getStatusHeight(context: Context): Int {
        var statusHeight = -1
        try {
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val obj = clazz.newInstance()
            val height = Integer.parseInt(clazz.getField("status_bar_height")
                .get(obj).toString())
            statusHeight = context.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusHeight
    }

    /**
     * Take screenshot including status bar
     */
    @JvmStatic
    fun snapShotWithStatusBar(activity: Activity): Bitmap? {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val width = getScreenWidth(activity)
        val height = getScreenHeight(activity)
        val bp = Bitmap.createBitmap(bmp, 0, 0, width, height)
        view.destroyDrawingCache()
        return bp
    }

    /**
     * Take screenshot excluding status bar
     */
    @JvmStatic
    fun snapShotWithoutStatusBar(activity: Activity): Bitmap? {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top

        val width = getScreenWidth(activity)
        val height = getScreenHeight(activity)
        val bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        return bp
    }

    /**
     * Get device DPI
     */
    @JvmStatic
    fun getScreenDensityDpi(context: Context): Int {
        return context.resources.displayMetrics.densityDpi
    }

    /**
     * Get screen density scale
     */
    @JvmStatic
    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    /**
     * Convert dp to pixels
     */
    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = getScreenDensity(context)
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * Get navigation bar height
     */
    @JvmStatic
    fun getBottomStatusHeight(context: Context): Int {
        val totalHeight = getDpi(context)
        val contentHeight = getScreenHeight(context)
        return totalHeight - contentHeight
    }

    /**
     * Get real screen height including virtual navigation
     */
    @JvmStatic
    fun getDpi(context: Context): Int {
        var dpi = 0
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        try {
            val c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, displayMetrics)
            dpi = displayMetrics.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dpi
    }
}