package com.topdon.commons.poster;

import java.lang.annotation.*;

/**
 * marker
 * <p>
 * date: 2019/8/2 23:53
 * author: chuanfeng.bi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RunOn {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    ThreadMode value() default ThreadMode.UNSPECIFIED;
}
