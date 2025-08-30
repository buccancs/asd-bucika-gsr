package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.tools.ToastTools

/**
 * Enhanced temperature alarm settings dialog with comprehensive alarm configuration
 * Consolidated from libcom.dialog.TempAlarmSetDialog
 */
class EnhancedAlarmSetDialog(
    context: Context,
    private val isEdit: Boolean = false,
    private val layoutResId: Int = 0
) : Dialog(context), CompoundButton.OnCheckedChangeListener {

    var alarmBean = AlarmBean()
        set(value) {
            field = value.copy()
        }

    /**
     * Save button click listener
     */
    var onSaveListener: ((alarmBean: AlarmBean) -> Unit)? = null

    /**
     * Media player for ringtone preview
     */
    private var mediaPlayer: MediaPlayer? = null

    var hideAlarmMark = false

    // UI Components - will be initialized in initView()
    private var clRoot: View? = null
    private var clClose: View? = null
    private var tvSave: View? = null
    private var switchAlarmHigh: CompoundButton? = null
    private var switchAlarmLow: CompoundButton? = null
    private var switchAlarmMark: CompoundButton? = null
    private var switchAlarmRingtone: CompoundButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        
        val layoutToUse = if (layoutResId != 0) layoutResId else getDefaultLayoutRes()
        setContentView(LayoutInflater.from(context).inflate(layoutToUse, null))
        
        initView()

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.attributes = layoutParams
        }
    }

    private fun getDefaultLayoutRes(): Int {
        // Return a default layout resource ID - this would need to be implemented
        // based on the actual layout resources available
        return android.R.layout.simple_list_item_1 // placeholder
    }

    private fun initView() {
        // Initialize UI components - this would be implemented based on actual layout
        // This is a consolidated version that handles multiple layout types
        
        clRoot = findViewById(getResourceId("cl_root"))
        clClose = findViewById(getResourceId("cl_close"))
        tvSave = findViewById(getResourceId("tv_save"))
        switchAlarmHigh = findViewById(getResourceId("switch_alarm_high"))
        switchAlarmLow = findViewById(getResourceId("switch_alarm_low"))
        switchAlarmMark = findViewById(getResourceId("switch_alarm_mark"))
        switchAlarmRingtone = findViewById(getResourceId("switch_alarm_ringtone"))

        clRoot?.setOnClickListener { dismiss() }
        clClose?.setOnClickListener { dismiss() }
        tvSave?.setOnClickListener { save() }
        
        setupRingtoneButtons()
        setupSwitchListeners()
        setupColorMarkers()
        setupMarkTypeSelection()
    }

    private fun getResourceId(name: String): Int {
        return context.resources.getIdentifier(name, "id", context.packageName)
    }

    private fun setupRingtoneButtons() {
        for (i in 1..5) {
            findViewById<View>(getResourceId("iv_ringtone$i"))?.setOnClickListener { 
                selectRingtone(i - 1) 
            }
        }
    }

    private fun setupSwitchListeners() {
        switchAlarmHigh?.setOnCheckedChangeListener(this)
        switchAlarmLow?.setOnCheckedChangeListener(this)
        switchAlarmMark?.setOnCheckedChangeListener(this)
        switchAlarmRingtone?.setOnCheckedChangeListener(this)
    }

    private fun setupColorMarkers() {
        findViewById<View>(getResourceId("img_mark_high"))?.setOnClickListener {
            showColorDialog(true)
        }
        findViewById<View>(getResourceId("img_mark_low"))?.setOnClickListener {
            showColorDialog(false)
        }
    }

    private fun setupMarkTypeSelection() {
        findViewById<View>(getResourceId("iv_check_stoke"))?.setOnClickListener {
            val view = it
            if (!view.isSelected) {
                view.isSelected = true
                findViewById<View>(getResourceId("iv_check_matrix"))?.isSelected = false
                alarmBean.markType = AlarmBean.TYPE_ALARM_MARK_STROKE
            }
        }
        
        findViewById<View>(getResourceId("iv_check_matrix"))?.setOnClickListener {
            val view = it
            if (!view.isSelected) {
                findViewById<View>(getResourceId("iv_check_stoke"))?.isSelected = false
                view.isSelected = true
                alarmBean.markType = AlarmBean.TYPE_ALARM_MARK_MATRIX
            }
        }
    }

    override fun show() {
        super.show()
        refreshAlarmView()
    }

    private fun refreshAlarmView() {
        switchAlarmHigh?.isChecked = alarmBean.isHighOpen
        switchAlarmLow?.isChecked = alarmBean.isLowOpen
        switchAlarmMark?.isChecked = isEdit || alarmBean.isMarkOpen
        
        if (!isEdit) {
            switchAlarmRingtone?.isChecked = alarmBean.isRingtoneOpen
        }

        // Update mark type selection
        findViewById<View>(getResourceId("iv_check_stoke"))?.isSelected = 
            alarmBean.markType == AlarmBean.TYPE_ALARM_MARK_STROKE
        findViewById<View>(getResourceId("iv_check_matrix"))?.isSelected = 
            alarmBean.markType == AlarmBean.TYPE_ALARM_MARK_MATRIX

        // Update color indicators
        updateColorIndicators()
        updateTemperatureInputs()
        updateVisibility()
        updateRingtoneSelection()
    }

    private fun updateColorIndicators() {
        try {
            findViewById<View>(getResourceId("img_c_alarm_high"))?.let { view ->
                Glide.with(context).load(ColorDrawable(alarmBean.highColor)).into(view as android.widget.ImageView)
            }
            findViewById<View>(getResourceId("img_c_alarm_low"))?.let { view ->
                Glide.with(context).load(ColorDrawable(alarmBean.lowColor)).into(view as android.widget.ImageView)
            }
        } catch (e: Exception) {
            // Handle gracefully if Glide is not available
        }
    }

    private fun updateTemperatureInputs() {
        findViewById<android.widget.EditText>(getResourceId("et_alarm_high"))?.let { editText ->
            editText.isEnabled = switchAlarmHigh?.isChecked == true
            if (alarmBean.highTemp == Float.MAX_VALUE) {
                editText.setText("")
            } else {
                editText.setText(UnitTools.showUnitValue(alarmBean.highTemp).toString())
            }
        }

        findViewById<android.widget.EditText>(getResourceId("et_alarm_low"))?.let { editText ->
            editText.isEnabled = switchAlarmLow?.isChecked == true
            if (alarmBean.lowTemp == Float.MIN_VALUE) {
                editText.setText("")
            } else {
                editText.setText(UnitTools.showUnitValue(alarmBean.lowTemp).toString())
            }
        }

        // Update unit labels
        findViewById<android.widget.TextView>(getResourceId("tv_alarm_high_unit"))?.text = UnitTools.showUnit()
        findViewById<android.widget.TextView>(getResourceId("tv_alarm_low_unit"))?.text = UnitTools.showUnit()
    }

    private fun updateVisibility() {
        findViewById<View>(getResourceId("cl_alarm_mark"))?.isVisible = isEdit || (switchAlarmMark?.isChecked == true)
        findViewById<View>(getResourceId("cl_ringtone_select"))?.isVisible = !isEdit && (switchAlarmRingtone?.isChecked == true)
        findViewById<View>(getResourceId("tv_alarm_ringtone"))?.isVisible = !isEdit
        switchAlarmRingtone?.isVisible = !isEdit

        if (hideAlarmMark) {
            findViewById<View>(getResourceId("tv_alarm_mark"))?.visibility = View.GONE
            findViewById<View>(getResourceId("switch_alarm_mark"))?.visibility = View.GONE
            findViewById<View>(getResourceId("cl_alarm_mark"))?.visibility = View.GONE
        }
        
        findViewById<View>(getResourceId("switch_alarm_mark"))?.isVisible = !isEdit
    }

    private fun updateRingtoneSelection() {
        // Reset all ringtone selections
        for (i in 1..5) {
            findViewById<View>(getResourceId("iv_ringtone$i"))?.isSelected = false
        }
        
        // Set current selection
        val selectedRingtone = alarmBean.ringtoneType + 1
        if (selectedRingtone in 1..5) {
            findViewById<View>(getResourceId("iv_ringtone$selectedRingtone"))?.isSelected = true
        }
    }

    private fun save() {
        try {
            val etHigh = findViewById<android.widget.EditText>(getResourceId("et_alarm_high"))
            val etLow = findViewById<android.widget.EditText>(getResourceId("et_alarm_low"))
            
            val inputHigh = if (switchAlarmHigh?.isChecked == true) {
                if (!etHigh?.text.isNullOrEmpty()) UnitTools.showToCValue(etHigh!!.text.toString().toFloat()) else null
            } else null
            
            val inputLow = if (switchAlarmLow?.isChecked == true) {
                if (!etLow?.text.isNullOrEmpty()) UnitTools.showToCValue(etLow!!.text.toString().toFloat()) else null
            } else null
            
            if (inputHigh != null && inputLow != null && inputLow > inputHigh) {
                ToastTools.showShort("Invalid temperature range")
                return
            }
            
            // Update alarm bean with validated values
            alarmBean.highTemp = inputHigh ?: Float.MAX_VALUE
            alarmBean.lowTemp = inputLow ?: Float.MIN_VALUE
            alarmBean.isHighOpen = switchAlarmHigh?.isChecked == true
            alarmBean.isLowOpen = switchAlarmLow?.isChecked == true
            alarmBean.isRingtoneOpen = switchAlarmRingtone?.isChecked == true

            onSaveListener?.invoke(alarmBean)
            dismiss()
            
        } catch (e: Exception) {
            ToastTools.showShort("Invalid input format")
        }
    }

    private fun showColorDialog(isHigh: Boolean) {
        val currentColor = if (isHigh) alarmBean.highColor else alarmBean.lowColor
        val colorPickDialog = EnhancedColorDialog(context, currentColor)
        
        colorPickDialog.onColorPickedListener = { selectedColor ->
            if (isHigh) {
                alarmBean.highColor = selectedColor
            } else {
                alarmBean.lowColor = selectedColor
            }
            updateColorIndicators()
        }
        
        colorPickDialog.show()
    }

    override fun dismiss() {
        super.dismiss()
        stopMediaPlayer()
    }

    private fun stopMediaPlayer() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (e: Exception) {
            // Handle gracefully
        } finally {
            mediaPlayer = null
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            getResourceId("switch_alarm_high") -> {
                findViewById<android.widget.EditText>(getResourceId("et_alarm_high"))?.isEnabled = isChecked
                alarmBean.isHighOpen = isChecked
            }
            getResourceId("switch_alarm_low") -> {
                findViewById<android.widget.EditText>(getResourceId("et_alarm_low"))?.isEnabled = isChecked
                alarmBean.isLowOpen = isChecked
            }
            getResourceId("switch_alarm_mark") -> {
                findViewById<View>(getResourceId("cl_alarm_mark"))?.isVisible = isChecked
                alarmBean.isMarkOpen = isChecked
            }
            getResourceId("switch_alarm_ringtone") -> {
                findViewById<View>(getResourceId("cl_ringtone_select"))?.isVisible = isChecked
                if (isChecked) {
                    selectRingtone(alarmBean.ringtoneType)
                } else {
                    selectRingtone(null)
                }
            }
        }
    }

    /**
     * Select and play ringtone, null to stop playback
     */
    private fun selectRingtone(position: Int?) {
        stopMediaPlayer()
        
        if (position == null) return
        
        alarmBean.ringtoneType = position
        updateRingtoneSelection()
        
        try {
            val ringtoneRes = when (position) {
                0 -> getRingtoneResource("ringtone1")
                1 -> getRingtoneResource("ringtone2") 
                2 -> getRingtoneResource("ringtone3")
                3 -> getRingtoneResource("ringtone4")
                4 -> getRingtoneResource("ringtone5")
                else -> return
            }
            
            if (ringtoneRes != 0) {
                mediaPlayer = MediaPlayer.create(context, ringtoneRes)
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            // Handle gracefully if ringtone resources are not available
        }
    }

    private fun getRingtoneResource(name: String): Int {
        return context.resources.getIdentifier(name, "raw", context.packageName)
    }

    companion object {
        const val TYPE_HOT = 1
        const val TYPE_LT = 2
        const val TYPE_A = 3
    }
}