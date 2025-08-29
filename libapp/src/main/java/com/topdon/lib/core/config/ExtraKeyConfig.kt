package com.topdon.lib.core.config

import com.topdon.lib.core.repository.GalleryRepository

object ExtraKeyConfig {
    /**
     * boolean type - 跳转相册界面时，是否为生成report拾取图片操作.
     * true-生成report拾取图片 false-普通的相册浏览
     */
    const val IS_PICK_REPORT_IMG = "IS_PICK_REPORT_IMG"
    /**
     * boolean type - 是否为视频.
     * true-视频 false-图片
     */
    const val IS_VIDEO = "IS_VIDEO"
    /**
     * boolean type - 图库是否有返回箭头
     */
    const val HAS_BACK_ICON = "HAS_BACK_ICON"
    /**
     * boolean type - 图库是否可切换 有线设备、TS004、TC007 目录
     */
    const val CAN_SWITCH_DIR = "CAN_SWITCH_DIR"
    /**
     * boolean type - 设备type是否为 TC007.
     * true-TC007 false-其他
     */
    const val IS_TC007 = "IS_TC007"
    /**
     * boolean type - 是否拾取检测师签名.
     * true-检测师签名 false-房主签名
     */
    const val IS_PICK_INSPECTOR = "IS_PICK_INSPECTOR"
    /**
     * boolean type - 是否查看report
     * true-查看report false-生成report
     */
    const val IS_REPORT = "IS_REPORT"


    /**
     * Int type - 进入图库时初始的目录type 具体取值由 [GalleryRepository.DirType] 定义
     */
    const val DIR_TYPE = "CUR_DIR_TYPE"
    /**
     * Int type - 当前要查看的图片在图片列表中的 index.
     */
    const val CURRENT_ITEM = "CURRENT_ITEM"


    /**
     * Long type - 房屋检测模块：要编辑的检测 Id.
     */
    const val DETECT_ID = "DETECT_ID"
    /**
     * Long type - 房屋检测模块：要编辑的目录 Id.
     */
    const val DIR_ID = "DIR_ID"
    /**
     * Long type - ID.
     */
    const val LONG_ID = "LONG_ID"


    /**
     * String type - URL.
     */
    const val URL = "URL"
    /**
     * String type - 文件绝对路径.
     */
    const val FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH"
    /**
     * String type - 房屋检测一项 item 名称.
     */
    const val ITEM_NAME = "ITEM_NAME"

    /**
     * String type - 返回输入的文字内容.
     */
    const val RESULT_INPUT_TEXT = "RESULT_INPUT_TEXT"

    /**
     * String type - 拾取的图片在本地的绝对路径.
     */
    const val RESULT_IMAGE_PATH = "RESULT_IMAGE_PATH"
    /**
     * String type - 拾取的白色画笔版签名图片在本地的绝对路径.
     */
    const val RESULT_PATH_WHITE = "RESULT_PATH_WHITE"
    /**
     * String type - 拾取的黑色画笔版签名图片在本地的绝对路径.
     */
    const val RESULT_PATH_BLACK = "RESULT_PATH_BLACK"


    /**
     * List&lt;String&gt; type - 图片在本地绝对路径列表.
     */
    const val IMAGE_PATH_LIST = "IMAGE_PATH_LIST"


    /**
     * Parcelable type - 一张图片point line area信息封装 (ImageTempBean).
     */
    const val IMAGE_TEMP_BEAN = "IMAGE_TEMP_BEAN"
    /**
     * Parcelable type - 一份report所有信息 (ReportBean).
     */
    const val REPORT_BEAN = "REPORT_BEAN"
    /**
     * Parcelable type - report信息 (ReportInfoBean).
     */
    const val REPORT_INFO = "REPORT_INFO"
    /**
     * Parcelable type - 检测条件 (ReportConditionBean).
     */
    const val REPORT_CONDITION = "REPORT_CONDITION"

    /**
     * Parcelable type - 当前已添加的图片对应数据列表 (List<ReportIRBean>).
     */
    const val REPORT_IR_LIST = "REPORT_IR_LIST"


    /**
     * Parcelable type - 自定义渲染settings相关配置项 (CustomPseudoBean).
     */
    const val CUSTOM_PSEUDO_BEAN = "CUSTOM_PSEUDO_BEAN"

    /**
     * long type - Unix 时间戳，单位毫秒.
     */
    const val TIME_MILLIS = "TIME_MILLIS"


    /**
     * String type - 监控记录type.
     * 由于历史原因，此处使用 String表示：
     * point-点 line-线 fence-面
     */
    const val MONITOR_TYPE = "MONITOR_TYPE"


    const val IR_PATH = "ir_path"
    const val TEMP_HIGH = "temp_high"
    const val TEMP_LOW = "temp_low"

    const val IS_CAR_DETECT_ENTER = "IS_CAR_DETECT_ENTER"


}