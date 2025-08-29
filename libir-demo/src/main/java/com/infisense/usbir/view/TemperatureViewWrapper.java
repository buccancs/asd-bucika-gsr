package com.infisense.usbir.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.infisense.iruvc.ircmd.IRCMD;
import com.infisense.iruvc.sdkisp.LibIRTemp;
import com.infisense.iruvc.utils.Line;
import com.topdon.lib.core.view.BaseTemperatureView;

import java.text.DecimalFormat;

/**
 * Backward compatibility wrapper for libir-demo TemperatureView.
 * This is the simpler version that doesn't implement BaseDualView.OnFrameCallback.
 * 
 * @deprecated Use com.topdon.lib.core.view.BaseTemperatureView directly for new code.
 */
public class TemperatureView extends BaseTemperatureView {
    
    // Original libir-demo specific fields
    private IRCMD ircmd;
    private DecimalFormat temperatureFormatter;
    
    // Temperature display settings
    private boolean showCenterTemperature = true;
    private boolean showMaxMinTemperature = true;
    
    // Temperature listener
    private OnTemperatureChangeListener mTemperatureChangeListener;
    
    public TemperatureView(Context context) {
        super(context);
    }

    public TemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        
        // Initialize libir-demo specific components
        temperatureFormatter = new DecimalFormat("#.##");
        initIRCmd();
    }

    private void initIRCmd() {
        // Initialize IR command interface if needed
        // This would be specific to the demo implementation
    }

    @Override
    protected void initTemperatureCalculation() {
        // Initialize LibIRTemp for temperature calculations
        if (irtemp == null) {
            irtemp = new LibIRTemp();
        }
    }

    @Override
    protected void updateDrawingRects(int width, int height) {
        // Set up source and destination rectangles for bitmap drawing
        if (srcRect == null) {
            srcRect = new Rect();
        }
        if (destRect == null) {
            destRect = new Rect();
        }
        
        // Use standard thermal resolution for demo
        srcRect.set(0, 0, 256, 192); // Standard thermal camera resolution
        destRect.set(0, 0, width, height);
    }

    @Override
    protected float calculateTemperatureAtPoint(Point point) {
        if (irtemp != null && mSynchronizedBitmap != null) {
            // Convert display coordinates to thermal image coordinates
            Point thermalPoint = convertDisplayToThermal(point);
            float temp = irtemp.getTemp(thermalPoint.x, thermalPoint.y);
            
            // Notify listener
            if (mTemperatureChangeListener != null) {
                mTemperatureChangeListener.onPointTemperatureChanged(temp);
            }
            
            return temp;
        }
        return 0f;
    }

    @Override
    protected float[] calculateTemperatureForLine(Line line) {
        if (irtemp != null && mSynchronizedBitmap != null) {
            // Convert line to thermal coordinates and calculate min/max/avg
            Line thermalLine = convertLineDisplayToThermal(line);
            float[] temps = irtemp.getLineTemps(thermalLine);
            
            // Notify listener
            if (mTemperatureChangeListener != null) {
                mTemperatureChangeListener.onLineTemperatureChanged(temps[0], temps[1], temps[2]);
            }
            
            return temps;
        }
        return new float[]{0f, 0f, 0f}; // min, max, avg
    }

    @Override
    protected float[] calculateTemperatureForRectangle(Rect rect) {
        if (irtemp != null && mSynchronizedBitmap != null) {
            // Convert rectangle to thermal coordinates and calculate min/max/avg
            Rect thermalRect = convertRectDisplayToThermal(rect);
            float[] temps = irtemp.getRectTemps(thermalRect);
            
            // Notify listener
            if (mTemperatureChangeListener != null) {
                mTemperatureChangeListener.onRectTemperatureChanged(temps[0], temps[1], temps[2]);
            }
            
            return temps;
        }
        return new float[]{0f, 0f, 0f}; // min, max, avg
    }

    @Override
    protected String formatTemperature(float temperature) {
        // Use the demo's temperature formatting
        return temperatureFormatter.format(temperature) + "°C";
    }

    @Override
    protected void drawTemperatureValues() {
        super.drawTemperatureValues();
        
        // Draw additional temperature info specific to demo version
        if (showCenterTemperature) {
            drawCenterTemperature();
        }
        
        if (showMaxMinTemperature) {
            drawMaxMinTemperature();
        }
    }

    private void drawCenterTemperature() {
        if (mCanvas != null && irtemp != null) {
            Point center = new Point(getWidth() / 2, getHeight() / 2);
            float centerTemp = calculateTemperatureAtPoint(center);
            String tempText = formatTemperature(centerTemp);
            
            // Draw center temperature
            float textWidth = mTextPaint.measureText(tempText);
            mCanvas.drawText(tempText, center.x - textWidth / 2, center.y - 30, mTextPaint);
        }
    }

    private void drawMaxMinTemperature() {
        if (mCanvas != null && irtemp != null) {
            float maxTemp = irtemp.getMaxTemp();
            float minTemp = irtemp.getMinTemp();
            Point maxPoint = irtemp.getMaxTempPoint();
            Point minPoint = irtemp.getMinTempPoint();
            
            // Convert thermal coordinates to display coordinates
            Point displayMaxPoint = convertThermalToDisplay(maxPoint);
            Point displayMinPoint = convertThermalToDisplay(minPoint);
            
            // Draw max temperature
            String maxText = "MAX: " + formatTemperature(maxTemp);
            mCanvas.drawText(maxText, displayMaxPoint.x + 10, displayMaxPoint.y - 10, mTextPaint);
            
            // Draw min temperature
            String minText = "MIN: " + formatTemperature(minTemp);
            mCanvas.drawText(minText, displayMinPoint.x + 10, displayMinPoint.y - 10, mTextPaint);
        }
    }

    // Coordinate conversion methods
    private Point convertDisplayToThermal(Point displayPoint) {
        if (destRect == null || srcRect == null) {
            return displayPoint;
        }
        
        float scaleX = (float) srcRect.width() / destRect.width();
        float scaleY = (float) srcRect.height() / destRect.height();
        
        int thermalX = (int) (displayPoint.x * scaleX);
        int thermalY = (int) (displayPoint.y * scaleY);
        
        return new Point(thermalX, thermalY);
    }

    private Point convertThermalToDisplay(Point thermalPoint) {
        if (destRect == null || srcRect == null) {
            return thermalPoint;
        }
        
        float scaleX = (float) destRect.width() / srcRect.width();
        float scaleY = (float) destRect.height() / srcRect.height();
        
        int displayX = (int) (thermalPoint.x * scaleX);
        int displayY = (int) (thermalPoint.y * scaleY);
        
        return new Point(displayX, displayY);
    }

    private Line convertLineDisplayToThermal(Line displayLine) {
        Point thermalStart = convertDisplayToThermal(displayLine.startPoint);
        Point thermalEnd = convertDisplayToThermal(displayLine.endPoint);
        return new Line(thermalStart, thermalEnd);
    }

    private Rect convertRectDisplayToThermal(Rect displayRect) {
        Point topLeft = convertDisplayToThermal(new Point(displayRect.left, displayRect.top));
        Point bottomRight = convertDisplayToThermal(new Point(displayRect.right, displayRect.bottom));
        return new Rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
    }

    // Public API methods from original libir-demo implementation
    public void setShowCenterTemperature(boolean show) {
        showCenterTemperature = show;
    }

    public void setShowMaxMinTemperature(boolean show) {
        showMaxMinTemperature = show;
    }

    public void setTemperatureChangeListener(OnTemperatureChangeListener listener) {
        mTemperatureChangeListener = listener;
    }

    public void setIRCmd(IRCMD ircmd) {
        this.ircmd = ircmd;
    }

    public IRCMD getIRCmd() {
        return ircmd;
    }

    public float getCurrentCenterTemperature() {
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        return calculateTemperatureAtPoint(center);
    }

    public void calibrate(float ambientTemp, float emissivity) {
        if (irtemp != null) {
            irtemp.setAmbientTemp(ambientTemp);
            irtemp.setEmissivity(emissivity);
        }
    }

    // Temperature change listener interface for demo version
    public interface OnTemperatureChangeListener {
        void onPointTemperatureChanged(float temperature);
        void onLineTemperatureChanged(float min, float max, float avg);
        void onRectTemperatureChanged(float min, float max, float avg);
        void onCenterTemperatureChanged(float temperature);
        void onMaxMinTemperatureChanged(float max, float min, Point maxPoint, Point minPoint);
    }
}