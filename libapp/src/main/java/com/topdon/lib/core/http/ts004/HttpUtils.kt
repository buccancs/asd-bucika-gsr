package com.topdon.lib.core.http.ts004
import com.topdon.lms.sdk.xutils.common.Callback
import com.topdon.lms.sdk.xutils.http.RequestParams
import com.topdon.lms.sdk.xutils.x
object HttpUtils {
    /**
 * settingspseudo color
 * @param mode pseudo color
 * @param iResponseCallback 
     * @ void
     */
    fun setPseudoColor(mode: Int, iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.addBodyParameter("enable", false)
        params.addBodyParameter("mode", mode)
        params.uri = TS004URL.SET_PSEUDO_COLOR
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
 * pseudo color
     */
    fun getPseudoColor(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PSEUDO_COLOR
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
 * settings
 * @param mode :0-100
 * @param iResponseCallback 
     * @ void
     */
    fun setBrightness(brightness: Int, iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.addBodyParameter("brightness", brightness)
        params.uri = TS004URL.SET_PANEL_PARAM
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    fun getBrightness(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PANEL_PARAM
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * medium
 * @param iResponseCallback 
     * @ void
     */
    fun setPip(enable: Boolean,iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.addBodyParameter("enable", enable)
        params.uri = TS004URL.SET_PIP
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * medium
     */
    fun getPip(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PIP
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
 * settings
 * @param factor :1,2,4,8
 * @param iResponseCallback 
     * @ void
     */
    fun setZoom(factor: Int, iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.addBodyParameter("enable", true)
        params.addBodyParameter("factor", factor)
        params.uri = TS004URL.SET_ZOOM
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    fun getZoom(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_ZOOM
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * settingsphoto
 * @param iResponseCallback 
     * @void
     */
    fun setCamera(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.SET_SNAPSHOT
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * settingsvideo
 * @param enable 
 * @param iResponseCallback 
     * @void
     */
    fun setVideo(enable: Boolean,iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.addBodyParameter("enable", enable)
        params.uri = TS004URL.GET_VRECORD
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
 * video
 * @param iResponseCallback 
     * @void
     */
    fun getVideoStatus(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_RECORD_STATUS
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * info
     */
    fun getVersion(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_VERSION
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * info
     */
    fun getDeviceDetails(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_DEVICE_DETAILS
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
     * info
     */
    fun getFreeSpace(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_FREE_SPACE
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }

    /**
 * settings
     */
    fun getResetAll(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_RESET_ALL
        params.isAsJsonContent = true
        x.http().post(params,iResponseCallback!!)
    }
}