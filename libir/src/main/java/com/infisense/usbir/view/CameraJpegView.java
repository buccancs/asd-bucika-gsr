package com.infisense.usbir.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.energy.iruvc.utils.SynchronizedBitmap;
import com.topdon.lib.core.view.EnhancedCameraView;

/**
 * Backward compatibility wrapper for CameraJpegView
 * Delegates to EnhancedCameraView in libapp.core with enhanced capabilities
 */
public class CameraJpegView extends EnhancedCameraView {
    
    private String TAG = "CameraView";
    private Thread cameraThread;

    public CameraJpegView(Context context) {
        this(context, null, 0);
    }

    public CameraJpegView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraJpegView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Legacy API compatibility methods
    public void setSyncimage(SynchronizedBitmap syncimage) {
        if (syncimage != null) {
            // Convert legacy SynchronizedBitmap to enhanced version
            EnhancedCameraView.SynchronizedBitmap enhancedSync = new EnhancedCameraView.SynchronizedBitmap();
            // Set up a bridge between old and new sync objects
            setSyncImage(enhancedSync);
            
            // Start a thread to bridge the old sync mechanism
            startLegacySyncBridge(syncimage, enhancedSync);
        } else {
            setSyncImage(null);
        }
    }
    
    private void startLegacySyncBridge(SynchronizedBitmap oldSync, EnhancedCameraView.SynchronizedBitmap newSync) {
        cameraThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (oldSync.viewLock) {
                        if (!oldSync.valid) {
                            oldSync.viewLock.wait();
                        }
                        if (oldSync.valid && oldSync.bitmap != null) {
                            newSync.updateBitmap(oldSync.bitmap);
                            oldSync.valid = false;
                        }
                    }
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cameraThread.start();
    }

    public void start() {
        startContinuousRendering();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (cameraThread != null) {
            cameraThread.interrupt();
        }
        super.onDetachedFromWindow();
    }
}

    public void stop() {
        cameraThread.interrupt();
        try {
            cameraThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
