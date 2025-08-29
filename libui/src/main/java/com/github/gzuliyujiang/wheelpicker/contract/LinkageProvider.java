/*
 * Copyright (c) 2016-present <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.github.gzuliyujiang.wheelpicker.contract;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * data
 *
 * @author （1032694760@qq.com）
 * @since 2019/6/17 11:27
 */
public interface LinkageProvider {
    int INDEX_NO_FOUND = -1;

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @return true
     */
    boolean firstLevelVisible();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @return true
     */
    boolean thirdLevelVisible();

    /**
     * data
     *
     * data
     */
    @NonNull
    List<?> provideFirstData();

    /**
     * data
     *
     * data
     * data
     */
    @NonNull
    List<?> linkageSecondData(int firstIndex);

    /**
     * data
     *
     * data
     * data
     * data
     */
    @NonNull
    List<?> linkageThirdData(int firstIndex, int secondIndex);

    /**
     * data
     *
     * data
     * data
     */
    int findFirstIndex(Object firstValue);

    /**
     * data
     *
     * data
     * data
     * data
     */
    int findSecondIndex(int firstIndex, Object secondValue);

    /**
     * data
     *
     * data
     * data
     * data
     * data
     */
    int findThirdIndex(int firstIndex, int secondIndex, Object thirdValue);

}