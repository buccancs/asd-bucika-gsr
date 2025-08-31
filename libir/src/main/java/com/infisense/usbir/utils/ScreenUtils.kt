package com.infisense.usbir.utils

import com.topdon.lib.core.utils.ScreenUtil

/**
 * Backward compatibility wrapper for ScreenUtils functionality.
 * All methods delegate to the consolidated ScreenUtil implementation.
 * 
 * This wrapper eliminates ~200 lines of duplicate screen utility code while maintaining
 * full backward compatibility with existing libir usage patterns.
 * 
 * @deprecated Use com.topdon.lib.core.utils.ScreenUtil directly for new code.
 */
@Suppress("UNUSED")
object ScreenUtils {
    
    @JvmStatic
    fun getScreenWidth(context: android.content.Context) = ScreenUtil.getScreenWidth(context)
    
    @JvmStatic
    fun getScreenHeight(context: android.content.Context) = ScreenUtil.getScreenHeight(context)
    
    @JvmStatic
    fun getStatusHeight(context: android.content.Context) = ScreenUtil.getStatusHeight(context)
    
    @JvmStatic
    fun snapShotWithStatusBar(activity: android.app.Activity) = ScreenUtil.snapShotWithStatusBar(activity)
    
    @JvmStatic
    fun snapShotWithoutStatusBar(activity: android.app.Activity) = ScreenUtil.snapShotWithoutStatusBar(activity)
    
    @JvmStatic
    fun getScreenDensityDpi(context: android.content.Context) = ScreenUtil.getScreenDensityDpi(context)
    
    @JvmStatic
    fun getScreenDendity(context: android.content.Context) = ScreenUtil.getScreenDensity(context)
    
    @JvmStatic
    fun dip2px(context: android.content.Context, dpValue: Float) = ScreenUtil.dip2px(context, dpValue)
    
    @JvmStatic
    fun getBottomStatusHeight(context: android.content.Context) = ScreenUtil.getBottomStatusHeight(context)
    
    @JvmStatic
    fun getDpi(context: android.content.Context) = ScreenUtil.getDpi(context)
    
    // Legacy dialog method - preserved as-is for compatibility
    @JvmStatic
    fun showNormalDialog(context: android.content.Context, info: String, dismissListener: android.widget.PopupWindow.OnDismissListener): android.app.Dialog {
        val normalDialog = android.app.AlertDialog.Builder(context)
        normalDialog.setTitle("Info")
        normalDialog.setMessage(info)
        normalDialog.setCancelable(false)
        normalDialog.setPositiveButton("OK") { _, _ ->
            dismissListener.onDismiss()
        }
        return normalDialog.show()
    }
    
    // Preserved IR-specific functionality
    @JvmStatic
    fun getPreviewFPSByDataFlowMode(dataFlowMode: com.energy.iruvc.utils.CommonParams.DataFlowMode): Int {
        return if (dataFlowMode == com.energy.iruvc.utils.CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) {
            25
        } else {
            50
        }
    }
}
