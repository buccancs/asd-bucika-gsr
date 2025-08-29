package com.topdon.thermal07.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.topdon.thermal07.R;


/**
 * UI
 */
public class PowerConsumptionRankingsBatteryView extends View {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    public static final int MAX_LEVEL = 100;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    public static final int DEFAULT_LEVEL = 40;

    /**
 * View
     */
    private int width;
    /**
     * high
     */
    private int height;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private DrawFilter drawFilter;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int shellStrokeWidth;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int shellCornerRadius;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int shellWidth;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int shellHeight;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int shellHeadCornerRadius;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int shellHeadWidth;
    /**
     * high
     */
    private int shellHeadHeight;

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int levelWidth;
    /**
     * high
     */
    private int levelMaxHeight;
    /**
     * high
     */
    private int levelHeight = 100;

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int gap;

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private Paint shellPaint;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private RectF shellRectF;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private RectF shellHeadRect;

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private Paint levelPaint;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private RectF levelRect;

    /**
     * low
     */
    private int lowerPowerColor;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int onlineColor;
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private int offlineColor;

    public PowerConsumptionRankingsBatteryView(Context context) {
        super(context);
    }

    public PowerConsumptionRankingsBatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        initTypeArray(context, attrs);

 //settings 
        shellPaint = new Paint();
        shellPaint.setAntiAlias(true);
        shellPaint.setColor(onlineColor);
        shellPaint.setStrokeWidth(shellStrokeWidth);
        shellPaint.setAntiAlias(true);

 //settings 
        levelPaint = new Paint();
        levelPaint.setColor(onlineColor);
        levelPaint.setStyle(Paint.Style.FILL);
        levelPaint.setStrokeWidth(levelWidth);

        shellRectF = new RectF();
        shellHeadRect = new RectF();
        levelRect = new RectF();
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    private void initTypeArray(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PowerConsumptionRankingsBatteryView);
        lowerPowerColor = typedArray.getColor(R.styleable.PowerConsumptionRankingsBatteryView_batteryLowerPowerColor,
                getResources().getColor(R.color.lowerPowerColor));
        onlineColor = typedArray.getColor(R.styleable.PowerConsumptionRankingsBatteryView_batteryOnlineColor,
                getResources().getColor(R.color.onlineColor));
        offlineColor = typedArray.getColor(R.styleable.PowerConsumptionRankingsBatteryView_batteryOfflineColor,
                getResources().getColor(R.color.offlineColor));
        // info
        shellCornerRadius = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellCornerRadius,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_corner));
        shellWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellWidth,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_width));
        shellHeight = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeight,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_height));
        shellStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellStrokeWidth,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_shell_stroke_width));

        // info
        shellHeadCornerRadius = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeadCornerRadius,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_head_corner));
        shellHeadWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeadWidth,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_head_width));
        shellHeadHeight = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryShellHeadHeight,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_head_height));

        // high
        levelMaxHeight = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryLevelMaxHeight,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_level_max_height));
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        levelWidth = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryLevelWidth,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_level_width));

        // [Technical comment in Chinese - content removed for ASCII compatibility]
        gap = typedArray.getDimensionPixelOffset(R.styleable.PowerConsumptionRankingsBatteryView_batteryGap,
                getResources().getDimensionPixelOffset(R.dimen.power_consumption_rankings_dimen_main_battery_view_gap));
 //typedArray
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // measurement
        width = getMeasuredWidth();
        // measurement
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(drawFilter);

        // [Technical comment in Chinese - content removed for ASCII compatibility]

 // left 
        shellHeadRect.left = width / 2 - shellHeadWidth / 2;
 // top 0
        shellHeadRect.top = 0;
 // right 
        shellHeadRect.right = width / 2 + shellHeadWidth / 2;
        // high
        shellHeadRect.bottom = shellHeadHeight;

        // [Technical comment in Chinese - content removed for ASCII compatibility]

 // left
        shellRectF.left = shellStrokeWidth / 2;
        // high
        shellRectF.top = shellStrokeWidth / 2 + shellHeadHeight;
 // right 
        shellRectF.right = width - shellStrokeWidth / 2;
        // high
        shellRectF.bottom = height - shellStrokeWidth / 2;

        // [Technical comment in Chinese - content removed for ASCII compatibility]

 // left 
        levelRect.left = shellStrokeWidth + gap;

        // high
        // high
        float topOffset = (height - shellHeadHeight - gap * 2 - shellStrokeWidth) * (MAX_LEVEL - levelHeight) / MAX_LEVEL;
        // high
        levelRect.top = shellHeadHeight + shellStrokeWidth + gap + topOffset;

 // right 
        levelRect.right = width - shellStrokeWidth - gap;

 // bottom 
        levelRect.bottom = height - shellStrokeWidth - gap;

        // [Technical comment in Chinese - content removed for ASCII compatibility]
        shellPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(shellHeadRect, shellHeadCornerRadius, shellHeadCornerRadius, shellPaint);
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        canvas.drawRect(shellHeadRect.left, shellHeadRect.bottom - shellHeadCornerRadius,
                shellHeadRect.left + shellHeadCornerRadius, shellHeadRect.bottom, shellPaint);
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        canvas.drawRect(shellHeadRect.right - shellHeadCornerRadius, shellHeadRect.bottom - shellHeadCornerRadius,
                shellHeadRect.right, shellHeadRect.bottom, shellPaint);

        // [Technical comment in Chinese - content removed for ASCII compatibility]
        shellPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(shellRectF, shellCornerRadius, shellCornerRadius, shellPaint);

        // [Technical comment in Chinese - content removed for ASCII compatibility]
        canvas.drawRect(levelRect, levelPaint);
    }

    /**
 * settings
     *
     * @param level
     */
    public void setLevelHeight(int level) {
        this.levelHeight = level;
        if (this.levelHeight < 0) {
            levelHeight = MAX_LEVEL;
        } else if (this.levelHeight > MAX_LEVEL) {
            levelHeight = MAX_LEVEL;
        }
        postInvalidate();
    }

    /**
 * settings 
     */
    public void setOnline() {
        shellPaint.setColor(onlineColor);
        levelPaint.setColor(onlineColor);
        postInvalidate();
    }

    /**
 * settings 
     */
    public void setOffline() {
        shellPaint.setColor(offlineColor);
        levelPaint.setColor(offlineColor);
        postInvalidate();
    }

    /**
     * low
     */
    public void setLowerPower() {
        shellPaint.setColor(lowerPowerColor);
        levelPaint.setColor(lowerPowerColor);
        postInvalidate();
    }
}
