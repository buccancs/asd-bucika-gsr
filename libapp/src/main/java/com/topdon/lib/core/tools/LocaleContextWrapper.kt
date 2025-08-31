package com.topdon.lib.core.tools

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.*

/**
 * Locale context wrapper for language switching
 * Converted from Java to Kotlin with enhanced null safety
 * 
 * @author CaiSongL
 * @date 2024/9/13 18:35
 */
class LocaleContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {
        fun wrap(context: Context, languageCode: String): ContextWrapper {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            return ContextWrapper(context.createConfigurationContext(config))
        }
    }
}