package com.topdon.lib.core.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Enhanced Temperature View consolidating thermal camera display functionality
 * Provides advanced thermal imaging capabilities with optimized rendering
 */
open class EnhancedTemperatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    companion object {
        private const val TAG = "EnhancedTemperatureView"
        
        // Temperature display modes
        const val MODE_NORMAL = 0
        const val MODE_HIGH_TEMP = 1
        const val MODE_LOW_TEMP = 2
        const val MODE_TEMP_DIFF = 3
        
        // Color palettes for thermal display
        val THERMAL_PALETTE_IRON = intArrayOf(
            Color.BLACK, Color.BLUE, Color.MAGENTA, 
            Color.RED, Color.YELLOW, Color.WHITE
        )
        
        val THERMAL_PALETTE_RAINBOW = intArrayOf(
            Color.BLACK, Color.BLUE, Color.CYAN, 
            Color.GREEN, Color.YELLOW, Color.RED, Color.WHITE
        )
    }

    // Surface rendering
    private var surfaceHolder: SurfaceHolder? = null
    private val renderScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isRendering = false

    // Temperature data
    private var temperatureData: FloatArray? = null
    private var temperatureMatrix: Array<FloatArray>? = null
    private var imageWidth = 0
    private var imageHeight = 0

    // Display configuration
    private var displayMode = MODE_NORMAL
    private var colorPalette = THERMAL_PALETTE_IRON
    private var minTemperature = -20.0f
    private var maxTemperature = 120.0f
    private var temperatureRange = maxTemperature - minTemperature

    // Visual settings
    private var showTemperaturePoints = true
    private var showTemperatureScale = true
    private var temperatureUnit = "°C"
    
    // Touch handling
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var onTemperaturePointClickListener: ((Float, Float, Float) -> Unit)? = null

    // Rendering objects
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }
    private var thermalBitmap: Bitmap? = null
    private var displayBitmap: Bitmap? = null

    init {
        holder.addCallback(this)
        holder.setFormat(PixelFormat.RGB_565)
        
        // Process custom attributes if provided
        attrs?.let { processAttributes(it) }
    }

    private fun processAttributes(attrs: AttributeSet) {
        // Process custom attributes for temperature view configuration
        // This would typically read from styleable attributes
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceHolder = holder
        startRendering()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface size changes
        updateDisplaySize(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopRendering()
        surfaceHolder = null
    }

    /**
     * Set temperature data for display
     */
    fun setTemperatureData(data: FloatArray, width: Int, height: Int) {
        temperatureData = data
        imageWidth = width
        imageHeight = height
        
        // Convert to matrix format for easier processing
        temperatureMatrix = Array(height) { y ->
            FloatArray(width) { x ->
                val index = y * width + x
                if (index < data.size) data[index] else 0f
            }
        }
        
        updateTemperatureRange()
        generateThermalBitmap()
        invalidateDisplay()
    }

    /**
     * Set temperature matrix data
     */
    fun setTemperatureMatrix(matrix: Array<FloatArray>) {
        temperatureMatrix = matrix
        imageHeight = matrix.size
        imageWidth = if (matrix.isNotEmpty()) matrix[0].size else 0
        
        // Flatten to array for compatibility
        temperatureData = matrix.flatMap { it.asIterable() }.toFloatArray()
        
        updateTemperatureRange()
        generateThermalBitmap()
        invalidateDisplay()
    }

    /**
     * Set display mode for temperature visualization
     */
    fun setDisplayMode(mode: Int) {
        displayMode = mode
        generateThermalBitmap()
        invalidateDisplay()
    }

    /**
     * Set color palette for thermal display
     */
    fun setColorPalette(palette: IntArray) {
        colorPalette = palette
        generateThermalBitmap()
        invalidateDisplay()
    }

    /**
     * Set temperature range for display
     */
    fun setTemperatureRange(min: Float, max: Float) {
        minTemperature = min
        maxTemperature = max
        temperatureRange = max - min
        generateThermalBitmap()
        invalidateDisplay()
    }

    /**
     * Get temperature at specific pixel coordinates
     */
    fun getTemperatureAt(x: Float, y: Float): Float? {
        val matrix = temperatureMatrix ?: return null
        
        // Convert screen coordinates to image coordinates
        val imageX = (x / width * imageWidth).toInt()
        val imageY = (y / height * imageHeight).toInt()
        
        return if (imageY in 0 until imageHeight && imageX in 0 until imageWidth) {
            matrix[imageY][imageX]
        } else null
    }

    /**
     * Set temperature point click listener
     */
    fun setOnTemperaturePointClickListener(listener: (x: Float, y: Float, temperature: Float) -> Unit) {
        onTemperaturePointClickListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                
                getTemperatureAt(lastTouchX, lastTouchY)?.let { temp ->
                    onTemperaturePointClickListener?.invoke(lastTouchX, lastTouchY, temp)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateTemperatureRange() {
        val data = temperatureData ?: return
        
        if (data.isNotEmpty()) {
            minTemperature = data.minOrNull() ?: minTemperature
            maxTemperature = data.maxOrNull() ?: maxTemperature
            temperatureRange = maxTemperature - minTemperature
        }
    }

    private fun generateThermalBitmap() {
        val matrix = temperatureMatrix ?: return
        
        if (imageWidth <= 0 || imageHeight <= 0) return
        
        try {
            thermalBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
            
            val pixels = IntArray(imageWidth * imageHeight)
            
            for (y in 0 until imageHeight) {
                for (x in 0 until imageWidth) {
                    val temperature = matrix[y][x]
                    val color = temperatureToColor(temperature)
                    pixels[y * imageWidth + x] = color
                }
            }
            
            thermalBitmap?.setPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight)
        } catch (e: Exception) {
            // Handle bitmap creation error
        }
    }

    private fun temperatureToColor(temperature: Float): Int {
        val normalizedTemp = when {
            temperatureRange <= 0 -> 0.5f
            temperature <= minTemperature -> 0f
            temperature >= maxTemperature -> 1f
            else -> (temperature - minTemperature) / temperatureRange
        }
        
        return interpolateColor(normalizedTemp, colorPalette)
    }

    private fun interpolateColor(position: Float, palette: IntArray): Int {
        if (palette.isEmpty()) return Color.BLACK
        if (palette.size == 1) return palette[0]
        
        val scaledPosition = position * (palette.size - 1)
        val index = scaledPosition.toInt()
        val fraction = scaledPosition - index
        
        return when {
            index >= palette.size - 1 -> palette.last()
            index < 0 -> palette.first()
            else -> {
                val color1 = palette[index]
                val color2 = palette[index + 1]
                interpolateColors(color1, color2, fraction)
            }
        }
    }

    private fun interpolateColors(color1: Int, color2: Int, fraction: Float): Int {
        val a = Color.alpha(color1) + ((Color.alpha(color2) - Color.alpha(color1)) * fraction).toInt()
        val r = Color.red(color1) + ((Color.red(color2) - Color.red(color1)) * fraction).toInt()
        val g = Color.green(color1) + ((Color.green(color2) - Color.green(color1)) * fraction).toInt()
        val b = Color.blue(color1) + ((Color.blue(color2) - Color.blue(color1)) * fraction).toInt()
        
        return Color.argb(a, r, g, b)
    }

    private fun startRendering() {
        if (isRendering) return
        isRendering = true
        
        renderScope.launch {
            while (isRendering) {
                drawFrame()
                delay(33) // ~30 FPS
            }
        }
    }

    private fun stopRendering() {
        isRendering = false
    }

    private fun drawFrame() {
        val holder = surfaceHolder ?: return
        
        try {
            val canvas = holder.lockCanvas()
            if (canvas != null) {
                try {
                    // Clear canvas
                    canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR)
                    
                    // Draw thermal image
                    thermalBitmap?.let { bitmap ->
                        val destRect = Rect(0, 0, width, height)
                        canvas.drawBitmap(bitmap, null, destRect, paint)
                    }
                    
                    // Draw temperature scale if enabled
                    if (showTemperatureScale) {
                        drawTemperatureScale(canvas)
                    }
                    
                    // Draw temperature points if enabled
                    if (showTemperaturePoints) {
                        drawTemperaturePoints(canvas)
                    }
                    
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        } catch (e: Exception) {
            // Handle drawing error
        }
    }

    private fun drawTemperatureScale(canvas: Canvas) {
        // Draw temperature scale on the side
        val scaleWidth = 60f
        val scaleHeight = height * 0.8f
        val scaleLeft = width - scaleWidth - 20f
        val scaleTop = (height - scaleHeight) / 2
        
        // Draw scale background
        paint.color = Color.BLACK
        paint.alpha = 128
        canvas.drawRect(scaleLeft - 10, scaleTop, scaleLeft + scaleWidth + 10, scaleTop + scaleHeight, paint)
        
        // Draw scale colors
        val steps = 50
        for (i in 0 until steps) {
            val position = i.toFloat() / (steps - 1)
            val color = interpolateColor(1 - position, colorPalette) // Invert for top-to-bottom
            
            paint.color = color
            paint.alpha = 255
            
            val y = scaleTop + position * scaleHeight
            canvas.drawRect(scaleLeft, y, scaleLeft + scaleWidth, y + scaleHeight / steps, paint)
        }
        
        // Draw temperature labels
        textPaint.color = Color.WHITE
        textPaint.textSize = 28f
        
        canvas.drawText("${maxTemperature.toInt()}$temperatureUnit", 
            scaleLeft + scaleWidth / 2, scaleTop - 5, textPaint)
        canvas.drawText("${minTemperature.toInt()}$temperatureUnit", 
            scaleLeft + scaleWidth / 2, scaleTop + scaleHeight + 30, textPaint)
    }

    private fun drawTemperaturePoints(canvas: Canvas) {
        // Draw temperature measurement points
        // This would draw crosshairs or markers at measurement points
    }

    private fun updateDisplaySize(width: Int, height: Int) {
        // Handle display size updates
        post {
            generateThermalBitmap()
            invalidateDisplay()
        }
    }

    private fun invalidateDisplay() {
        // Trigger display update
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRendering()
        renderScope.cancel()
        
        thermalBitmap?.recycle()
        displayBitmap?.recycle()
    }

    /**
     * Export current thermal image
     */
    fun exportThermalBitmap(): Bitmap? {
        return thermalBitmap?.copy(Bitmap.Config.ARGB_8888, false)
    }

    /**
     * Set temperature unit display
     */
    fun setTemperatureUnit(unit: String) {
        temperatureUnit = unit
        invalidateDisplay()
    }

    /**
     * Enable/disable temperature scale display
     */
    fun setTemperatureScaleVisible(visible: Boolean) {
        showTemperatureScale = visible
        invalidateDisplay()
    }

    /**
     * Enable/disable temperature points display
     */
    fun setTemperaturePointsVisible(visible: Boolean) {
        showTemperaturePoints = visible
        invalidateDisplay()
    }
}