package com.topdon.lib.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.topdon.lib.ui.R
// Removed synthetic imports - using findViewById instead

/**
 * 校准方向
 */
class SteeringWheelView : LinearLayout, OnClickListener {

    var listener: ((action: Int, moveX: Int) -> Unit)? = null
    var moveX = 30
    var rotationIR = 270
    set(value) {
        field = value
        val tvConfirm = findViewById<View>(R.id.tv_confirm)
        if (value == 270 || value == 90){
            tvConfirm?.rotation = 270f
            rotation = 90f
        }else{
            tvConfirm?.rotation = 0f
            rotation = 0f
        }
        requestLayout()
    }

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
        inflate(context, R.layout.ui_steering_wheel_view, this)
        findViewById<View>(R.id.steering_wheel_start_btn)?.setOnClickListener(this)
        findViewById<View>(R.id.steering_wheel_center_btn)?.setOnClickListener(this)
        findViewById<View>(R.id.steering_wheel_end_btn)?.setOnClickListener(this)
        val tvConfirm = findViewById<View>(R.id.tv_confirm)
        if (rotationIR == 270 || rotationIR == 90){
            tvConfirm?.rotation = 270f
            rotation = 90f
        }else{
            tvConfirm?.rotation = 0f
            rotation = 0f
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.steering_wheel_start_btn -> {
                moveX += 10
                if (moveX > 60) {
                    moveX = 60
                }
                listener?.invoke(-1, moveX)
            }
            R.id.steering_wheel_center_btn -> {
                listener?.invoke(0, moveX)
            }
            R.id.steering_wheel_end_btn -> {
                moveX -= 10
                if (moveX < -20) {
                    moveX = -20
                }
                listener?.invoke(1, moveX)
            }
        }
    }


}