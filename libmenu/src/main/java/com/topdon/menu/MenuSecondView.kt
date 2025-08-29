package com.topdon.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.menu.constant.MenuType
import com.topdon.menu.adapter.ColorAdapter
import com.topdon.menu.adapter.FenceAdapter
import com.topdon.menu.adapter.SettingAdapter
import com.topdon.menu.adapter.TargetAdapter
import com.topdon.menu.adapter.TempLevelAdapter
import com.topdon.menu.adapter.TempPointAdapter
import com.topdon.menu.adapter.TempSourceAdapter
import com.topdon.menu.adapter.TwoLightAdapter
import com.topdon.menu.constant.FenceType
import com.topdon.menu.constant.SettingType
import com.topdon.menu.constant.TargetType
import com.topdon.menu.constant.TempPointType
import com.topdon.menu.constant.TwoLightType
import com.topdon.menu.databinding.ViewMenuSecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * menu
 */
@SuppressLint("NotifyDataSetChanged")
class MenuSecondView : FrameLayout {
    /**
     * edit
     */
    private val menuType: MenuType

    private lateinit var binding: ViewMenuSecondBinding


 /* ********************************************* public ********************************************* */
    /**
 * : 0-> photo 10->photo
     *
 * : 1-> point line area
     *
 * 11->AI
     *
 * : 2-> dual light
 * 13->target
     *
 * : 3-> pseudo color 12->pseudo color
     *
 * : 4-> settings
     *
 * 15->settings
     *
     * temperature
     *
 * 14->high low temperature point
     */
    fun selectPosition(position: Int) {
        binding.cameraMenuView.isVisible = position == 0 || position == 10
        binding.recyclerFence.isVisible = position == 1
        binding.recyclerTwoLight.isVisible = position == 2
        binding.recyclerColor.isVisible = position == 3 || position == 12
        binding.recyclerSettingTe.isVisible = position == 4
        binding.recyclerTempLevel.isVisible = position == 5

        binding.recyclerTempSource.isVisible = position == 11
        binding.recyclerTarget.isVisible = position == 13
        binding.recyclerTempPoint.isVisible = position == 14
        binding.recyclerSettingOb.isVisible = position == 15
    }



 /* ********************************************* public ********************************************* */

    /**
     * event
     * gallery
     */
    var onCameraClickListener: ((actionCode: Int) -> Unit)?
        get() = binding.cameraMenuView.onCameraClickListener
        set(value) {
            binding.cameraMenuView.onCameraClickListener = value
        }
    /**
     * event
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)?
        get() = fenceAdapter.onFenceListener
        set(value) {
            fenceAdapter.onFenceListener = value
        }
    /**
     * event
     * medium
     */
    var onTwoLightListener: ((twoLightType: TwoLightType, isSelected: Boolean) -> Unit)?
        get() = twoLightAdapter.onTwoLightListener
        set(value) {
            twoLightAdapter.onTwoLightListener = value
        }
    /**
     * event
     * medium
 * code-pseudo color index TC007 
 * size-pseudo color TC007 
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)?
        get() = colorAdapter.onColorListener
        set(value) {
            colorAdapter.onColorListener = value
        }
    /**
     * event
     * medium
     * high
     * medium
     */
    var onSettingListener: ((type: SettingType, isSelected: Boolean) -> Unit)?
        get() = settingTeAdapter.onSettingListener
        set(value) {
            settingTeAdapter.onSettingListener = value
            settingObAdapter.onSettingListener = value
        }
    /**
     * event
     *
     * medium
 * - -1
     * high
     * high
     */
    var onTempLevelListener: ((code: Int) -> Unit)?
        get() = tempLevelAdapter.onTempLevelListener
        set(value) {
            tempLevelAdapter.onTempLevelListener = value
        }


    /**
     * event
     *
     * medium
     * medium
 * - 0
     * high
     * low
     */
    var onTempSourceListener: ((code: Int) -> Unit)?
        get() = tempSourceAdapter.onTempSourceListener
        set(value) {
            tempSourceAdapter.onTempSourceListener = value
        }
    /**
     * event
     */
    var onTargetListener: ((targetType: TargetType) -> Unit)?
        get() = targetAdapter.onTargetListener
        set(value) {
            targetAdapter.onTargetListener = value
        }
    /**
     * event
     */
    var onTempPointListener: ((type: TempPointType, isSelected: Boolean) -> Unit)?
        get() = tempPointAdapter.onTempPointListener
        set(value) {
            tempPointAdapter.onTempPointListener = value
        }





