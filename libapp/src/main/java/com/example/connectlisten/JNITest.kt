package com.example.connectlisten

import com.topdon.lib.core.so.Algorithm

/**
 * JNI test interface for native library functions
 * Converted from Java to Kotlin with enhanced type safety
 */
object JNITest {
    
    init {
        System.loadLibrary("opencv_java4")
        // System.loadLibrary("SRImage")
        // System.loadLibrary("minMaxTemperatureDetect")
    }

    fun maxTempL(imgBytes: ByteArray, tempByte: ByteArray, width: Int, height: Int): ByteArray {
        return Algorithm.maxTempL(imgBytes, tempByte, width, height)
    }
    
    fun lowTemTrack(imgBytes: ByteArray, tempByte: ByteArray, width: Int, height: Int): ByteArray {
        return Algorithm.lowTemTrack(imgBytes, tempByte, width, height)
    }
}