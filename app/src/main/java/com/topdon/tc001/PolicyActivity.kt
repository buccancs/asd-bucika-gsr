package com.topdon.tc001

import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import kotlinx.android.synthetic.main.activity_policy.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 条款 1: 用户条款  2: 隐私条款  3: 第三方
 *
 * 服务返回有错误时,加载默认条款
 */
@Route(path = RouterConfig.POLICY)
class PolicyActivity : BaseActivity() {

    private val mHandler = Handler(Looper.getMainLooper())

    companion object {
        const val KEY_THEME_TYPE = "key_theme_type"
    }

    private var themeType = 1
    private var themeStr = ""

    override fun initContentView() = R.layout.activity_policy

    override fun initView() {
        if (intent.hasExtra(KEY_THEME_TYPE)) {
            themeType = intent.getIntExtra(KEY_THEME_TYPE, 1)
        }
        themeStr = when (themeType) {
            1 -> getString(R.string.user_services_agreement)
            2 -> getString(R.string.privacy_policy)
            3 -> getString(R.string.third_party_components)
            else -> getString(R.string.user_services_agreement)
        }

        title_view.setTitleText(themeStr)
        // Load local policy documents only - no online functionality
        loadLocalPolicy(policy_web)
        delayShowWebView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 为解决闪缩白屏问题，延时打开webView
     */
    private fun delayShowWebView() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(200)
            launch(Dispatchers.Main) {
                policy_web.visibility = View.VISIBLE
            }
        }
    }

    override fun initData() {
        // No online data loading - all policy documents are loaded locally
    }

    override fun httpErrorTip(text: String, requestUrl: String) {
        // No online functionality - this method is not needed
    }

    /**
     * Load local policy documents only - no online functionality
     */
    private fun loadLocalPolicy(view: WebView) {
        when (themeType) {
            1 -> {
                if (BaseApplication.instance.isDomestic()) {
                    view.loadUrl("file:///android_asset/web/services_agreement_default_inside_china.html")
                } else {
                    //用户服务协议
                    view.loadUrl("file:///android_asset/web/services_agreement_default.html")
                }
            }

            2 -> {
                if (BaseApplication.instance.isDomestic()) {
                    view.loadUrl("file:///android_asset/web/privacy_default_inside_china.html")
                } else {
                    //隐私政策
                    view.loadUrl("file:///android_asset/web/privacy_default.html")
                }
            }

            3 -> {
                //第三方组件
                view.loadUrl("file:///android_asset/web/third_statement.html")
            }
        }
    }

}