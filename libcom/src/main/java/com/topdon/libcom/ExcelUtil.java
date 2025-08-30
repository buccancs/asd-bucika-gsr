package com.topdon.libcom;

import android.net.Uri;
import com.topdon.lib.core.db.entity.ThermalEntity;
import com.topdon.lib.core.export.ExportUtils;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Backward compatibility wrapper for ExcelUtil.
 * Delegates to enhanced ExportUtils in libapp core.
 * @author: CaiSongL
/**
 * Backward compatibility wrapper for ExcelUtil.
 * @deprecated Use ExportUtils in libapp core instead
 * @date: 2023/5/11 15:58
 */
@Deprecated
public class ExcelUtil {

    /**
     * Export thermal matrix to Excel file.
     * @param name File name
     * @param width Matrix width
     * @param height Matrix height  
     * @param norTempData Temperature data bytes
     * @param callback Progress callback
     * @return File path or null if failed
     */
    @Nullable
    public static String exportExcel(@NonNull String name, int width, int height, @NonNull byte[] norTempData, @Nullable Callback callback) {
        return ExportUtils.INSTANCE.exportThermalMatrix(name, width, height, norTempData, 
            callback != null ? new ExportUtils.ProgressCallback() {
                @Override
                public void onOneCell(int current, int total) {
                    callback.onOneCell(current, total);
                }
            } : null);
    }

    /**
     * Export thermal entity list to Excel file.
     * @param listData List of thermal entities
     * @param isPoint Whether to include point data
     * @return File path or null if failed
     */
    public static String exportExcel(ArrayList<ThermalEntity> listData, boolean isPoint) {
        Uri uri = ExportUtils.INSTANCE.exportToExcel(listData, null);
        return uri != null ? uri.getPath() : null;
    }

    /**
     * Callback interface for progress reporting during Excel export.
     */
    @FunctionalInterface
    public interface Callback {
        void onOneCell(int current, int total);
    }
}