package com.topdon.lib.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Base SeekBar class providing common functionality for all SeekBar implementations
 */
public abstract class BaseSeekBar extends View {

    // Indicator modes
    public final static int INDICATOR_ALWAYS_HIDE = 0;
    public final static int INDICATOR_ALWAYS_SHOW = 1;
    public final static int INDICATOR_SHOW_WHEN_TOUCHED = 2;

    @IntDef({INDICATOR_ALWAYS_HIDE, INDICATOR_ALWAYS_SHOW, INDICATOR_SHOW_WHEN_TOUCHED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorModeDef {}

    // Common fields
    protected Paint mPaint;
    protected Paint mTextPaint;
    protected int mIndicatorMode = INDICATOR_SHOW_WHEN_TOUCHED;

    public BaseSeekBar(Context context) {
        this(context, null);
    }

    public BaseSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected abstract void init(Context context, AttributeSet attrs);

    public void setIndicatorMode(@IndicatorModeDef int mode) {
        mIndicatorMode = mode;
        invalidate();
    }

    public int getIndicatorMode() {
        return mIndicatorMode;
    }

    protected float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}