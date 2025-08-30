package com.topdon.libcom

import android.view.View
import android.widget.ScrollView
import com.topdon.lib.core.export.ExportUtils

/**
 * Backward compatibility wrapper for PDFHelp.
 * Delegates to enhanced ExportUtils in libapp core.
 * @author: CaiSongL
 * @date: 2023/5/5 17:41
 */
@Deprecated("Use ExportUtils in libapp core instead")
object PDFHelp {

    /**
     * Save PDF file from list view with watermark.
     * @param name File name
     * @param view ScrollView container
     * @param viewList List of views to render
     * @param watermarkView Watermark view
     * @return File path
     */
    fun savePdfFileByListView(name: String, view: ScrollView, viewList: MutableList<View>, watermarkView: View): String {
        return ExportUtils.savePdfFromViews(name, view, viewList, watermarkView)
    }
}