package com.topdon.lib.core.alarm

import android.content.Context
import android.media.MediaPlayer
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.utils.SingletonHolder

/**
 * Enhanced alarm management system consolidating libcom AlarmHelp functionality.
 * Provides comprehensive temperature alarm handling with audio alerts and customizable thresholds.
 */
class AlarmManager private constructor(val context: Context) {
    companion object : SingletonHolder<AlarmManager, Context>(::AlarmManager)

    private var mediaPlayer: MediaPlayer? = null
    private var ringtoneResPosition = -1
    private var isOpenLowTemp = false
    private var isOpenHighTemp = false
    private var isTempAlarmRingtoneOpen = false
    private var maxTemp: Float = 0f
    private var minTemp: Float = 0f
    private var isPause = false
    private var alarmBean: AlarmBean? = null

    /**
     * Update alarm configuration with new settings.
     */
    fun updateData(alarmBean: AlarmBean) {
        this.alarmBean = alarmBean
        isTempAlarmRingtoneOpen = alarmBean.isRingtoneOpen
        isOpenLowTemp = alarmBean.isLowOpen
        isOpenHighTemp = alarmBean.isHighOpen
        ringtoneResPosition = alarmBean.ringtoneType
        maxTemp = alarmBean.highTemp
        minTemp = alarmBean.lowTemp
        
        if (isTempAlarmRingtoneOpen) {
            initializeMediaPlayer()
        } else {
            mediaPlayer = null
        }
    }
    
    private fun initializeMediaPlayer() {
        // Note: Ringtone resources would need to be available in libapp
        // This maintains the API while resources can be provided by implementing modules
        when (ringtoneResPosition) {
            0 -> mediaPlayer = createMediaPlayerFromResource("ringtone1")
            1 -> mediaPlayer = createMediaPlayerFromResource("ringtone2") 
            2 -> mediaPlayer = createMediaPlayerFromResource("ringtone3")
            3 -> mediaPlayer = createMediaPlayerFromResource("ringtone4")
            4 -> mediaPlayer = createMediaPlayerFromResource("ringtone5")
        }
        mediaPlayer?.isLooping = true
    }
    
    private fun createMediaPlayerFromResource(resourceName: String): MediaPlayer? {
        // Flexible resource loading - can be customized per implementation
        return try {
            val resourceId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resourceId != 0) {
                MediaPlayer.create(context, resourceId)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check temperature against alarm thresholds and trigger alarm if needed.
     */
    fun checkTemperatureAlarm(temperature: Float): Boolean {
        if (!isTempAlarmRingtoneOpen || isPause) return false
        
        val shouldAlarm = when {
            isOpenHighTemp && temperature > maxTemp -> true
            isOpenLowTemp && temperature < minTemp -> true
            else -> false
        }
        
        if (shouldAlarm) {
            startAlarm()
        } else {
            stopAlarm()
        }
        
        return shouldAlarm
    }

    /**
     * Start alarm sound if configured.
     */
    fun startAlarm() {
        if (!isTempAlarmRingtoneOpen || isPause) return
        
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                }
            }
        } catch (e: Exception) {
            // Handle media player errors gracefully
        }
    }

    /**
     * Stop alarm sound.
     */
    fun stopAlarm() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    player.seekTo(0)
                }
            }
        } catch (e: Exception) {
            // Handle media player errors gracefully
        }
    }

    /**
     * Pause alarm functionality.
     */
    fun pauseAlarm() {
        isPause = true
        stopAlarm()
    }

    /**
     * Resume alarm functionality.
     */
    fun resumeAlarm() {
        isPause = false
    }

    /**
     * Release media player resources.
     */
    fun release() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            // Handle release errors gracefully
        }
    }
}