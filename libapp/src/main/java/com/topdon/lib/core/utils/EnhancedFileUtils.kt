package com.topdon.lib.core.utils

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.*
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enhanced File Utilities consolidating libir FileUtil functionality
 * Provides comprehensive file operations with enhanced error handling and coroutine support
 */
object EnhancedFileUtils {

    private const val TAG = "EnhancedFileUtils"
    private const val BUFFER_SIZE = 8192

    /**
     * Get application version name
     */
    @JvmStatic
    fun getVersionName(context: Context): String {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            info.versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }

    /**
     * Get application version code
     */
    @JvmStatic
    fun getVersionCode(context: Context): Long {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                info.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            -1L
        }
    }

    /**
     * Check if external storage is available
     */
    @JvmStatic
    fun isExternalStorageAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * Get external storage directory with enhanced path handling
     */
    @JvmStatic
    fun getExternalStorageDirectory(): File? {
        return if (isExternalStorageAvailable()) {
            Environment.getExternalStorageDirectory()
        } else null
    }

    /**
     * Create directory with parent directories if needed
     */
    @JvmStatic
    fun createDirectory(path: String): Boolean {
        return try {
            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
            } else true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Copy file from source to destination with enhanced error handling
     */
    @JvmStatic
    suspend fun copyFile(sourcePath: String, destPath: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val sourceFile = File(sourcePath)
            val destFile = File(destPath)
            
            if (!sourceFile.exists()) return@withContext false
            
            // Create parent directories if needed
            destFile.parentFile?.mkdirs()
            
            sourceFile.copyTo(destFile, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Copy file using FileChannel for better performance
     */
    @JvmStatic
    suspend fun copyFileChannel(sourcePath: String, destPath: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            FileInputStream(sourcePath).use { fis ->
                FileOutputStream(destPath).use { fos ->
                    val sourceChannel = fis.channel
                    val destChannel = fos.channel
                    sourceChannel.transferTo(0, sourceChannel.size(), destChannel)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Read file content as byte array
     */
    @JvmStatic
    suspend fun readFileToByteArray(filePath: String): ByteArray? = withContext(Dispatchers.IO) {
        return@withContext try {
            File(filePath).readBytes()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Write byte array to file
     */
    @JvmStatic
    suspend fun writeByteArrayToFile(data: ByteArray, filePath: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(filePath)
            file.parentFile?.mkdirs()
            file.writeBytes(data)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Calculate MD5 hash of file
     */
    @JvmStatic
    suspend fun calculateMD5(filePath: String): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val digest = MessageDigest.getInstance("MD5")
            FileInputStream(filePath).use { fis ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            
            val hashBytes = digest.digest()
            val hexString = StringBuilder()
            for (b in hashBytes) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get file size in bytes
     */
    @JvmStatic
    fun getFileSize(filePath: String): Long {
        return try {
            File(filePath).length()
        } catch (e: Exception) {
            -1L
        }
    }

    /**
     * Get file size from URI
     */
    @JvmStatic
    fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            val contentResolver = context.contentResolver
            val afd = contentResolver.openAssetFileDescriptor(uri, "r")
            afd?.use { it.length } ?: -1L
        } catch (e: Exception) {
            -1L
        }
    }

    /**
     * Delete file or directory recursively
     */
    @JvmStatic
    fun deleteRecursively(path: String): Boolean {
        return try {
            File(path).deleteRecursively()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if file exists
     */
    @JvmStatic
    fun fileExists(filePath: String): Boolean {
        return try {
            File(filePath).exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get file extension
     */
    @JvmStatic
    fun getFileExtension(filePath: String): String {
        return try {
            val file = File(filePath)
            val name = file.name
            val lastDot = name.lastIndexOf('.')
            if (lastDot > 0) name.substring(lastDot + 1) else ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Get file name without extension
     */
    @JvmStatic
    fun getFileNameWithoutExtension(filePath: String): String {
        return try {
            val file = File(filePath)
            val name = file.name
            val lastDot = name.lastIndexOf('.')
            if (lastDot > 0) name.substring(0, lastDot) else name
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Generate unique filename with timestamp
     */
    @JvmStatic
    fun generateUniqueFileName(prefix: String, extension: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
        return "${prefix}_${timestamp}.${extension}"
    }

    /**
     * Read input stream to byte array
     */
    @JvmStatic
    suspend fun readInputStreamToByteArray(inputStream: InputStream): ByteArray = withContext(Dispatchers.IO) {
        return@withContext inputStream.use { input ->
            val buffer = ByteArrayOutputStream()
            val data = ByteArray(BUFFER_SIZE)
            var nRead: Int
            while (input.read(data, 0, data.size).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.toByteArray()
        }
    }

    /**
     * Copy input stream to output stream
     */
    @JvmStatic
    suspend fun copyStream(inputStream: InputStream, outputStream: OutputStream): Long = withContext(Dispatchers.IO) {
        return@withContext try {
            var totalBytes = 0L
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            
            inputStream.use { input ->
                outputStream.use { output ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead
                    }
                    output.flush()
                }
            }
            totalBytes
        } catch (e: Exception) {
            -1L
        }
    }

    /**
     * Create temporary file
     */
    @JvmStatic
    fun createTempFile(context: Context, prefix: String, suffix: String): File? {
        return try {
            File.createTempFile(prefix, suffix, context.cacheDir)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get cache directory size
     */
    @JvmStatic
    fun getCacheDirectorySize(context: Context): Long {
        return try {
            calculateDirectorySize(context.cacheDir)
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Calculate directory size recursively
     */
    @JvmStatic
    fun calculateDirectorySize(directory: File): Long {
        var size = 0L
        try {
            if (directory.exists()) {
                directory.walkTopDown().forEach { file ->
                    if (file.isFile) {
                        size += file.length()
                    }
                }
            }
        } catch (e: Exception) {
            // Handle error
        }
        return size
    }

    /**
     * Clear cache directory
     */
    @JvmStatic
    fun clearCacheDirectory(context: Context): Boolean {
        return try {
            context.cacheDir.deleteRecursively()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Format file size to human readable string
     */
    @JvmStatic
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    /**
     * Check if path is safe (prevents directory traversal)
     */
    @JvmStatic
    fun isSafePath(path: String): Boolean {
        return try {
            val file = File(path)
            val canonical = file.canonicalPath
            !canonical.contains("..") && !path.contains("../")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Ensure directory exists, create if not
     */
    @JvmStatic
    fun ensureDirectoryExists(dirPath: String): Boolean {
        return try {
            val dir = File(dirPath)
            if (!dir.exists()) {
                dir.mkdirs()
            } else true
        } catch (e: Exception) {
            false
        }
    }
}