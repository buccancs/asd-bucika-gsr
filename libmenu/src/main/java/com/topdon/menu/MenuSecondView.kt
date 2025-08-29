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
 * 二级menu
 */
@SuppressLint("NotifyDataSetChanged")
class MenuSecondView : FrameLayout {
    /**
     * 该menu的type，由于不同的设备（单光、dual light、Lite、TC007、2D编辑）menu存在差异，用该枚举区分.
     */
    private val menuType: MenuType

    private lateinit var binding: ViewMenuSecondBinding


    /* *********************************************  public 方法  ********************************************* */
    /**
     * 测温: 0-> photo      观测 10->photo
     *
     * 测温: 1-> point line area
     *
     *                    观测 11->AI识别
     *
     * 测温: 2-> dual light
     *                    观测 13->target
     *
     * 测温: 3-> pseudo color颜色   观测 12->pseudo color颜色
     *
     * 测温: 4-> settings
     *
     *                    观测 15->settings
     *
     * 测温: 5-> 温度档位
     *
     *                    观测 14->high low temperature point
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



    /* *********************************************  public 属性  ********************************************* */

    /**
     * 第 1 个menu-photovideo 各个操作的点击事件监听.
     * actionCode: 0-photo/video  1-图库  2-更多menu  3-切换到photo  4-切换到video
     */
    var onCameraClickListener: ((actionCode: Int) -> Unit)?
        get() = binding.cameraMenuView.onCameraClickListener
        set(value) {
            binding.cameraMenuView.onCameraClickListener = value
        }
    /**
     * Temperature measurement mode-menu2-point line area 切换事件监听。
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)?
        get() = fenceAdapter.onFenceListener
        set(value) {
            fenceAdapter.onFenceListener = value
        }
    /**
     * Temperature measurement mode-menu3-dual light 点击事件监听。
     * isSelected: true-切换为选中 false-切换为未选中
     */
    var onTwoLightListener: ((twoLightType: TwoLightType, isSelected: Boolean) -> Unit)?
        get() = twoLightAdapter.onTwoLightListener
        set(value) {
            twoLightAdapter.onTwoLightListener = value
        }
    /**
     * Temperature measurement mode-menu4-pseudo color/Observation mode-menu3-pseudo color pseudo color切换事件监听.
     * index-选中pseudo color在列表中的 index，也就 TC007 要用
     * code-pseudo color编码，由于历史遗留跟 index 对不上，非 TC007 时使用
     * size-预设pseudo color数量，也就 TC007 要用
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)?
        get() = colorAdapter.onColorListener
        set(value) {
            colorAdapter.onColorListener = value
        }
    /**
     * Temperature measurement mode-menu5-settings/Observation mode-menu6-settings 点击事件监听.
     * isSelected: true-点击时为选中状态 false-点击时为未选中状态
     * 警示、字体、水印是以生效才视为高亮选中的，这里先保持旧代码逻辑，
     * settingsmenu的选中刷新丢给上层的 listener 去做，后面有空再考虑更改
     */
    var onSettingListener: ((type: SettingType, isSelected: Boolean) -> Unit)?
        get() = settingTeAdapter.onSettingListener
        set(value) {
            settingTeAdapter.onSettingListener = value
            settingObAdapter.onSettingListener = value
        }
    /**
     * Temperature measurement mode-menu6-high low temperature level 点击事件监听.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 自动切换：-1
     * - 高温(低增益)：0
     * - 常温(高增益)：1
     */
    var onTempLevelListener: ((code: Int) -> Unit)?
        get() = tempLevelAdapter.onTempLevelListener
        set(value) {
            tempLevelAdapter.onTempLevelListener = value
        }


