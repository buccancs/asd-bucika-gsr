package com.topdon.menu

import android.content.Context
import android.util.AttributeSet
import com.topdon.lib.core.menu.EnhancedMenuSecondView

/**
 * Backward compatibility wrapper for MenuSecondView.
 * Delegates to EnhancedMenuSecondView in libapp core.
 */
class MenuSecondView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : EnhancedMenuSecondView(context, attrs, defStyleAttr)