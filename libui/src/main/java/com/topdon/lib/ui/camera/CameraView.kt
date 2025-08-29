package com.topdon.lib.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.NonNull
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.R
import java.nio.ByteBuffer
import java.util.*
import kotlin.concurrent.thread

class CameraView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener {

    preview
    lateinit var mTextureView: TextureView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun initView() {
        inflate(context, R.layout.camera_lay, this)
        mTextureView = findViewById(R.id.camera_texture)
        mTextureView.alpha = 0.4f
        lis = ScaleGestureDetector(context, this)

        onResumeView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mCameraDevice?.close()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isScale && event.action != MotionEvent.ACTION_UP) {
            return lis.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleW = mTextureView.width * (scale - 1) / 2f
                scaleH = mTextureView.height * (scale - 1) / 2f
                startX = event.x - mTextureView.x
                startY = event.y - mTextureView.y
                val view: View = mTextureView.parent as View
                parentViewW = view.width.toFloat()
                parentViewH = view.height.toFloat()
            }
            MotionEvent.ACTION_MOVE -> {
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                moveX = event.x - startX
                moveY = event.y - startY
                if (moveX - scaleW < 0f) moveX = 0f + scaleW
                if (moveY - scaleH < 0f) moveY = 0f + scaleH
                if (moveX + scaleW > parentViewW - mTextureView.width) {
                    moveX = parentViewW - mTextureView.width - scaleW
                }
                if (moveY + scaleH > parentViewH - mTextureView.height) {
                    moveY = parentViewH - mTextureView.height - scaleH
                }
                mTextureView.x = moveX
                mTextureView.y = moveY
            }
            MotionEvent.ACTION_UP -> {
 isScale = false//
            }
        }
        return lis.onTouchEvent(event)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        isScale = true
        detector?.let {
            val scaleFactor = it.scaleFactor - 1
            scale += scaleFactor
            mTextureView.scaleX = scale
            mTextureView.scaleY = scale
        }
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        isScale = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {

    }

    distance
    private var startY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var parentViewW = 0f
    private var parentViewH = 0f
    private var isScale = false
    private var scale = 1f
 private var scaleW = 0f//
    private var scaleH = 0f

    private lateinit var lis: ScaleGestureDetector

