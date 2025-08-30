package com.infisense.usbir.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.topdon.lib.core.view.EnhancedCameraView;

/**
 * Backward compatibility wrapper for CameraJpegView
 * Delegates to EnhancedCameraView in libapp.core with enhanced capabilities
 */
public class CameraJpegView extends FrameLayout {
    
    private String TAG = "CameraView";
    private Thread cameraThread;
    private EnhancedCameraView enhancedCameraView;

    public CameraJpegView(Context context) {
        this(context, null, 0);
    }

    public CameraJpegView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraJpegView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        // Initialize the enhanced camera view
        enhancedCameraView = new EnhancedCameraView(context, attrs, defStyleAttr);
        addView(enhancedCameraView);
    }

    // Legacy API compatibility methods
    public void setSyncimage(EnhancedCameraView.SynchronizedBitmap syncimage) {
        // Delegate to enhanced camera view
        if (enhancedCameraView != null) {
            enhancedCameraView.setSyncImage(syncimage);
        }
    }


    public void start() {
        if (enhancedCameraView != null) {
            enhancedCameraView.startContinuousRendering();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (cameraThread != null) {
            cameraThread.interrupt();
        }
        super.onDetachedFromWindow();
    }
}