    /**
     * Temperature measurement mode-menu2-point line area used for Adapter.
     */
    private val fenceAdapter: FenceAdapter
    /**
     * Temperature measurement mode-menu3-dual light used for Adapter.
     */
    private val twoLightAdapter: TwoLightAdapter
    /**
     * Temperature measurement mode-menu4-pseudo color or Observation mode-menu3-pseudo color used for Adapter.
     */
    private val colorAdapter = ColorAdapter()
    /**
     * Temperature measurement mode-menu5-settings used for Adapter.
     */
    private val settingTeAdapter: SettingAdapter
    /**
     * Temperature measurement mode-menu6-high low temperature level used for Adapter.
     */
    private val tempLevelAdapter: TempLevelAdapter


    /**
     * Observation mode-menu2-high low temperature source used for Adapter.
     */
    private val tempSourceAdapter = TempSourceAdapter()
    /**
     * Observation mode-menu4-target used for Adapter.
     */
    private val targetAdapter = TargetAdapter()
    /**
     * Observation mode-menu5-high low temperature point used for Adapter.
     */
    private val tempPointAdapter = TempPointAdapter()
    /**
     * Observation mode-menu6-settings used for Adapter.
     */
    private val settingObAdapter = SettingAdapter(isObserver = true)







    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuSecondView, defStyleAttr, defStyleRes)
        menuType = when (typedArray.getInt(R.styleable.MenuSecondView_deviceType, 0)) {
            0 -> MenuType.SINGLE_LIGHT
            1 -> MenuType.DOUBLE_LIGHT
            2 -> MenuType.Lite
            4 -> MenuType.GALLERY_EDIT
            else -> MenuType.TC007
        }
        typedArray.recycle()

        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.view_menu_second, this, true)
            fenceAdapter = FenceAdapter(menuType)
            twoLightAdapter = TwoLightAdapter(menuType)
            settingTeAdapter = SettingAdapter(menuType)
            tempLevelAdapter = TempLevelAdapter(menuType)
        } else {
            binding = ViewMenuSecondBinding.inflate(LayoutInflater.from(context), this, true)

            refreshImg(GalleryRepository.DirType.LINE)

 // Temperature measurement mode-menu2-point line area menu
            fenceAdapter = FenceAdapter(menuType)
            binding.recyclerFence.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerFence.adapter = fenceAdapter

 // Temperature measurement mode-menu3-dual light menu
            twoLightAdapter = TwoLightAdapter(menuType)
            binding.recyclerTwoLight.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTwoLight.adapter = twoLightAdapter

 // Temperature measurement mode-menu4-pseudo color or Observation mode-menu3-pseudo color menu
            binding.recyclerColor.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerColor.adapter = colorAdapter

 // Temperature measurement mode-menu5-settings menu
            settingTeAdapter = SettingAdapter(menuType)
            binding.recyclerSettingTe.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerSettingTe.adapter = settingTeAdapter

 // Temperature measurement mode-menu6-high low temperature level menu
            tempLevelAdapter = TempLevelAdapter(menuType)
            binding.recyclerTempLevel.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempLevel.adapter = tempLevelAdapter



 // Observation mode-menu2-high low temperature source menu
            binding.recyclerTempSource.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempSource.adapter = tempSourceAdapter

 // Observation mode-menu4-target menu
            binding.recyclerTarget.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTarget.adapter = targetAdapter

 // Observation mode-menu5-high low temperature point menu
            binding.recyclerTempPoint.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempPoint.adapter = tempPointAdapter

 // Observation mode-menu6-settings menu
            binding.recyclerSettingOb.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerSettingOb.adapter = settingObAdapter
        }
    }


    /* *********************************************  menu1-photovideo  ********************************************* */
    /**
 * videomode.
     *
     * true-videomode false-photomode
     */
    var isVideoMode: Boolean
        get() = binding.cameraMenuView.isVideoMode
        set(value) {
            binding.cameraMenuView.isVideoMode = value
        }

    /**
 * TS001/ delayphotophotovideophoto.
     */
    fun switchToCamera() {
        binding.cameraMenuView.canSwitchMode = true
        binding.cameraMenuView.isVideoMode = false
        binding.cameraMenuView.setToNormal()
    }

    /**
     * class
     * medium
     */
    fun updateCameraModel() {
        binding.cameraMenuView.canSwitchMode = true
        binding.cameraMenuView.setToNormal()
    }

    fun refreshImg(type: GalleryRepository.DirType = GalleryRepository.DirType.LINE) {
 updateCameraModel()//
        CoroutineScope(Dispatchers.IO).launch {
            val path = GalleryRepository.readLatest(type)
            launch(Dispatchers.Main) {
                binding.cameraMenuView.refreshGallery(path)
            }
        }
    }

    /**
     * medium
     */
    fun setToRecord(isDelay: Boolean) {
        binding.cameraMenuView.canSwitchMode = false
        binding.cameraMenuView.setToRecord(isDelay)
    }

    /**
     * medium
     */
    fun setToCamera() {
        binding.cameraMenuView.setToRecord(false)
    }


    /* *****************************************  Temperature measurement mode-menu2-point line area  ***************************************** */
    /**
     * medium
     */
    var fenceSelectType: FenceType?
        get() = fenceAdapter.selectType
        set(value) {
            fenceAdapter.selectType = value
        }


    /* *****************************************  Temperature measurement mode-menu3-dual light  ***************************************** */
    /**
 * dual lighttype
 * - 
 * - Lite 
 * - dual light dual light1dual light2
     * medium
     */
    var twoLightType: TwoLightType
        get() = twoLightAdapter.twoLightType
        set(value) {
            twoLightAdapter.twoLightType = value
        }

    /**
 * settingsdual light
     * medium
     * medium
     * medium
 * - TC007
     */
    fun setTwoLightSelected(twoLightType: TwoLightType, isSelected: Boolean) {
        twoLightAdapter.setSelected(twoLightType, isSelected)
    }


    /* **********************************  Temperature measurement mode-menu4-pseudo color/Observation mode-menu3-pseudo color  ********************************** */
    /**
     * medium
 * @param code 1- 3- 4-1 5-2 6-3 7- 8- 9-4 10-5 11-
     */
    fun setPseudoColor(code: Int) {
        colorAdapter.selectCode = code
    }


    /* **********************************  Temperature measurement mode-menu5-settings or Observation mode-menu6-settings  ********************************** */
    /**
     * medium
     */
    fun setSettingSelected(settingType: SettingType, isSelected: Boolean) {
        settingTeAdapter.setSelected(settingType, isSelected)
        settingObAdapter.setSelected(settingType, isSelected)
    }

    /**
     * rotation angle
     * rotation angle
     */
    fun setSettingRotate(rotateAngle: Int) {
        settingTeAdapter.rotateAngle = rotateAngle
        settingObAdapter.rotateAngle = rotateAngle
    }


    /* *****************************************  Temperature measurement mode-menu6-high low temperature level  ***************************************** */
    /**
     * temperature
     *
 * true- false-
     */
    var isUnitF: Boolean
        get() = tempLevelAdapter.isUnitF
        set(value) {
            tempLevelAdapter.isUnitF = value
        }
    /**
     * temperature
     *
     * medium
 * - -1
     * high
     * high
     */
    fun setTempLevel(code: Int) {
        tempLevelAdapter.selectCode = code
    }






    /* *****************************************  Observation mode-menu2-high low temperature source  ***************************************** */
    /**
     * medium
     *
     * medium
     * medium
 * - 0
     * high
     * low
     */
    fun setTempSource(code: Int) {
        tempSourceAdapter.selectCode = code
    }


    /* *****************************************  Observation mode-menu4-target  ***************************************** */
    /**
     * medium
     */
    fun setTargetSelected(targetType: TargetType, isSelected: Boolean) {
        targetAdapter.setSelected(targetType, isSelected)
    }
    /**
     * measurement
     *
     * medium
 * - 10
 * - 11
 * - 12
 * - 13
     */
    fun setTargetMode(modeCode: Int) {
        targetAdapter.setTargetMode(modeCode)
    }

    
    /* *****************************************  Observation mode-menu5-high low temperature point  ***************************************** */
    /**
     * low
     */
    fun setTempPointSelect(tempPointType: TempPointType, isSelected: Boolean) {
        tempPointAdapter.setSelected(tempPointType, isSelected)
    }
    /**
     * medium
     * medium
     */
    fun clearTempPointSelect() {
        tempPointAdapter.clearAllSelect()
    }
}