    /**
     * Observation mode-menu2-high low temperature source 点击事件监听.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 什么都未选中：-1
     * - 动态识别：0
     * - 高温源：1
     * - 低温源：2
     */
    var onTempSourceListener: ((code: Int) -> Unit)?
        get() = tempSourceAdapter.onTempSourceListener
        set(value) {
            tempSourceAdapter.onTempSourceListener = value
        }
    /**
     * Observation mode-menu4-target 点击事件监听.
     */
    var onTargetListener: ((targetType: TargetType) -> Unit)?
        get() = targetAdapter.onTargetListener
        set(value) {
            targetAdapter.onTargetListener = value
        }
    /**
     * Observation mode-menu5-high low temperature point 点击事件监听.
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

            //初始化 Temperature measurement mode-menu2-point line area menu
            fenceAdapter = FenceAdapter(menuType)
            binding.recyclerFence.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerFence.adapter = fenceAdapter

            //初始化 Temperature measurement mode-menu3-dual light menu
            twoLightAdapter = TwoLightAdapter(menuType)
            binding.recyclerTwoLight.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTwoLight.adapter = twoLightAdapter

            //初始化 Temperature measurement mode-menu4-pseudo color or Observation mode-menu3-pseudo color menu
            binding.recyclerColor.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerColor.adapter = colorAdapter

            //初始化 Temperature measurement mode-menu5-settings menu
            settingTeAdapter = SettingAdapter(menuType)
            binding.recyclerSettingTe.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerSettingTe.adapter = settingTeAdapter

            //初始化 Temperature measurement mode-menu6-high low temperature level menu
            tempLevelAdapter = TempLevelAdapter(menuType)
            binding.recyclerTempLevel.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempLevel.adapter = tempLevelAdapter



            //初始化 Observation mode-menu2-high low temperature source menu
            binding.recyclerTempSource.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempSource.adapter = tempSourceAdapter

            //初始化 Observation mode-menu4-target menu
            binding.recyclerTarget.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTarget.adapter = targetAdapter

            //初始化 Observation mode-menu5-high low temperature point menu
            binding.recyclerTempPoint.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempPoint.adapter = tempPointAdapter

            //初始化 Observation mode-menu6-settings menu
            binding.recyclerSettingOb.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerSettingOb.adapter = settingObAdapter
        }
    }


    /* *********************************************  menu1-photovideo  ********************************************* */
    /**
     * 当前是否处于videomode.
     *
     * true-videomode false-photomode
     */
    var isVideoMode: Boolean
        get() = binding.cameraMenuView.isVideoMode
        set(value) {
            binding.cameraMenuView.isVideoMode = value
        }

    /**
     * 仅 TS001，测温/观测 切换时，关闭delayphoto、连续photo、video后，需要重置为photo状态.
     */
    fun switchToCamera() {
        binding.cameraMenuView.canSwitchMode = true
        binding.cameraMenuView.isVideoMode = false
        binding.cameraMenuView.setToNormal()
    }

    /**
     * 类似重置，这个方法的目的是重置状态为未photo、未video状态，且放开 photo/video 切换.
     * 在各个thermal imaging Activity 的 start()，以及当前 View 中调用
     */
    fun updateCameraModel() {
        binding.cameraMenuView.canSwitchMode = true
        binding.cameraMenuView.setToNormal()
    }

    fun refreshImg(type: GalleryRepository.DirType = GalleryRepository.DirType.LINE) {
        updateCameraModel()//恢复状态
        CoroutineScope(Dispatchers.IO).launch {
            val path = GalleryRepository.readLatest(type)
            launch(Dispatchers.Main) {
                binding.cameraMenuView.refreshGallery(path)
            }
        }
    }

    /**
     * 将中间 photo/video 按钮settings为 photo中-立即/photo中-延迟/video中
     */
    fun setToRecord(isDelay: Boolean) {
        binding.cameraMenuView.canSwitchMode = false
        binding.cameraMenuView.setToRecord(isDelay)
    }

    /**
     * 将中间 photo/video 按钮settings为 photo中-立即 状态
     */
    fun setToCamera() {
        binding.cameraMenuView.setToRecord(false)
    }


    /* *****************************************  Temperature measurement mode-menu2-point line area  ***************************************** */
    /**
     * Temperature measurement mode-menu2-point line area 当前选中的menutype，若为 null 表示所有都未选中.
     */
    var fenceSelectType: FenceType?
        get() = fenceAdapter.selectType
        set(value) {
            fenceAdapter.selectType = value
        }


    /* *****************************************  Temperature measurement mode-menu3-dual light  ***************************************** */
    /**
     * 当前单选的dual lighttype
     * - 单光：  不应该使用这个属性
     * - Lite： 不应该使用这个属性
     * - dual light：  dual light1、dual light2、红外、可见光
     * - TC007：dual light、红外、可见光、画中画
     */
    var twoLightType: TwoLightType
        get() = twoLightAdapter.twoLightType
        set(value) {
            twoLightAdapter.twoLightType = value
        }

