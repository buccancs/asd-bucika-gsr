package com.topdon.libcom.dialog

import android.content.Context
import com.topdon.lib.core.dialog.EnhancedAlarmSetDialog

/**
 * Backward compatibility wrapper for TempAlarmSetDialog
 * Delegates to EnhancedAlarmSetDialog in libapp.core
 */
class TempAlarmSetDialog(
    context: Context,
    private val isEdit: Boolean
) : EnhancedAlarmSetDialog(context, isEdit, getDialogLayoutRes(context)) {

    companion object {
        private fun getDialogLayoutRes(context: Context): Int {
            return try {
                context.resources.getIdentifier("dialog_temp_alarm_set", "layout", context.packageName)
            } catch (e: Exception) {
                0
            }
        }
    }

    // All functionality delegated to enhanced parent class
    // Original API maintained for backward compatibility