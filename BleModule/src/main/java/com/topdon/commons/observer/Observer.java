package com.topdon.commons.observer;

/**
 * [Technical comment in Chinese - content removed for ASCII compatibility]
 * <p>
 * date: 2019/8/3 13:15
 * author: chuanfeng.bi
 */
public interface Observer {
    /**
     * change
     */
    @Observe
    default void onChanged(Object o) {}
}
