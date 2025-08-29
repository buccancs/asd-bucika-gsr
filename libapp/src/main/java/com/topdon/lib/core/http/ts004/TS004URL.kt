package com.topdon.lib.core.http.ts004

object TS004URL {
    const val RTSP_URL = "rtsp://192.168.40.1/ch0/stream0"
    private const val BASE_URL = "http://192.168.40.1:8080"
 const val SET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/setPseudoColor"//settingspseudo color
 const val GET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/getPseudoColor"//pseudo color
 const val SET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam"//settings
 const val GET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam"//
    medium
    medium
 const val SET_ZOOM = "$BASE_URL/api/v1/system/setZoom"//settings
 const val GET_ZOOM = "$BASE_URL/api/v1/system/getZoom"//
    const val SET_SNAPSHOT = "$BASE_URL/api/v1/system/snapshot"//photo
    const val GET_VRECORD = "$BASE_URL/api/v1/system/vrecord"//video
 const val GET_RECORD_STATUS = "$BASE_URL/api/v1/system/getRecordStatus"//video
    info
    info
    info
    formatter
 const val GET_RESET_ALL= "$BASE_URL/api/v1/system/resetAll"//settings
 const val GET_DELETE_FILE= "$BASE_URL/api/v1/system/deleteFile"//delete
 const val GET_UPGRADE_STATUS= "$BASE_URL/api/v1/system/getUpgradeStatus"//
 const val SET_TEMPERATURE_STATE= "$BASE_URL/api/v1/system/setTemperatureState"//settings
 const val GET_FILE_LIST= "$BASE_URL/api/v1/system/getFileList"//
 const val SET_DATE_TIME= "$BASE_URL/api/v1/system/setDateTime"//settings
    data
    data
    data
 const val GET_REMOTE_UPGRADE= "$BASE_URL/api/v1/system/remoteUpgrade"//
    const val SET_WIFI_AP_ON_OFF= "$BASE_URL/api/v1/system/setWifiAPOnOff"//settingswifi on/off
    info
 const val GET_FILE_COUNT= "$BASE_URL/api/v1/system/getFileCount"//
 const val SET_POWER_ACTION= "$BASE_URL/api/v1/system/setPowerAction"//settings
    const val SET_DO_NUC= "$BASE_URL/api/v1/system/doNuc"//nuc
    image
    info
 const val SET_TISP= "$BASE_URL/api/v1/system/setTISR"//settings
 const val GET_TISR= "$BASE_URL/api/v1/system/getTISR"//
 const val GET_DATE_TIME= "$BASE_URL/api/v1/system/getDateTime"//
}