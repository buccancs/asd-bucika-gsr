package com.topdon.commons.util;

import static com.topdon.lms.sdk.LMS.SUCCESS;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.topdon.lms.sdk.LMS;
import com.topdon.lms.sdk.network.IResponseCallback;
import com.topdon.lms.sdk.utils.StringUtils;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class TDatrsInIUtil {

    /**
     * Helper method to load and configure INI file
     */
    private static Ini loadIniFile(String path) throws Exception {
        File file = new File(path + "T-darts.ini");
        if (!file.exists()) {
            LLog.e("bcf", "  ini不存在：" + file.getPath());
            return null;
        }
        Config cfg = new Config();
        cfg.setLowerCaseOption(true);
        cfg.setLowerCaseSection(true);
        cfg.setMultiSection(true);
        Ini ini = new Ini();
        ini.setConfig(cfg);
        ini.load(file);
        return ini;
    }

    public static String getTdartsVersion(String path) {
        try {
            Ini ini = loadIniFile(path);
            if (ini == null) return "";
            
            Profile.Section tDartSWSection = ini.get("TDartSW".toLowerCase());
            if (tDartSWSection == null) {
                return "";
            }
            if (!TextUtils.isEmpty(tDartSWSection.get("version"))) {
                return tDartSWSection.get("Version".toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static HashMap<String, String> getTdarts(String path) {
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            Ini ini = loadIniFile(path);
            if (ini == null) return hashMap;
            
            Profile.Section tDartSWSection = ini.get("TDartSW".toLowerCase());
            if (tDartSWSection != null && !TextUtils.isEmpty(tDartSWSection.get("version"))) {
                hashMap.put("Version", tDartSWSection.get("Version".toLowerCase()));
            }

            Profile.Section libsSection = ini.get("libs");
            if (libsSection != null) {
                addToMapIfNotEmpty(hashMap, libsSection, "T-dartsApp");
                addToMapIfNotEmpty(hashMap, libsSection, "825x_module");
                addToMapIfNotEmpty(hashMap, libsSection, "N32S032-app");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    /**
     * Helper method to add section value to map if not empty
     */
    private static void addToMapIfNotEmpty(HashMap<String, String> map, Profile.Section section, String key) {
        String value = section.get(key.toLowerCase());
        if (!TextUtils.isEmpty(value)) {
            map.put(key, value);
        }
    }


    public static String getBinPath(int data) {
        String path = FolderUtil.getTdartsUpgradePath();
        if (data == 0) {
            return path + "T-dartsApp.bin";
        } else if (data == 1) {
            return path + "825x_module.bin";
        } else if (data == 2) {
            return path + "N32S032-app.bin";
        }
        return "";
    }
}
