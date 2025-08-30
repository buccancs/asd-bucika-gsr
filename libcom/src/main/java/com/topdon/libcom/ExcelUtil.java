package com.topdon.libcom;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;
import com.topdon.lib.core.common.SharedManager;
import com.topdon.lib.core.config.FileConfig;
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
            callback != null ? new ExportUtils.ThermalExportCallback() {
                @Override
                public void onOneCell(int current, int total) {
                    callback.onOneCell(current, total);
                }
            }
        }
        try {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                File excel = new File(FileConfig.getExcelDir(), name + ".xlsx");
                FileOutputStream fos = new FileOutputStream(excel);
                workbook.write(fos);
                fos.flush();
                fos.close();
                return excel.getAbsolutePath();
            }else {
                String fileName = name + ".xlsx";
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, FileConfig.getExcelDir());
                Uri contentUri = MediaStore.Files.getContentUri("external");
                Uri uri = Utils.getApp().getContentResolver().insert(contentUri, values);
                if (uri != null) {
                    OutputStream outputStream = Utils.getApp().getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                        workbook.write(bos);
                        bos.flush();
                        bos.close();
                    }
                    return UriUtils.uri2File(uri).getAbsolutePath();
                }else {
                    return null;
                }
            }
        }catch (Exception e){
            return null;
        }
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

    /**
     * @param listData
     * @return
     */
    public static String exportExcel(ArrayList<ThermalEntity> listData,boolean isPoint) {
        boolean isShowC = SharedManager.INSTANCE.getTemperature() == 1;
        try {
            // 创建excel xlsx格式
            Workbook wb = new XSSFWorkbook();
            // 创建工作表
            Sheet sheet = wb.createSheet();
            String[] title = {Utils.getApp().getString(R.string.detail_date), Utils.getApp().getString(R.string.chart_temperature_low), Utils.getApp().getString(R.string.chart_temperature_high)};
            if (isPoint){
                title = new String[]{Utils.getApp().getString(R.string.detail_date), Utils.getApp().getString(R.string.chart_temperature)};
            }
            //创建行对象
            Row row = sheet.createRow(0);
            // 设置有效数据的行数和列数
            int colNum = title.length;
            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font =  wb.createFont();
            font.setBold(true);//粗体显示
            titleStyle.setFont(font);
            CellStyle contentStyle = wb.createCellStyle();
            contentStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
            contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            for (int i = 0; i < colNum; i++) {
                sheet.setColumnWidth(i, 20 * 256);  // 显示20个字符的宽度
                Cell cell1 = row.createCell(i);
                cell1.setCellStyle(titleStyle);
                //第一行
                cell1.setCellValue(title[i]);
            }
            // 导入数据
            for (int rowNum = 0; rowNum < listData.size(); rowNum++) {

                // 之所以rowNum + 1 是因为要设置第二行单元格
                row = sheet.createRow(rowNum + 1);
                // 设置单元格显示宽度
                row.setHeightInPoints(28f);

                ThermalEntity bean = listData.get(rowNum);

                for (int j = 0; j < title.length; j++) {
                    Cell cell = row.createCell(j);
                    //要和title[]一一对应
                    if (isPoint){
                        switch (j) {
                            case 0:
                                //时间
                                cell.setCellValue(bean.getTime());
                                break;
                            case 1:
                                //温度
                                cell.setCellStyle(contentStyle);
                                cell.setCellValue(UnitTools.showC(bean.getMinTemp()));
                                break;
                        }
                    }else {
                        switch (j) {
                            case 0:
                                //时间
                                cell.setCellValue(bean.getTime());
                                break;
                            case 1:
                                //最低温
                                cell.setCellStyle(contentStyle);
                                cell.setCellValue(UnitTools.showC(bean.getMinTemp()));
                                break;
                            case 2:
                                //最高温
                                cell.setCellStyle(contentStyle);
                                cell.setCellValue(UnitTools.showC(bean.getMaxTemp(),isShowC));
                                break;
                        }
                    }
                }
            }
            String timeStr = listData.isEmpty() ? TimeTool.INSTANCE.showDateSecond() : TimeUtils.millis2String(listData.get(0).getStartTime(), "yyyyMMddHHmmss");
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                File excel = new File(FileConfig.getExcelDir(), "TCView_"+ timeStr + ".xlsx");
                FileOutputStream fos = new FileOutputStream(excel);
                wb.write(fos);
                fos.flush();
                fos.close();
                return excel.getAbsolutePath();
            }else {
                String fileName = "TCView_"+timeStr + ".xlsx";
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
//                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/xlsx");
//                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, FileConfig.getExcelDir());
                Uri contentUri = MediaStore.Files.getContentUri("external");
                Uri uri = Utils.getApp().getContentResolver().insert(contentUri, values);
                if (uri != null) {
                    OutputStream outputStream = Utils.getApp().getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                        wb.write(bos);
                        bos.flush();
                        bos.close();
                    }
                    return UriUtils.uri2File(uri).getAbsolutePath();
                }else {
                    return null;
                }
            }
        } catch (IOException e) {
            return null;
        }

    }
}
