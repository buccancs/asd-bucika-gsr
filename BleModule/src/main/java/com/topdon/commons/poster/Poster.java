package com.topdon.commons.poster;

import androidx.annotation.NonNull;

/**
 * date: 2019/8/7 09:44
 * author: chuanfeng.bi
 */
interface Poster {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     * 
 * @param runnable 
     */
    void enqueue(@NonNull Runnable runnable);

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    void clear();
}
