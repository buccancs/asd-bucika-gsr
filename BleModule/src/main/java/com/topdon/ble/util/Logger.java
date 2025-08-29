package com.topdon.ble.util;

/**
 * Logger - Simplified (logging removed)
 * date: 2019/8/2 23:56
 * author: bichuanfeng
 */
public interface Logger {
    /**
     * 一般的
     */
    int TYPE_GENERAL = 0;
    /**
     * 搜索状态
     */
    int TYPE_SCAN_STATE = 1;
    /**
     * 连接状态
     */
    int TYPE_CONNECTION_STATE = 2;
    /**
     * 读特征
     */
    int TYPE_CHARACTERISTIC_READ = 3;
    /**
     * 特征值变化
     */
    int TYPE_CHARACTERISTIC_CHANGED = 4;
    /**
     * 信号强度
     */
    int TYPE_READ_REMOTE_RSSI = 5;
    /**
     * 最大传输单元变化
     */
    int TYPE_MTU_CHANGED = 6;
    /**
     * 请求失败
     */
    int TYPE_REQUEST_FAILED = 7;
    int TYPE_DESCRIPTOR_READ = 8;
    int TYPE_NOTIFICATION_CHANGED = 9;
    int TYPE_INDICATION_CHANGED = 10;
    int TYPE_CHARACTERISTIC_WRITE = 11;
    int TYPE_PHY_CHANGE = 12;

    /**
     * 打印日志 - removed
     */
    void log(int priority, int type, String msg);

    /**
     * 打印日志 - removed
     */
    void log(int priority, int type, String msg, Throwable th);
    
    /**
     * 日志输出控制 - removed
     */
    void setEnabled(boolean isEnabled);

    /**
     * 日志输出是否使能 - removed
     */
    boolean isEnabled();
}
