package com.topdon.commons.base.entity;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * [Technical comment in Chinese - content removed for ASCII compatibility]
 * 
 * date: 2019/8/6 13:31
 * author: chuanfeng.bi
 */
public abstract class AbstractTimer {
    private Timer timer;
    private final Handler handler;
    private final boolean callbackOnMainThread;
    
    public AbstractTimer(boolean callbackOnMainThread) {
        handler = new Handler(Looper.getMainLooper());
        this.callbackOnMainThread = callbackOnMainThread;
    }

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    public abstract void onTick();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    public synchronized final void start(long delay, long period) {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (callbackOnMainThread) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onTick();
                            }
                        });
                    } else {
                        onTick();
                    }
                }
            }, delay, period);
        }
    }
    
    public synchronized final void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    public boolean isRunning() {
        return timer != null;
    }
}
