package com.topdon.lib.core.so

/**
 * Native algorithm interface for thermal image processing
 * Converted from Java to Kotlin with enhanced documentation
 */
object Algorithm {
    
    /**
     * Adjust photo with native processing
     * @param strFilePath File path for processing
     * @param bytes Input byte array
     * @return Processed byte array
     */
    external fun adjustPhoto(strFilePath: String, bytes: ByteArray): ByteArray
    
    /**
     * Maximum temperature processing with location tracking
     * @param imgBytes Image byte data
     * @param tempByte Temperature byte data  
     * @param width Image width
     * @param height Image height
     * @return Processed result
     */
    external fun maxTempL(imgBytes: ByteArray, tempByte: ByteArray, width: Int, height: Int): ByteArray
    
    /**
     * Low temperature tracking algorithm
     * @param imgBytes Image byte data
     * @param tempByte Temperature byte data
     * @param width Image width
     * @param height Image height
     * @return Processed result
     */
    external fun lowTemTrack(imgBytes: ByteArray, tempByte: ByteArray, width: Int, height: Int): ByteArray
}