package com.topdon.lib.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

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
public class SeekBarUtils {

    private static final String TAG = "RangeSeekBar";

    /**
     * dp 转 px
     *
     * @param context 上下文
     * @param dpVal   dp 值
     * @return px 值
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) (dpVal * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * px 转 dp
     *
     * @param context 上下文
     * @param pxVal   px 值
     * @return dp 值
     */
    public static float px2dp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().density);
    }

    /**
     * sp 转 px
     *
     * @param context 上下文
     * @param spVal   sp 值
     * @return px 值
     */
    public static int sp2px(Context context, float spVal) {
        return (int) (spVal * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * px 转 sp
     *
     * @param context 上下文
     * @param pxVal   px 值
     * @return sp 值
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int getColor(Context context, @ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    /**
     * Drawable to Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth <= 0 ? 1 : intrinsicWidth,
                intrinsicHeight <= 0 ? 1 : intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap to Drawable
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static BitmapDrawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap scaleImage(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
    }

    /**
     * 获得NinePatch
     * 要先把图片放在drawable-nodpi 文件夹下
     *
     * @param context
     * @param resId
     * @return
     */
    public static NinePatch getNinePatch(Context context, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        return new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
    }

    /**
     * 转换为.9格式的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static BitmapDrawable createNinePatchDrawable(Context context, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Android 10.0+ 适配
            return new BitmapDrawable(context.getResources(), bitmap);
        } else {
            NinePatch np = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
            return new BitmapDrawable(context.getResources(), np.getBitmap());
        }
    }

    public static void log(Object o) {
    }

    /**
     * 判断颜色的深浅，深色返回true，浅色返回false
     *
     * @param color
     * @return
     */
    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }

    /**
     * 测量文本宽度
     *
     * @param paint
     * @param text
     * @return
     */
    public static float measureText(Paint paint, String text) {
        return paint.measureText(text);
    }

    /**
     * 测量文本高度
     *
     * @param paint
     * @return
     */
    public static float measureTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    /**
     * 根据文本内容获取文本区域
     *
     * @param paint
     * @param text
     * @param textBound
     */
    public static void measureTextBound(Paint paint, String text, Rect textBound) {
        paint.getTextBounds(text, 0, text.length(), textBound);
    }

}