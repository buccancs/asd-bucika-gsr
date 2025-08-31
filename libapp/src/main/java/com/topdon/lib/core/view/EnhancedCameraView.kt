package com.topdon.lib.core.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Enhanced camera view for JPEG frame rendering with improved performance and lifecycle management
 * Consolidated from libir.view.CameraJpegView with additional capabilities
 */
class EnhancedCameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "EnhancedCameraView"
        private const val DEFAULT_FPS = 30
    }

    // Frame data
    private var currentBitmap: Bitmap? = null
    private var syncImage: SynchronizedBitmap? = null
    
    // Threading
    private var renderingJob: Job? = null
    private var isRendering = AtomicBoolean(false)
    private var targetFps = DEFAULT_FPS
    
    // Paint for drawing
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Callbacks
    var onFrameRendered: ((Bitmap) -> Unit)? = null
    var onRenderingError: ((Exception) -> Unit)? = null

    init {
        initView()
    }

    private fun initView() {
        paint.isAntiAlias = true
        paint.color = Color.WHITE
    }

    /**
     * Set bitmap for direct rendering
     */
    fun setBitmap(bitmap: Bitmap?) {
        currentBitmap = bitmap
        if (bitmap != null && !isRendering.get()) {
            renderFrame(bitmap)
        }
    }

    /**
     * Set synchronized bitmap for continuous rendering
     */
    fun setSyncImage(syncImage: SynchronizedBitmap?) {
        this.syncImage = syncImage
        if (syncImage != null) {
            startContinuousRendering()
        } else {
            stopContinuousRendering()
        }
    }

    /**
     * Start continuous rendering with frame synchronization
     */
    fun startContinuousRendering() {
        if (isRendering.get()) return
        
        isRendering.set(true)
        renderingJob = CoroutineScope(Dispatchers.Main).launch {
            renderLoop()
        }
    }

    /**
     * Stop continuous rendering
     */
    fun stopContinuousRendering() {
        isRendering.set(false)
        renderingJob?.cancel()
        renderingJob = null
    }

    /**
     * Set target FPS for continuous rendering
     */
    fun setTargetFps(fps: Int) {
        targetFps = fps.coerceIn(1, 60)
    }

    private suspend fun renderLoop() = withContext(Dispatchers.Default) {
        val frameDelay = 1000L / targetFps
        
        while (isRendering.get() && !Thread.currentThread().isInterrupted) {
            try {
                syncImage?.let { sync ->
                    var bitmapToRender: Bitmap? = null
                    
                    synchronized(sync.viewLock) {
                        if (!sync.valid) {
                            sync.viewLock.wait(frameDelay)
                        }
                        
                        if (sync.valid && sync.bitmap != null) {
                            bitmapToRender = sync.bitmap
                            sync.valid = false
                        }
                    }
                    
                    bitmapToRender?.let { bitmap ->
                        withContext(Dispatchers.Main) {
                            renderFrame(bitmap)
                        }
                    }
                }
                
                delay(frameDelay)
                
            } catch (e: InterruptedException) {
                break
            } catch (e: Exception) {
                onRenderingError?.invoke(e)
                delay(100) // Brief pause on error
            }
        }
    }

    private fun renderFrame(bitmap: Bitmap) {
        if (!isAvailable) return
        
        try {
            val canvas = lockCanvas() ?: return
            
            try {
                // Clear canvas
                canvas.drawColor(Color.BLACK)
                
                // Calculate scaling to fit bitmap to view while maintaining aspect ratio
                val viewWidth = width.toFloat()
                val viewHeight = height.toFloat()
                val bitmapWidth = bitmap.width.toFloat()
                val bitmapHeight = bitmap.height.toFloat()
                
                if (bitmapWidth > 0 && bitmapHeight > 0) {
                    val scaleX = viewWidth / bitmapWidth
                    val scaleY = viewHeight / bitmapHeight
                    val scale = minOf(scaleX, scaleY)
                    
                    val scaledWidth = bitmapWidth * scale
                    val scaledHeight = bitmapHeight * scale
                    
                    val left = (viewWidth - scaledWidth) / 2
                    val top = (viewHeight - scaledHeight) / 2
                    
                    val destRect = android.graphics.Rect(
                        left.toInt(),
                        top.toInt(),
                        (left + scaledWidth).toInt(),
                        (top + scaledHeight).toInt()
                    )
                    
                    canvas.drawBitmap(bitmap, null, destRect, paint)
                }
                
                onFrameRendered?.invoke(bitmap)
                
            } finally {
                unlockCanvasAndPost(canvas)
            }
            
        } catch (e: Exception) {
            onRenderingError?.invoke(e)
        }
    }

    /**
     * Enhanced bitmap rendering with custom transformations
     */
    fun renderBitmapWithTransform(
        bitmap: Bitmap,
        transform: BitmapTransform? = null
    ) {
        if (!isAvailable) return
        
        try {
            val processedBitmap = if (transform != null) {
                transform.apply(bitmap)
            } else {
                bitmap
            }
            
            renderFrame(processedBitmap)
            
        } catch (e: Exception) {
            onRenderingError?.invoke(e)
        }
    }

    /**
     * Capture current frame as bitmap
     */
    fun captureFrame(): Bitmap? {
        return try {
            currentBitmap?.copy(currentBitmap?.config ?: Bitmap.Config.ARGB_8888, false)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Clear the current view
     */
    fun clearView() {
        if (!isAvailable) return
        
        try {
            val canvas = lockCanvas() ?: return
            try {
                canvas.drawColor(Color.BLACK)
            } finally {
                unlockCanvasAndPost(canvas)
            }
        } catch (e: Exception) {
        }
    }

    override fun onDetachedFromWindow() {
        stopContinuousRendering()
        super.onDetachedFromWindow()
    }

    /**
     * Get rendering statistics
     */
    fun getRenderingStats(): RenderingStats {
        return RenderingStats(
            isRendering = isRendering.get(),
            targetFps = targetFps,
            hasCurrentBitmap = currentBitmap != null,
            hasSyncImage = syncImage != null
        )
    }

    /**
     * Interface for bitmap transformations
     */
    fun interface BitmapTransform {
        fun apply(bitmap: Bitmap): Bitmap
    }

    /**
     * Data class for rendering statistics
     */
    data class RenderingStats(
        val isRendering: Boolean,
        val targetFps: Int,
        val hasCurrentBitmap: Boolean,
        val hasSyncImage: Boolean
    )

    /**
     * Synchronized bitmap wrapper for thread-safe access
     */
    class SynchronizedBitmap {
        @Volatile
        var bitmap: Bitmap? = null
        
        @Volatile
        var valid: Boolean = false
        
        val viewLock = Object()
        
        fun updateBitmap(newBitmap: Bitmap) {
            synchronized(viewLock) {
                bitmap = newBitmap
                valid = true
                viewLock.notifyAll()
            }
        }
        
        fun invalidate() {
            synchronized(viewLock) {
                valid = false
            }
        }
    }
}