//////////////////
 /** */
    private val REQUEST_CAMERA_CODE = 0x100

 /**photo */
    private var mBtnTake: Button? = null

 /** */
    private var mImageView: ImageView? = null

 /**ID */
    private lateinit var mCameraId: String

 /** */
    private var mCaptureSize: Size? = null

    image
    private lateinit var mImageReader: ImageReader

    image
    private lateinit var mCameraHandler: Handler

 /** */
    private var mCameraDevice: CameraDevice? = null

    preview
    private var mPreviewSize: Size? = null

 /** */
    private lateinit var mCameraCaptureBuilder: CaptureRequest.Builder

 /**photo */
    private var mCameraCaptureSession: CameraCaptureSession? = null

 /** */
    private var mCameraManager: CameraManager? = null

 /** */
    private val mStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(@NonNull camera: CameraDevice) {
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                mCameraDevice = camera
                // preview
                takePreview()
            }

            override fun onDisconnected(@NonNull camera: CameraDevice) {
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                camera.close()
                mCameraDevice = null
            }

            override fun onError(@NonNull camera: CameraDevice, error: Int) {
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                camera.close()
                mCameraDevice = null
            }
        }

    /**
     * preview
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
        mTextureView.rotation = 0f
 // SurfaceTexture
        val surfaceTexture = mTextureView.surfaceTexture
 // settings
        surfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // create
        val previewSurface = Surface(surfaceTexture)
        try {
            // preview
            mCameraCaptureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // medium
            mCameraCaptureBuilder.addTarget(previewSurface)
            // create
            mCameraDevice!!.createCaptureSession(
                listOf(
                    previewSurface,
                    mImageReader.surface
                ), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull session: CameraCaptureSession) {
                        try {
                            // configuration
                            val captureRequest = mCameraCaptureBuilder.build()
 // session
                            mCameraCaptureSession = session
                            // preview
                            mCameraCaptureSession!!.setRepeatingRequest(
                                captureRequest,
                                null,
                                mCameraHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(@NonNull session: CameraCaptureSession) {
                        // configuration
                    }
                }, mCameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun onResumeView() {
        mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
 // SurfaceTexture
 // settings
                Log.w("123", "width:$width, height:$height")
                //w:h = 1 / 1.33
                setUpCamera(width, height)
//                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
 // SurfaceTexture
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
 // SurfaceTexture 
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
 // SurfaceTexture 
            }
        }
    }


    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
 Log.e("123", ":${e.message}")
 ToastUtils.showShort("")
        }
    }

    /**
 * settings
 * @param width 
     * high
     */
    private fun setUpCamera(width: Int, height: Int) {
        // create
        mCameraHandler = Handler(Looper.getMainLooper())
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            for (cameraId in cameraManager.cameraIdList) {
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                if (null != facing && CameraCharacteristics.LENS_FACING_FRONT == facing) continue
 // StreamConfigurationMap
                val map =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // preview
                mPreviewSize =
                    getOptimalSize(
                        map.getOutputSizes(SurfaceTexture::class.java),
                        width,
                        height
                    )
 // photo
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
                mCaptureSize = Size(w, h)
                Log.w("123", "w:${sizes[0].width}, h:${sizes[0].height}")
 Log.w("123", "w:${w}, h:${h}")
//                mCaptureSize = Size(1000, 1000)
//                mCaptureSize =
//                    Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))) { lhs, rhs ->
//                        java.lang.Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth())
//                    }
 // ImageReaderphoto
                setupImageReader()
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * medium
 * @param sizeMap optional
 * @param width 
     * high
 * @return widthheightsize
     */
    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size {
        // create
        val sizeList: MutableList<Size> = ArrayList()
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        for (option in sizeMap) {
            // high
            if (width > height) {
                if (option.width > width && option.height > height) {
                    sizeList.add(option)
                }
            } else {
                if (option.width > height && option.height > width) {
                    sizeList.add(option)
                }
            }
        }
        // data
        return if (sizeList.size > 0) {
            Collections.min(
                sizeList
            ) { lhs, rhs -> java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong()) }
        } else sizeMap[0]
    }

    private var flag = 0

    /**
     * settingsImageReader
     */
    private fun setupImageReader() {
        // medium
        mImageReader = ImageReader.newInstance(
            mCaptureSize!!.width,
            mCaptureSize!!.height,
            ImageFormat.JPEG,
            1
        )
        // image
        mImageReader.setOnImageAvailableListener({ reader ->
            flag = 1
            // [Technical comment in Chinese - content removed for ASCII compatibility]
            val image: Image = reader.acquireLatestImage()
            // save
            mCameraHandler.post(ImageSaver(image))
 // UI
 runOnUiThread { // 
                val buffer: ByteBuffer = image.planes[0].buffer
                // create
                buffer.rewind()
                // create
                val bytes = ByteArray(buffer.remaining())
                // finish
                buffer[bytes]
                // image
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                if (null != bitmap) {
                    val h = bitmap.height
                    val w = bitmap.width
                    mImageView?.let {
                        val sw = ScreenUtil.getScreenWidth(context)
                        it.layoutParams.height = sw / 2 * w / h
                        it.layoutParams.width = sw / 2
                        it.setImageBitmap(bitmap)
                    }

                }
                flag++
                thread {
                    while (flag < 3) {
//                        delay(100)
                        Thread.sleep(100)
                    }
                    flag = 0
                    image.close()
                }
            }
        }, mCameraHandler)
    }

    /**
     * save
     */
    private inner class ImageSaver(image: Image) : Runnable {

        image
        private val mImage: Image = image

        override fun run() {
//            ImageSaverTool().save(mImage)
            flag++
        }

    }

}

