package com.jaygoo.widget;

/**
 * ================================================
 * JayGoo
 * [Technical comment in Chinese - content removed for ASCII compatibility]
 * create
 * : it works for draw indicator text
 * ================================================
 */
public class SeekBarState {
    public String indicatorText;
    public float value; //now progress value
    public boolean isMin;
    public boolean isMax;

    @Override
    public String toString() {
        return "indicatorText: " + indicatorText + " ,isMin: " + isMin + " ,isMax: " + isMax;
    }
}
