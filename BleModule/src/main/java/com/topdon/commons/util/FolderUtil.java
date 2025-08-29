package com.topdon.commons.util;

import android.text.TextUtils;

import com.topdon.lms.sdk.LMS;

import java.io.File;

public class FolderUtil {
    public static String mPath = "/data/user/0/com.topdon.diag.artidiag/files";
    public static String mUserId;
    public static String fileName;
    public static String tdartsSn;

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String mfileName) {
        fileName = mfileName;
    }

    public static void setUserId(String userId) {
        mUserId = userId;
    }


    public static void init() {
        mUserId = PreUtil.getInstance(Topdon.getApp()).get("VCI_" + LMS.getInstance().getLoginName());
        setUserId(mUserId);
        mPath = Topdon.getApp().getExternalFilesDir("").getAbsolutePath();
        initPath();
    }

    public static void initTDarts(String tdSn) {
        tdartsSn = tdSn;
        String mPath = Topdon.getApp().getExternalFilesDir("").getAbsolutePath();
        if (!TextUtils.isEmpty(tdSn)) {
            File rfidFile = new File(mPath + fileName + tdSn + "/RFID/");
            if (!rfidFile.exists()) {
                rfidFile.mkdirs();
            }
        }
    }

    public static void initFilePath() {
        String basePath = Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName;
        String downPath = basePath + "Download/";
        File file = new File(downPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static void initPath() {
        if (!TextUtils.isEmpty(mUserId)) {

            String basePath = mPath + fileName;
            String userPath = basePath + mUserId;
            
            // Diagnosis directories
            ensureDirectoryExists(userPath + "/Diagnosis/Asia/");
            ensureDirectoryExists(userPath + "/Diagnosis/Europe/");
            ensureDirectoryExists(userPath + "/Diagnosis/America/");
            ensureDirectoryExists(userPath + "/Diagnosis/China/");
            ensureDirectoryExists(userPath + "/Diagnosis/Public/");

            // Immo directories
            ensureDirectoryExists(userPath + "/Immo/Asia/");
            ensureDirectoryExists(userPath + "/Immo/Europe/");
            ensureDirectoryExists(userPath + "/Immo/America/");
            ensureDirectoryExists(userPath + "/Immo/China/");
            ensureDirectoryExists(userPath + "/Immo/Australia/");

            // Other user-specific directories
            ensureDirectoryExists(userPath + "/RFID/");
            ensureDirectoryExists(userPath + "/NewEnergy/");
            ensureDirectoryExists(userPath + "/Shot/");
            ensureDirectoryExists(userPath + "/Pdf/");
            ensureDirectoryExists(userPath + "/Datastream/");
            ensureDirectoryExists(userPath + "/Gallery/");
            ensureDirectoryExists(userPath + "/DataLog/");
            ensureDirectoryExists(userPath + "/History/Diagnose/");
            ensureDirectoryExists(userPath + "/History/Service/");
            ensureDirectoryExists(userPath + "/FeedbackLog/");
            ensureDirectoryExists(userPath + "/autovinLog/");
            ensureDirectoryExists(userPath + "Download/");

            // Global directories
            ensureDirectoryExists(basePath + "App/");
            ensureDirectoryExists(basePath + "Firmware/");
            ensureDirectoryExists(basePath + "T-darts/");
            ensureDirectoryExists(basePath + "UserData/Diagnose/");
            ensureDirectoryExists(basePath + "UserData/Immo/");
            ensureDirectoryExists(basePath + "UserData/NewEnergy/");
            ensureDirectoryExists(basePath + "UserData/RFID/");
        }
    }

    public static String getOtaPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + "/s/";
    }


    public static String getDataBasePath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName;
    }
  
    public static String getTDartsRootPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + tdartsSn + "/";
    }

    public static String getRootPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/";
    }

    public static String getVehiclesPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/";
    }

    public static String getImmoPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Immo/";
    }

    /**
     * 获取Tdarts sn下车型软件包路径
     *
     * @return str
     */
    public static String getRfidTopScanPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + tdartsSn + "/RFID/";
    }

    public static String getRfidPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/RFID/";
    }

    public static String getAsiaPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/Asia/";
    }

    public static String getAmericaPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/America/";
    }

    public static String getEuropePath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/Europe/";
    }

    public static String getVehiclePublicPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/Public/";
    }

    public static String getVehicleTopScanPublicPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName;
    }


    public static String getShotPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Shot/";
    }

    public static String getDataStreamPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Datastream/";
    }

    public static String getPdfPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Pdf/";
    }

    public static String getAppPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "App/";
    }

    public static String getFirmwarePath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Firmware/";
    }

    public static String getTdartsUpgradePath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "T-darts/";
    }

    public static String getDownloadPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Download/";
    }

    public static String getDiagHistoryPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/History/Diagnose/";
    }

    public static String getServiceHistoryPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/History/Service/";
    }

    public static String getLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Log/";
    }

    public static String getSoLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Log/SoLog/";
    }

    public static String getGalleryPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Gallery/";
    }

    public static String getDataLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/DataLog/";
    }

    public static String getDiagDataLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/DataLog/DIAG/";
    }

    public static String getImmoDataLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/DataLog/IMMO/";
    }

    public static String getFeedbackLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/FeedbackLog/";
    }

    public static String getUserDataDiag() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/Diagnose/";
    }

    public static String getUserDataImmo() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/Immo/";
    }

    public static String getUserDataNewEnergy() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/NewEnergy/";
    }

    public static String getUserDataRFID() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/RFID/";
    }

    public static String getSoftDownPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Download/";
    }

    public static String getAutoVinLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/autovinLog/";
    }

}
