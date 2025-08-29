package com.topdon.lib.core.tools

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.topdon.lib.core.R
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.tools.ConstantLanguages

object LanguageTool {

    /**
     * 获取显示各国语言 - Only supports English
     */
    fun showLanguage(context: Context): String {
        return context.getString(R.string.english)
    }

    /**
     * 获取各国语言简称 - Only supports English
     * (用于服务端多语言的识别)
     */
    fun useLanguage(context: Context): String {
        return "en-WW"
    }

    /**
     * 获取各国语言简称 - Only supports English
     * (用于声明接口)
     */
    fun useStatementLanguage(): String {
        return "EN"
    }
}