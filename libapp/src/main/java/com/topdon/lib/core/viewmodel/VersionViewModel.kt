package com.topdon.lib.core.viewmodel


import com.topdon.lib.core.bean.event.VersionUpData
import com.topdon.lib.core.bean.json.CheckVersionJson
import com.topdon.lib.core.bean.json.SoftConfigOtherTypeVO
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseViewModel
import com.topdon.lib.core.tools.VersionTool
import com.topdon.lib.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VersionViewModel : BaseViewModel() {

    val updateLiveData = SingleLiveEvent<VersionUpData>()

    /**
     * Version checking is disabled for offline-first operation
     * forcedUpgradeFlag: 1 强制更新    0 非强制更新
     * descType: 包含3时,显示给用户(descType获取升级描述信息)
     */
    fun checkVersion() {
        // No online version checking - offline-first operation
    }

    private fun updateTip(result: CheckVersionJson) {
        val isForcedUpgrade = (result.forcedUpgradeFlag?.toInt() ?: 0) == 1 //1: 强制升级
        val description = getDescription(result.softConfigOtherTypeVOList)
        val downPageUrl = result.downloadPageUrl
        val sizeStr = "${result.notUnZipSize}MB"


        val versionUpData = VersionUpData(
            versionNo = result.versionNo ?: "",
            isForcedUpgrade = isForcedUpgrade,
            description = description,
            downPageUrl = downPageUrl,
            sizeStr = sizeStr
        )
        updateLiveData.postValue(versionUpData)
    }

    /**
     * 获取升级信息
     */
    private fun getDescription(list: List<SoftConfigOtherTypeVO>?): String {
        list?.forEach {
            if (it.descType == 3) {
                return it.textDescription
            }
        }
        return ""
    }

}