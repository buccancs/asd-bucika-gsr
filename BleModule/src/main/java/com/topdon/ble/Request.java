package com.topdon.ble;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * date: 2019/8/11 15:34
 * author: bichuanfeng
 */
public interface Request {
    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @NonNull
    Device getDevice();

    /**
 * type
     */
    @NonNull
    RequestType getType();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    @Nullable
    String getTag();

    /**
 * UUID
     */
    @Nullable
    UUID getService();

    /**
 * UUID
     */
    @Nullable
    UUID getCharacteristic();

    /**
 * UUID
     */
    @Nullable
    UUID getDescriptor();

    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     *
 * @param connection 
     */
    void execute(Connection connection);
}
