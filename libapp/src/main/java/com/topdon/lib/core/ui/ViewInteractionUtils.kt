package com.topdon.lib.core.ui

import android.view.MotionEvent
import android.view.View

/**
 * Consolidated view interaction utilities.
 * Combines drag, gesture, and touch functionality from various lib* modules.
 */
object ViewInteractionUtils {

    /**
     * Make any view draggable with optional delay.
     * Consolidated from libir DragViewUtil.
     */
    @JvmStatic
    @JvmOverloads
    fun makeDraggable(view: View, delay: Long = 0L) {
        view.setOnTouchListener(DragTouchListener(delay))
    }

    /**
     * Remove drag functionality from a view.
     */
    @JvmStatic
    fun removeDragFunctionality(view: View) {
        view.setOnTouchListener(null)
    }

    private class DragTouchListener(private val delay: Long) : View.OnTouchListener {
        private var downX = 0f
        private var downY = 0f
        private var downTime = 0L
        private var isMove = false
        private var canDrag = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handleActionDown(v, event)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    handleActionMove(v, event)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handleActionUp()
                    true
                }
                else -> false
            }
        }

        private fun handleActionDown(view: View, event: MotionEvent) {
            downX = event.rawX
            downY = event.rawY
            downTime = System.currentTimeMillis()
            isMove = false
            canDrag = delay == 0L
        }

        private fun handleActionMove(view: View, event: MotionEvent) {
            val currentTime = System.currentTimeMillis()
            
            // Check if delay period has passed
            if (!canDrag && currentTime - downTime >= delay) {
                canDrag = true
            }

            if (canDrag) {
                val moveX = event.rawX - downX
                val moveY = event.rawY - downY
                
                // Only move if there's significant movement
                if (!isMove && (kotlin.math.abs(moveX) > 10 || kotlin.math.abs(moveY) > 10)) {
                    isMove = true
                }

                if (isMove) {
                    updateViewPosition(view, moveX, moveY)
                }
            }
        }

        private fun handleActionUp() {
            isMove = false
            canDrag = false
        }

        private fun updateViewPosition(view: View, deltaX: Float, deltaY: Float) {
            val parent = view.parent as? View ?: return
            
            // Calculate new position
            val newX = view.x + deltaX
            val newY = view.y + deltaY
            
            // Constrain to parent bounds
            val constrainedX = newX.coerceIn(0f, parent.width - view.width.toFloat())
            val constrainedY = newY.coerceIn(0f, parent.height - view.height.toFloat())
            
            view.x = constrainedX
            view.y = constrainedY
            
            // Update the reference point for next move
            downX += deltaX
            downY += deltaY
        }
    }

    /**
     * Scale gesture handler for zoom functionality.
     */
    interface ScaleGestureListener {
        fun onScaleBegin(scaleFactor: Float): Boolean
        fun onScale(scaleFactor: Float): Boolean
        fun onScaleEnd(scaleFactor: Float)
    }

    /**
     * Combined drag and scale gesture handler.
     */
    interface DragAndScaleGestureListener : ScaleGestureListener {
        fun onDragStart(x: Float, y: Float)
        fun onDragMove(deltaX: Float, deltaY: Float)
        fun onDragEnd()
    }
}