package com.topdon.lib.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Size
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintSet
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils

import com.topdon.lib.core.listener.BitmapViewListener
import com.topdon.lib.ui.R
import kotlinx.android.synthetic.main.camera_lay.view.*
import java.util.*

/**
 * 相机预览
 */
class CameraPreView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener,
    BitmapViewListener {
    private var cameraCharacteristics: CameraCharacteristics ?= null
    private var isReverse : Boolean = false
    private lateinit var mTextureView: TextureView
    private var cameraWidth = 0

    var isPreviewing = false

    var cameraPreViewCloseListener : (() -> Unit) ?= null

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
        mTextureView.post { cameraWidth = mTextureView.width }
        lis = ScaleGestureDetector(context, this)
        onResumeView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isPreviewing = false
        mCameraDevice?.close()
        mCameraHandler?.removeCallbacksAndMessages(null)
    }

    private var startX = 0f//记录落点到控件的距离
    private var startY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var parentViewW = 0f
    private var parentViewH = 0f
    private var isScale = false
    private var scale = 1f
    private var scaleW = 0f//单边缩放长度
    private var scaleH = 0f

    private lateinit var lis: ScaleGestureDetector

    @SuppressLint("ClickableViewAccessibility")
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
                //滑动
                moveX = event.x - startX
                moveY = event.y - startY
                //根据移动情况，不可见时候关闭
//                if (moveX-scaleW < -mTextureView.width ||
//                    moveX+scaleW > parentViewW ||
//                    moveY - scaleH < -mTextureView.height ||
//                    moveY + scaleH > parentViewH){
//                    cameraPreViewCloseListener?.invoke()
//                }

                //越界归位
//                if (moveX - scaleW < 0f) moveX = 0f + scaleW
//                if (moveY - scaleH < 0f) moveY = 0f + scaleH
//                if (moveX + scaleW > parentViewW - mTextureView.width) {
//                    moveX = parentViewW - mTextureView.width - scaleW
//                }
//                if (moveY + scaleH > parentViewH - mTextureView.height) {
//                    moveY = parentViewH - mTextureView.height - scaleH
//                }
//                // Logging removed
                // 此处ImageReader用于拍照所需
//                setupImageReader()
                // 为摄像头赋值
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            // Debug removed
        }
    }

    /**
     * 选择SizeMap中大于并且最接近width和height的size
     * @param sizeMap 可选的尺寸
     * @param width 宽
     * @param height 高
     * @return 最接近width和height的size
     */
    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size {
        // 创建列表
        val sizeList: MutableList<Size> = ArrayList()
        // 遍历
        for (option in sizeMap) {
            // 判断宽度是否大于高度
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
        // 判断存储Size的列表是否有数据
        return if (sizeList.size > 0) {
            Collections.min(sizeList) { lhs, rhs ->
                java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong())
            }
        } else sizeMap[0]
    }

    /**
     * 打开相机
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        isPreviewing = true
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            isPreviewing = false
            ToastUtils.showShort("打开相机失败")
        }
    }

    /**
     * 关闭相机
     */
    @SuppressLint("MissingPermission")
    fun closeCamera() {
        isPreviewing = false
        try {
            mCameraDevice?.close()
            //恢复原始状态
            mTextureView.x = 0f
            mTextureView.y = 0f
            mTextureView.scaleX = 1f
            mTextureView.scaleY = 1f
            scale = 1f
//            isReverse = false
        } catch (e: Exception) {
            ToastUtils.showShort("关闭相机失败")
        }
    }

    override val viewX: Float
        get() = mTextureView.x - (viewWidth - mTextureView.width)/2
    override val viewY: Float
        get() = mTextureView.y - (viewHeight - mTextureView.height)/2
    override val viewAlpha: Float
        get() = mTextureView.alpha
    override val viewWidth: Float
        get() = mTextureView.width * scale
    override val viewHeight: Float
        get() = mTextureView.height * scale
    override val viewScale: Float
        get() = scale

    fun setCameraAlpha(alpha : Float){
        mTextureView?.alpha = 1 - alpha
    }

    fun setZoom(zoomLeve : Int){
        scale = zoomLeve * 0.5f
        mTextureView.scaleX = scale
        mTextureView.scaleY = scale
        invalidate()
    }

}
