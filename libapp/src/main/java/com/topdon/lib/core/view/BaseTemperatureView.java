package com.topdon.lib.core.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.lang.ref.WeakReference;

/**
 * Consolidated TemperatureView base class combining functionality from:
 * - libir TemperatureView (1,562 lines) - supports BaseDualView.OnFrameCallback
 * - libir-demo TemperatureView (1,300 lines) - simpler implementation
 * 
 * This base class contains all shared functionality, with specific implementations
 * extending this for module-specific features (like BaseDualView integration).
 */
public abstract class BaseTemperatureView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    /**
     * Simple line class to avoid external dependencies
     */
    public static class ThermalLine {
        public Point startPoint;
        public Point endPoint;
        
        public ThermalLine(Point start, Point end) {
            this.startPoint = new Point(start);
            this.endPoint = new Point(end);
        }
    }

    protected static final String TAG = "BaseTemperatureView";
    
    // Common drawing constants
    protected final int LINE_STROKE_WIDTH = SizeUtils.dp2px(1f);
    protected final int DOT_STROKE_WIDTH = SizeUtils.dp2px(1f);
    protected final int DOT_RADIUS = SizeUtils.dp2px(3f);
    protected final int POINT_SIZE = SizeUtils.sp2px(8f);
    protected final int TEXT_SIZE = SizeUtils.sp2px(12f);
    protected final int TOUCH_TOLERANCE = SizeUtils.sp2px(7f);
    
    // Limits for drawing objects
    protected final int POINT_MAX_COUNT = 3;
    protected final int LINE_MAX_COUNT = 3;
    protected final int RECTANGLE_MAX_COUNT = 3;
    
    // Core fields
    protected Thread temperatureThread;
    protected Runnable runnable;
    
    // Canvas and drawing
    protected Canvas mCanvas;
    protected SurfaceHolder mSurfaceHolder;
    protected boolean isDrawing = false;
    
    // Temperature data  
    protected Object mSynchronizedBitmap; // Generic object to avoid direct dependency
    protected Bitmap mBitmap;
    protected Rect srcRect, destRect;
    
    // Drawing objects storage
    protected ArrayList<Point> mPointList = new ArrayList<>();
    protected ArrayList<ThermalLine> mLineList = new ArrayList<>();
    protected ArrayList<Rect> mRectangleList = new ArrayList<>();
    
    // Paint objects
    protected Paint mLinePaint;
    protected Paint mTextPaint;
    protected Paint mPointPaint;
    protected Paint mRectPaint;
    
    // Touch and selection
    protected int selectedPointIndex = -1;
    protected int selectedLineIndex = -1;
    protected int selectedRectIndex = -1;
    protected Point mTouchDownPoint = new Point();
    protected Point mTouchUpPoint = new Point();
    protected boolean isDragging = false;
    
    // Temperature calculation
    protected DecimalFormat df = new DecimalFormat("#.##");
    protected boolean isShowingTemperature = true;
    
    // Listeners
    protected WeakReference<OnTemperatureUpdateListener> mTemperatureUpdateListener;
    
    public BaseTemperatureView(Context context) {
        super(context);
        init(context, null);
    }

    public BaseTemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseTemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        initSurfaceView();
        initPaints();
        initTemperatureCalculation();
        setOnTouchListener(this);
    }

    protected void initSurfaceView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setKeepScreenOn(true);
    }

    protected void initPaints() {
        // Line paint
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setColor(Color.YELLOW);
        mLinePaint.setStrokeWidth(LINE_STROKE_WIDTH);
        mLinePaint.setStyle(Paint.Style.STROKE);

        // Text paint  
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.YELLOW);
        mTextPaint.setTextSize(TEXT_SIZE);

        // Point paint
        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(Color.YELLOW);
        mPointPaint.setStrokeWidth(DOT_STROKE_WIDTH);

        // Rectangle paint
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(Color.YELLOW);
        mRectPaint.setStrokeWidth(LINE_STROKE_WIDTH);
        mRectPaint.setStyle(Paint.Style.STROKE);
    }

    protected void initTemperatureCalculation() {
        // Initialize temperature calculation library
        // Subclasses can override for specific initialization
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startTemperatureThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Update drawing rectangles based on surface size
        updateDrawingRects(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopTemperatureThread();
    }

    protected void startTemperatureThread() {
        if (temperatureThread == null || !temperatureThread.isAlive()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            drawTemperatureFrame();
                            Thread.sleep(33); // ~30 FPS
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            };
            temperatureThread = new Thread(runnable);
            temperatureThread.start();
        }
    }

    protected void stopTemperatureThread() {
        if (temperatureThread != null && temperatureThread.isAlive()) {
            temperatureThread.interrupt();
            try {
                temperatureThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            temperatureThread = null;
        }
    }

    protected void drawTemperatureFrame() {
        if (mSynchronizedBitmap != null && !isDrawing) {
            isDrawing = true;
            try {
                mCanvas = mSurfaceHolder.lockCanvas();
                if (mCanvas != null) {
                    // Clear canvas
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    
                    // Draw temperature bitmap if available
                    if (mSynchronizedBitmap instanceof Bitmap) {
                        Bitmap bitmap = (Bitmap) mSynchronizedBitmap;
                        if (bitmap != null && !bitmap.isRecycled()) {
                            mCanvas.drawBitmap(bitmap, srcRect, destRect, null);
                        }
                    }
                    
                    // Draw overlays
                    drawTemperatureOverlays();
                    
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
            } catch (Exception e) {
            } finally {
                isDrawing = false;
            }
        }
    }

    protected void drawTemperatureOverlays() {
        if (mCanvas == null) return;
        
        // Draw points
        drawPoints();
        
        // Draw lines
        drawLines();
        
        // Draw rectangles
        drawRectangles();
        
        // Draw temperature values
        if (isShowingTemperature) {
            drawTemperatureValues();
        }
    }

    protected void drawPoints() {
        for (int i = 0; i < mPointList.size(); i++) {
            Point point = mPointList.get(i);
            boolean isSelected = (i == selectedPointIndex);
            
            // Draw cross-hair
            mPointPaint.setColor(isSelected ? Color.RED : Color.YELLOW);
            mCanvas.drawLine(point.x - POINT_SIZE, point.y, point.x + POINT_SIZE, point.y, mPointPaint);
            mCanvas.drawLine(point.x, point.y - POINT_SIZE, point.x, point.y + POINT_SIZE, mPointPaint);
            
            // Draw center dot
            mCanvas.drawCircle(point.x, point.y, DOT_RADIUS, mPointPaint);
        }
    }

    protected void drawLines() {
        // Draw lines
        for (int i = 0; i < mLineList.size(); i++) {
            ThermalLine line = mLineList.get(i);
            boolean isSelected = (i == selectedLineIndex);
            
            mLinePaint.setColor(isSelected ? Color.RED : Color.YELLOW);
            mCanvas.drawLine(line.startPoint.x, line.startPoint.y, 
                           line.endPoint.x, line.endPoint.y, mLinePaint);
        }
    }

    protected void drawRectangles() {
        for (int i = 0; i < mRectangleList.size(); i++) {
            Rect rect = mRectangleList.get(i);
            boolean isSelected = (i == selectedRectIndex);
            
            mRectPaint.setColor(isSelected ? Color.RED : Color.YELLOW);
            mCanvas.drawRect(rect, mRectPaint);
        }
    }

    protected void drawTemperatureValues() {
        // Draw temperature values for points
        for (int i = 0; i < mPointList.size(); i++) {
            Point point = mPointList.get(i);
            float temperature = calculateTemperatureAtPoint(point);
            String tempText = formatTemperature(temperature);
            mCanvas.drawText(tempText, point.x + 15, point.y - 15, mTextPaint);
        }
        
        // Draw temperature values for lines (min, max, avg)
        for (int i = 0; i < mLineList.size(); i++) {
            ThermalLine line = mLineList.get(i);
            float[] temps = calculateTemperatureForLine(line);
            String tempText = String.format("Min:%.1f Max:%.1f Avg:%.1f", temps[0], temps[1], temps[2]);
            int textX = (line.startPoint.x + line.endPoint.x) / 2;
            int textY = (line.startPoint.y + line.endPoint.y) / 2 - 20;
            mCanvas.drawText(tempText, textX, textY, mTextPaint);
        }
        
        // Draw temperature values for rectangles (min, max, avg)
        for (int i = 0; i < mRectangleList.size(); i++) {
            Rect rect = mRectangleList.get(i);
            float[] temps = calculateTemperatureForRectangle(rect);
            String tempText = String.format("Min:%.1f Max:%.1f Avg:%.1f", temps[0], temps[1], temps[2]);
            mCanvas.drawText(tempText, rect.left + 5, rect.top - 5, mTextPaint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return handleTouchDown(event);
            case MotionEvent.ACTION_MOVE:
                return handleTouchMove(event);
            case MotionEvent.ACTION_UP:
                return handleTouchUp(event);
        }
        return false;
    }

    protected boolean handleTouchDown(MotionEvent event) {
        mTouchDownPoint.set((int) event.getX(), (int) event.getY());
        
        // Check if touching existing objects
        selectedPointIndex = findTouchedPoint(mTouchDownPoint);
        selectedLineIndex = findTouchedLine(mTouchDownPoint);
        selectedRectIndex = findTouchedRectangle(mTouchDownPoint);
        
        return true;
    }

    protected boolean handleTouchMove(MotionEvent event) {
        if (selectedPointIndex != -1 || selectedLineIndex != -1 || selectedRectIndex != -1) {
            isDragging = true;
            // Handle dragging of selected objects
            Point currentPoint = new Point((int) event.getX(), (int) event.getY());
            updateSelectedObject(currentPoint);
        }
        return true;
    }

    protected boolean handleTouchUp(MotionEvent event) {
        mTouchUpPoint.set((int) event.getX(), (int) event.getY());
        
        if (!isDragging) {
            // Check for delete gesture (touch down and up in same place)
            if (isDeleteGesture()) {
                deleteSelectedObject();
            } else {
                // Add new object if not touching existing ones
                if (selectedPointIndex == -1 && selectedLineIndex == -1 && selectedRectIndex == -1) {
                    addNewObject();
                }
            }
        }
        
        // Reset selection and dragging state
        selectedPointIndex = -1;
        selectedLineIndex = -1;
        selectedRectIndex = -1;
        isDragging = false;
        
        return true;
    }

    // Abstract and virtual methods for subclass implementation
    protected abstract void updateDrawingRects(int width, int height);
    protected abstract float calculateTemperatureAtPoint(Point point);
    protected abstract float[] calculateTemperatureForLine(ThermalLine line);
    protected abstract float[] calculateTemperatureForRectangle(Rect rect);
    protected abstract String formatTemperature(float temperature);

    // Helper methods
    protected int findTouchedPoint(Point touchPoint) {
        for (int i = 0; i < mPointList.size(); i++) {
            Point point = mPointList.get(i);
            if (isPointNear(touchPoint, point, TOUCH_TOLERANCE)) {
                return i;
            }
        }
        return -1;
    }

    protected int findTouchedLine(Point touchPoint) {
        for (int i = 0; i < mLineList.size(); i++) {
            ThermalLine line = mLineList.get(i);
            if (isPointNearLine(touchPoint, line, TOUCH_TOLERANCE)) {
                return i;
            }
        }
        return -1;
    }

    protected int findTouchedRectangle(Point touchPoint) {
        for (int i = 0; i < mRectangleList.size(); i++) {
            Rect rect = mRectangleList.get(i);
            if (isPointNearRectangle(touchPoint, rect, TOUCH_TOLERANCE)) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isPointNear(Point p1, Point p2, int tolerance) {
        return Math.abs(p1.x - p2.x) <= tolerance && Math.abs(p1.y - p2.y) <= tolerance;
    }

    protected boolean isPointNearLine(Point point, ThermalLine line, int tolerance) {
        // Calculate distance from point to line
        float distance = distanceFromPointToLine(point, line);
        return distance <= tolerance;
    }

    protected boolean isPointNearRectangle(Point point, Rect rect, int tolerance) {
        // Check if point is near rectangle edges
        return (Math.abs(point.x - rect.left) <= tolerance && point.y >= rect.top && point.y <= rect.bottom) ||
               (Math.abs(point.x - rect.right) <= tolerance && point.y >= rect.top && point.y <= rect.bottom) ||
               (Math.abs(point.y - rect.top) <= tolerance && point.x >= rect.left && point.x <= rect.right) ||
               (Math.abs(point.y - rect.bottom) <= tolerance && point.x >= rect.left && point.x <= rect.right);
    }

    protected float distanceFromPointToLine(Point point, ThermalLine line) {
        // Calculate perpendicular distance from point to line
        float A = line.endPoint.y - line.startPoint.y;
        float B = line.startPoint.x - line.endPoint.x;
        float C = line.endPoint.x * line.startPoint.y - line.startPoint.x * line.endPoint.y;
        
        return Math.abs(A * point.x + B * point.y + C) / (float) Math.sqrt(A * A + B * B);
    }

    protected boolean isDeleteGesture() {
        return isPointNear(mTouchDownPoint, mTouchUpPoint, TOUCH_TOLERANCE);
    }

    protected void deleteSelectedObject() {
        if (selectedPointIndex != -1 && selectedPointIndex < mPointList.size()) {
            mPointList.remove(selectedPointIndex);
        } else if (selectedLineIndex != -1 && selectedLineIndex < mLineList.size()) {
            mLineList.remove(selectedLineIndex);
        } else if (selectedRectIndex != -1 && selectedRectIndex < mRectangleList.size()) {
            mRectangleList.remove(selectedRectIndex);
        }
    }

    protected void addNewObject() {
        // Default: add point. Subclasses can override for different behavior
        if (mPointList.size() < POINT_MAX_COUNT) {
            mPointList.add(new Point(mTouchDownPoint));
        }
    }

    protected void updateSelectedObject(Point newPosition) {
        if (selectedPointIndex != -1 && selectedPointIndex < mPointList.size()) {
            mPointList.get(selectedPointIndex).set(newPosition.x, newPosition.y);
        } else if (selectedLineIndex != -1 && selectedLineIndex < mLineList.size()) {
            ThermalLine line = mLineList.get(selectedLineIndex);
            int deltaX = newPosition.x - mTouchDownPoint.x;
            int deltaY = newPosition.y - mTouchDownPoint.y;
            line.startPoint.offset(deltaX, deltaY);
            line.endPoint.offset(deltaX, deltaY);
            mTouchDownPoint.set(newPosition.x, newPosition.y);
        } else if (selectedRectIndex != -1 && selectedRectIndex < mRectangleList.size()) {
            Rect rect = mRectangleList.get(selectedRectIndex);
            int deltaX = newPosition.x - mTouchDownPoint.x;
            int deltaY = newPosition.y - mTouchDownPoint.y;
            rect.offset(deltaX, deltaY);
            mTouchDownPoint.set(newPosition.x, newPosition.y);
        }
    }

    // Public API methods
    public void setSynchronizedBitmap(Object bitmap) {
        mSynchronizedBitmap = bitmap;
    }

    public void setShowingTemperature(boolean showing) {
        isShowingTemperature = showing;
    }

    public void clearAllObjects() {
        mPointList.clear();
        mLineList.clear();
        mRectangleList.clear();
    }

    public void addPoint(Point point) {
        if (mPointList.size() < POINT_MAX_COUNT) {
            mPointList.add(new Point(point));
        }
    }

    public void addLine(ThermalLine line) {
        if (mLineList.size() < LINE_MAX_COUNT) {
            mLineList.add(line);
        }
    }

    public void addRectangle(Rect rect) {
        if (mRectangleList.size() < RECTANGLE_MAX_COUNT) {
            mRectangleList.add(new Rect(rect));
        }
    }

    public ArrayList<Point> getPointList() {
        return new ArrayList<>(mPointList);
    }

    public ArrayList<ThermalLine> getLineList() {
        return new ArrayList<>(mLineList);
    }

    public ArrayList<Rect> getRectangleList() {
        return new ArrayList<>(mRectangleList);
    }

    public void setTemperatureUpdateListener(OnTemperatureUpdateListener listener) {
        mTemperatureUpdateListener = new WeakReference<>(listener);
    }

    protected void notifyTemperatureUpdate() {
        OnTemperatureUpdateListener listener = mTemperatureUpdateListener != null ? mTemperatureUpdateListener.get() : null;
        if (listener != null) {
            listener.onTemperatureUpdated();
        }
    }

    /**
     * Listener interface for temperature updates
     */
    public interface OnTemperatureUpdateListener {
        void onTemperatureUpdated();
    }
}