    /**
     * settingsdual light多选状态
     * - 单光：  画中画、融合度
     * - Lite： 画中画、融合度
     * - dual light：  配准、画中画、融合度
     * - TC007：配准、、融合度
     */
    fun setTwoLightSelected(twoLightType: TwoLightType, isSelected: Boolean) {
        twoLightAdapter.setSelected(twoLightType, isSelected)
    }


    /* **********************************  Temperature measurement mode-menu4-pseudo color/Observation mode-menu3-pseudo color  ********************************** */
    /**
     * 根据指定的pseudo color代号，选中pseudo colormenu中的指定pseudo color，若传递的 code 为不支持 code，则为全部未选中效果。
     * @param code 1-白热 3-铁红 4-彩虹1 5-彩虹2 6-彩虹3 7-红热 8-热铁 9-彩虹4 10-彩虹5 11-黑热
     */
    fun setPseudoColor(code: Int) {
        colorAdapter.selectCode = code
    }


    /* **********************************  Temperature measurement mode-menu5-settings or Observation mode-menu6-settings  ********************************** */
    /**
     * settingssettingsmenu中指定选项的选中状态
     */
    fun setSettingSelected(settingType: SettingType, isSelected: Boolean) {
        settingTeAdapter.setSelected(settingType, isSelected)
        settingObAdapter.setSelected(settingType, isSelected)
    }

    /**
     * settingssettingsmenu中旋转选项的angle
     * @param rotateAngle 注意！这个值是机芯的旋转angle，非 UI 旋转angle
     */
    fun setSettingRotate(rotateAngle: Int) {
        settingTeAdapter.rotateAngle = rotateAngle
        settingObAdapter.rotateAngle = rotateAngle
    }


    /* *****************************************  Temperature measurement mode-menu6-high low temperature level  ***************************************** */
    /**
     * 温度档位是否使用华氏度作为单位
     *
     * true-华氏度 false-摄氏度
     */
    var isUnitF: Boolean
        get() = tempLevelAdapter.isUnitF
        set(value) {
            tempLevelAdapter.isUnitF = value
        }
    /**
     * settings Temperature measurement mode-menu6-high low temperature level 温度档位.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 自动切换：-1
     * - 高温(低增益)：0
     * - 常温(高增益)：1
     */
    fun setTempLevel(code: Int) {
        tempLevelAdapter.selectCode = code
    }






    /* *****************************************  Observation mode-menu2-high low temperature source  ***************************************** */
    /**
     * settings Observation mode-menu2-high low temperature source 选中.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 什么都未选中：-1
     * - 动态识别：0
     * - 高温源：1
     * - 低温源：2
     */
    fun setTempSource(code: Int) {
        tempSourceAdapter.selectCode = code
    }


    /* *****************************************  Observation mode-menu4-target  ***************************************** */
    /**
     * settings Observation mode-menu4-target 指定选项的选中状态
     */
    fun setTargetSelected(targetType: TargetType, isSelected: Boolean) {
        targetAdapter.setSelected(targetType, isSelected)
    }
    /**
     * settings Observation mode-menu4-target-测量mode 图标type.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 人：10
     * - 羊：11
     * - 狗：12
     * - 鸟：13
     */
    fun setTargetMode(modeCode: Int) {
        targetAdapter.setTargetMode(modeCode)
    }

    
    /* *****************************************  Observation mode-menu5-high low temperature point  ***************************************** */
    /**
     * settings Observation mode-menu5-high low temperature point menu中，High temperature point 或 低稳点 的选中状态。
     */
    fun setTempPointSelect(tempPointType: TempPointType, isSelected: Boolean) {
        tempPointAdapter.setSelected(tempPointType, isSelected)
    }
    /**
     * 清除 Observation mode-menu5-high low temperature point menu的所有选中状态。
     * 这里维持原有逻辑，后续考虑是否直接给选中delete得了。
     */
    fun clearTempPointSelect() {
        tempPointAdapter.clearAllSelect()
    }
}