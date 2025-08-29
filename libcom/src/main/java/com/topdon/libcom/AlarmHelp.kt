package com.topdon.libcom

import android.content.Context
import com.topdon.lib.core.alarm.AlarmManager
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.libcom.view.TempLayout

/**
 * Backward compatibility wrapper for AlarmHelp.
 * Delegates to enhanced AlarmManager in libapp core while preserving original API.
 * @author: CaiSongL
 * @date: 2023/5/5 15:13
 */
@Deprecated("Use AlarmManager in libapp core instead", ReplaceWith("AlarmManager.getInstance(context)"))
class AlarmHelp private constructor(val context: Context) {
    companion object {
        @JvmStatic
        fun getInstance(context: Context): AlarmHelp {
            return AlarmHelp(context)
        }
    }
    
    // Delegate to enhanced AlarmManager
    private val alarmManager = AlarmManager.getInstance(context)
    
    fun updateData(alarmBean: AlarmBean) {
        alarmManager.updateData(alarmBean)
    }
    
    fun updateData(low: Float?, high: Float?, ringtone: Int?) {
        // Create AlarmBean from parameters for compatibility
        val alarmBean = AlarmBean().apply {
            lowTemp = low ?: Float.MIN_VALUE
            highTemp = high ?: Float.MAX_VALUE
            isLowOpen = low != null
            isHighOpen = high != null
            isRingtoneOpen = ringtone != null
            ringtoneType = ringtone ?: -1
        }
        alarmManager.updateData(alarmBean)
    }
    
    fun alarmData(realMax: Float, realMin: Float, tempLayout: TempLayout?) {
        // Enhanced alarm logic with visual feedback
        val shouldAlarm = alarmManager.checkTemperatureAlarm(realMax) || 
                         alarmManager.checkTemperatureAlarm(realMin)
        
        if (shouldAlarm) {
            tempLayout?.startAnimation(TempLayout.TYPE_HOT)
        } else {
            tempLayout?.stopAnimation()
        }
    }
    
    fun checkTemperatureAlarm(temperature: Float): Boolean {
        return alarmManager.checkTemperatureAlarm(temperature)
    }
    
    fun startMediaPlayAndStartListener() {
        alarmManager.startAlarm()
    }
    
    fun stopMediaPlayer() {
        alarmManager.stopAlarm()
    }
    
    fun pauseAlarm() {
        alarmManager.pauseAlarm()
    }
    
    fun pause() {
        alarmManager.pauseAlarm()
    }
    
    fun resumeAlarm() {
        alarmManager.resumeAlarm()
    }
    
    fun onResume() {
        alarmManager.resumeAlarm()
    }
    
    fun release() {
        alarmManager.release()
    }
    
    fun onDestroy(isSaveSetting: Boolean) {
        if (!isSaveSetting) {
            // Reset alarm settings if not saving
        }
        alarmManager.release()
    }
}