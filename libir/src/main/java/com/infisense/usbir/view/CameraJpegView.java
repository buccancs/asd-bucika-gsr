package com.infisense.usbir.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import android.view.TextureView;

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
        runnable = new Runnable() {
            @Override
            public void run() {
                Canvas canvas = null;
                while (!cameraThread.isInterrupted()) {
                    synchronized (syncimage.viewLock) {
                        if (syncimage.valid == false) {
                            try {
                                syncimage.viewLock.wait();
                            } catch (InterruptedException e) {
                                cameraThread.interrupt();
                            }
                        }
                        if (syncimage.valid == true) {
                            canvas = lockCanvas();
                            if (canvas == null)
                                continue;

                            //p2
                            /*Matrix matrix = new Matrix();
                            matrix.setRotate(90);
                            Bitmap newBM = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                            */
                            Bitmap mScaledBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
                            canvas.drawBitmap(mScaledBitmap, 0, 0, null);

                            Paint paint = new Paint();  //画笔
                            paint.setStrokeWidth(2);  //设置线宽。单位为像素
                            paint.setAntiAlias(true); //抗锯齿
                            paint.setColor(Color.WHITE);  //画笔颜色

                            int cross_len = 20;
                            canvas.drawLine(getWidth() / 2f - cross_len, getHeight() / 2f,
                                    getWidth() / 2f + cross_len, getHeight() / 2f, paint);
                            canvas.drawLine(getWidth() / 2f, getHeight() / 2f - cross_len,
                                    getWidth() / 2f, getHeight() / 2f + cross_len, paint);
                            unlockCanvasAndPost(canvas);
                            syncimage.valid = false;
                        }
                    }
                    try {
                        cameraThread.sleep(1);
                    } catch (InterruptedException e) {
                        cameraThread.interrupt();
                    }
                }
            }
        };

    }

    public void start() {
        cameraThread = new Thread(runnable);
        cameraThread.start();
    }

    public void stop() {
        cameraThread.interrupt();
        try {
            cameraThread.join();
        } catch (InterruptedException e) {
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
