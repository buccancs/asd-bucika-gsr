// package com.topdon.tc001
//
// import com.topdon.tc001.jni.algorithm.so.Algorithm
//
// /**
//  * Amplify utility for TC001 thermal processing
//  * Converted from Java to Kotlin
//  */
// object AmplifyUtil {
//     init {
//         System.loadLibrary("opencv_java4")
//         System.loadLibrary("SRImage")
//     }
//
//     fun testOpencv(strFilePath: String, bytes: ByteArray): ByteArray {
//         return Algorithm.adjustPhoto(strFilePath, bytes)
//     }
// }