package com.topdon.lib.core.tools

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import android.util.DisplayMetrics
import java.util.*

/**
 * Language utility for managing application language settings
 * @author YanLu
 * @since 17/5/12
 */
object AppLanguageUtils {
    
    private val allLanguages: Map<String, Locale> = mapOf(
        ConstantLanguages.ENGLISH to Locale.ENGLISH
    )

    /**
     * 获取系统默认语言 - Always returns English
     */
    fun getSystemLanguage(): String = ConstantLanguages.ENGLISH

    @Suppress("DEPRECATION")
    fun changeAppLanguage(context: Context, newLanguage: String) {
        val resources = context.resources
        val configuration = resources.configuration

        // app locale
        val locale = getLocaleByLanguage(newLanguage)
        configuration.setLocale(locale)
        // updateConfiguration
        val dm = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }

    fun getConf() {
        // Empty function - placeholder
    }

    private fun isSupportLanguage(language: String): Boolean {
        return allLanguages.containsKey(language)
    }

    fun getSupportLanguage(language: String): String {
        return if (isSupportLanguage(language)) {
            language
        } else {
            ConstantLanguages.ENGLISH
        }
    }

    /**
     * 获取指定语言的locale信息，如果指定语言不存在，返回本机语言，如果本机语言不是语言集合中的一种，返回英语
     *
     * @param language language
     * @return Locale for the specified language
     */
    fun getLocaleByLanguage(language: String): Locale {
        if (isSupportLanguage(language)) {
            return allLanguages[language] ?: Locale.ENGLISH
        } else {
            val locale = Locale.getDefault()
            for ((_, supportedLocale) in allLanguages) {
                if (TextUtils.equals(supportedLocale.language, locale.language)) {
                    return locale
                }
            }
        }
        return Locale.ENGLISH
    }

    fun attachBaseContext(context: Context, language: String): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            context
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val resources = context.resources
        val locale = getLocaleByLanguage(language)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        return context.createConfigurationContext(configuration)
    }
}