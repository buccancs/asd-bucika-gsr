package com.topdon.hik.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.topdon.hik.R
import com.topdon.hik.databinding.FragmentIrThermalHikBinding
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseBindingFragment
import com.topdon.libcom.bean.SaveSettingBean
import com.topdon.libhik.util.HikHelper
import com.topdon.module.thermal.ir.bean.DataBean
import com.topdon.module.thermal.ir.repository.ConfigRepository
import kotlinx.coroutines.launch

/**
 * Fragment.
 *
 * Created by LCG on 2025/1/14.
 */
class IRThermalHikFragment : BaseBindingFragment<FragmentIrThermalHikBinding>() {
    override fun initContentLayoutId(): Int = R.layout.fragment_ir_thermal_hik

    override fun initView(savedInstanceState: Bundle?) {
        HikHelper.bind(this)
        HikHelper.setFrameListener { yuvArray, tempArray ->
            binding.hikSurfaceView.refresh(yuvArray, tempArray)
        }
        HikHelper.onTimeoutListener = {
 // TODO: 
            TipDialog.Builder(requireContext())
 .setMessage("5")
                .setPositiveListener(R.string.app_got_it) {
                    activity?.finish()
                }
                .create().show()
        }
        HikHelper.onReadyListener = {
 //thermal imaging
            lifecycleScope.launch {
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                // [Technical comment in Chinese - content removed for ASCII compatibility]
                // temperature
 //high low temperature levelTC001

                HikHelper.initConfig()
                HikHelper.setAutoShutter(true)
 HikHelper.setContrast(50) //
 HikHelper.setEnhanceLevel(50) //

                val config: DataBean = ConfigRepository.readConfig(false)
                emissivity
                HikHelper.setDistance((config.distance * 100).toInt().coerceAtLeast(30))
            }
        }

        val saveSetBean = SaveSettingBean()
        save
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun getBitmap(): Bitmap = binding.hikSurfaceView.getScaleBitmap()
}