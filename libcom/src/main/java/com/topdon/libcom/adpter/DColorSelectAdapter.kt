package com.topdon.libcom.adpter

import android.content.Context
import com.topdon.lib.core.adapter.EnhancedColorSelectAdapter
import com.topdon.lib.core.bean.ColorSelectBean

/**
 * Backward compatibility wrapper for DColorSelectAdapter  
 * Delegates to EnhancedColorSelectAdapter in libapp.core
 * 
 * @deprecated Product requirement: all color picking should use ColorPickDialog style
 */
@Deprecated("Product requirement: all color picking should use ColorPickDialog style")
class DColorSelectAdapter(
    context: Context,
    private val initialColorList: MutableList<ColorSelectBean> = mutableListOf()
) : EnhancedColorSelectAdapter(context, initialColorList) {

    // Legacy API support - expose colors as colorBean for backward compatibility
    val colorBean: MutableList<ColorSelectBean>
        get() = initialColorList

    // Legacy API support
    var listener: ((code: Int, color: Int) -> Unit)? = null
        set(value) {
            field = value
            onColorSelectedListener = { colorBean, position ->
                value?.invoke(position, colorBean.colorValue)
            }
        }

    fun selected(index: Int) {
        setSelectedColor(if (index < itemCount) getSelectedColors().getOrNull(index)?.colorValue ?: 0 else 0)
    }
}