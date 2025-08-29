package com.infisense.usbir;

import android.content.Context;

/**
 * CrashHandler - Simplified (crash handling removed)
 * @author: CaiSongL
 * @date: 2023/5/24 9:47
 */
public class CrashHandler {

    private static CrashHandler crashHandler = new CrashHandler();

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (crashHandler == null) {
            synchronized (CrashHandler.class) {
                if (crashHandler == null) {
                    crashHandler = new CrashHandler();
                }
            }
        }
        return crashHandler;
    }

    public void init(Context context) {
        // Crash handling removed
    }
}