package com.infisense.usbir.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

/**
 * Backward compatibility wrapper for BitmapUtils functionality.
 * All methods delegate to the shared implementation in com.topdon.lib.core.utils.BitmapUtils.
 * 
 * @deprecated Use com.topdon.lib.core.utils.BitmapUtils directly for new code.
 */
public class BitmapUtils {
    
    public static Bitmap mirror(Bitmap rawBitmap) {
        return com.topdon.lib.core.utils.BitmapUtils.mirror(rawBitmap);
    }

    public static Bitmap rotateBitmap(Bitmap bm, int degree) {
        return com.topdon.lib.core.utils.BitmapUtils.rotateBitmap(bm, degree);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, int quality) {
        return com.topdon.lib.core.utils.BitmapUtils.bitmapToBytes(bitmap, quality);
    }

    public static boolean saveBitmap(Bitmap bitmap, File file, File path) {
        return com.topdon.lib.core.utils.BitmapUtils.saveBitmap(bitmap, file, path);
    }

    public static Bitmap imageZoom(Bitmap bitmap, double width) {
        return com.topdon.lib.core.utils.BitmapUtils.imageZoom(bitmap, width);
    }

    public static Bitmap scaleWithWH(Bitmap bitmap, double w, double h) {
        return com.topdon.lib.core.utils.BitmapUtils.scaleWithWH(bitmap, w, h);
    }

    public static boolean saveFile(String file, Bitmap bmp) {
        return com.topdon.lib.core.utils.BitmapUtils.saveFile(file, bmp);
    }

    public static File saveBmp2Gallery(Context context, Bitmap bmp, String picName) {
        return com.topdon.lib.core.utils.BitmapUtils.saveBmp2Gallery(context, bmp, picName);
    }

    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap, int leftFront, int topFront) {
        return com.topdon.lib.core.utils.BitmapUtils.mergeBitmap(backBitmap, frontBitmap, leftFront, topFront);
    }

    public static void saveRawFile(byte[] bytes, byte[] bytes2) {
        com.topdon.lib.core.utils.BitmapUtils.saveRawFile(bytes, bytes2);
    }

    public static void saveIRFile(byte[] bytes) {
        com.topdon.lib.core.utils.BitmapUtils.saveIRFile(bytes);
    }

    public static void saveTempFile(byte[] bytes) {
        com.topdon.lib.core.utils.BitmapUtils.saveTempFile(bytes);
    }

    public static void saveByteFile(byte[] bytes, String fileTitle) {
        com.topdon.lib.core.utils.BitmapUtils.saveByteFile(bytes, fileTitle);
    }

    public static void saveShortFile(short[] bytes, String fileTitle) {
        com.topdon.lib.core.utils.BitmapUtils.saveShortFile(bytes, fileTitle);
    }

    public static byte[] toByteArray(short[] src) {
        return com.topdon.lib.core.utils.BitmapUtils.toByteArray(src);
    }

    public static short[] toShortArray(byte[] src) {
        return com.topdon.lib.core.utils.BitmapUtils.toShortArray(src);
    }
}