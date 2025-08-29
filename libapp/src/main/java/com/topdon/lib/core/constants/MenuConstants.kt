package com.topdon.lib.core.constants

/**
 * Consolidated menu constants from libmenu.
 * Contains all menu types and UI constants used across different device types.
 */

/**
 * Device types that have different menu configurations.
 * 由于不同的设备（单光、双光、Lite、TC007、2D编辑）菜单存在差异，用该枚举区分.
 */
enum class MenuType {
    SINGLE_LIGHT,
    DOUBLE_LIGHT,
    Lite,
    TC007,
    GALLERY_EDIT,
}

/**
 * Temperature point types for observation mode.
 * 观测模式-菜单5-高低温点 菜单类型.
 */
enum class TempPointType {
    /** 高温点 */
    HIGH,
    /** 低温点 */
    LOW,
    /** 删除 */
    DELETE,
}

/**
 * Fence/boundary types for temperature monitoring.
 * 点、线、面、全图、趋势图(可选)、删除 菜单类型.
 */
enum class FenceType {
    /** 点 */
    POINT,
    /** 线 */
    LINE,
    /** 面 */
    RECT,
    /** 全图 */
    FULL,
    /** 趋势图 */
    TREND,
    /** 删除 */
    DEL,
}

/**
 * Setting/configuration types.
 * 测温模式-菜单5-设置/观测模式-菜单6-设置 菜单类型.
 */
enum class SettingType {
    /** 伪彩条 */
    PSEUDO_BAR,
    /** 对比度 */
    CONTRAST,
    /** 锐度（细节） */
    DETAIL,
    /** 旋转 */
    ROTATE,
    /** 镜像 */
    MIRROR,
    /** 警示 */
    ALARM,
    /** 字体 */
    FONT,
    /** 指南针（仅观测模式） */
    COMPASS,
    /** 水印（仅2D编辑） */
    WATERMARK,
}

/**
 * Target detection types for different objects.
 * 观测模式-菜单4-标靶 菜单类型.
 */
enum class TargetType {
    /** 测量模式：人(默认)、羊、狗、鸟 */
    MODE,
    /** 标靶风格 */
    STYLE,
    /** 标靶颜色 */
    COLOR,
    /** 删除 */
    DELETE,
    /** 帮助 */
    HELP,
}

/**
 * Dual light mode types.
 * 测温模式-菜单3-双光 菜单类型.
 */
enum class TwoLightType {
    /** 双光1 */
    TWO_LIGHT_1,
    /** 双光2 */
    TWO_LIGHT_2,
    /** 红外 */
    IR,
    /** 可见光 */
    LIGHT,
    /** 配准 */
    CORRECT,
    /** 画中画 */
    P_IN_P,
    /** 融合度 */
    BLEND_EXTENT,
}