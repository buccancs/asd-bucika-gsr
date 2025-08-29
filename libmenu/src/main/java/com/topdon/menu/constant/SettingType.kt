package com.topdon.menu.constant

/**
 * Temperature measurement mode-menu5-settings/Observation mode-menu6-settings menutype.
 *
 * Created by LCG on 2024/11/28.
 */
enum class SettingType {
    /** pseudo color条 */
    PSEUDO_BAR,

    /** 对比度 */
    CONTRAST,

    /** 锐度（细节） */
    DETAIL,

    /** 旋转 */
    ROTATE,

    /** mirror */
    MIRROR,

    /** 警示 */
    ALARM,

    /** 字体 */
    FONT,

    /** 指南针（仅Observation mode） */
    COMPASS,

    /** 水印（仅2D编辑） */
    WATERMARK,
}