package com.topdon.lib.core.ktbase

import android.content.*
import android.content.pm.ActivityInfo
import android.os.*
import android.provider.MediaStore

import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import com.google.gson.Gson
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.R
import com.topdon.lib.core.bean.event.SocketStateEvent
import com.topdon.lib.core.bean.event.device.DeviceConnectEvent
import com.topdon.lib.core.bean.response.ResponseUserInfo
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.common.UserInfoManager
import com.topdon.lib.core.dialog.LoadingDialog
import com.topdon.lib.core.tools.*
import com.topdon.lib.core.dialog.TipCameraProgressDialog
import com.topdon.lib.core.dialog.TipProgressDialog
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.bean.CommonBean
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * Created by admin on 2018/6/4.
 */
abstract class BaseActivity : RxAppCompatActivity() {

    val TAG = this.javaClass.simpleName

    protected abstract fun initContentView(): Int
    protected abstract fun initView()
    protected abstract fun initData()

    protected var savedInstanceState: Bundle? = null

    protected open fun isLockPortrait(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApplication.instance.activitys.add(this)
        this.savedInstanceState = savedInstanceState
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }

        if (isLockPortrait()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        window.navigationBarColor = ContextCompat.getColor(this,R.color.toolbar_16131E)
        setContentView(initContentView())
        initView()
        initData()
        // Removed login sync
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, SharedManager.getLanguage(newBase ?: this)))
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        cameraDialog?.dismiss()
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
        BaseApplication.instance.activitys.remove(this)
    }

    /**
     * 监听 USB 连接状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getConnectState(event: DeviceConnectEvent) {
        if (event.isConnect) {
            connected()
        } else {
            disConnected()
        }
    }
    protected open fun connected() {

    }
    protected open fun disConnected() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSocketConnectState(event: SocketStateEvent) {
        if (event.isConnect) {
            onSocketConnected(event.isTS004)
        } else {
            onSocketDisConnected(event.isTS004)
        }
    }
    protected open fun onSocketConnected(isTS004: Boolean) {

    }
    protected open fun onSocketDisConnected(isTS004: Boolean) {

    }

    /**
     * 新版 LMS 风格的加载中弹框.
     */
    private var loadingDialog: LoadingDialog? = null
    /**
     * 真是醉了，一个加载中的弹框现在就有 3 种，不管了，继续加，理论上后续都要改成这个.
     */
    fun showLoadingDialog(@StringRes resId: Int = R.string.tip_loading) {
        showLoadingDialog(getString(resId))
    }
    fun showLoadingDialog(text: CharSequence?) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.setTips(text)
        loadingDialog?.show()
    }
    /**
     * 真是醉了，一个加载中的弹框现在就有 3 种，不管了，继续加，理论上后续都要改成这个.
     */
    fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private var cameraDialog: TipCameraProgressDialog? = null
    fun showCameraLoading() {
        if (cameraDialog != null && cameraDialog!!.isShowing) {
            return
        }
        if (cameraDialog == null) {
            cameraDialog =
                TipCameraProgressDialog.Builder(this)
                    .setCanceleable(false)
                    .create()
        }
        try {
            if (!(isFinishing && isDestroyed)) {
                cameraDialog?.show()
            }
        }catch (e:Exception){
            //临时捕获方案，后面需求完成后再追踪优化
        }
    }
    fun dismissCameraLoading() {
        if (cameraDialog != null && cameraDialog!!.isShowing) {
            cameraDialog?.dismiss()
        }
    }

    // Login synchronization removed

    protected class TakePhotoResult : ActivityResultContract<File, File?>() {
        private lateinit var file: File

        override fun createIntent(context: Context, input: File): Intent {
            file = input
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): File? = if (resultCode == RESULT_OK) file else null
    }
}
