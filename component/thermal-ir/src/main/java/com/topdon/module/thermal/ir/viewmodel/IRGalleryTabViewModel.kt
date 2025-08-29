package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import com.topdon.lib.core.ktbase.BaseViewModel

class IRGalleryTabViewModel : BaseViewModel() {
    /**
     * edit
     */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)
    /**
     * medium
     */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)

    /**
 * Fragment index 0 1.
     */
    val selectAllIndex: MutableLiveData<Int> = MutableLiveData(0)
}