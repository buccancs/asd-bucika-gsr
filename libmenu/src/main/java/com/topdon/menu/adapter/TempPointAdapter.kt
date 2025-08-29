package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.R
import com.topdon.menu.constant.TempPointType

/**
 * Observation mode-menu5-high low temperature point menuused for Adapter，按旧逻辑存在全部未选择的状态。
 *
 * - High temperature point、Low temperature point 互相独立，可多选
 * - {High temperature point、Low temperature point} 与 delete 互斥
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class TempPointAdapter : BaseMenuAdapter() {
    /**
     * Observation mode-menu5-high low temperature point 点击事件监听.
     */
    var onTempPointListener: ((type: TempPointType, isSelected: Boolean) -> Unit)? = null

    /**
     * settings High temperature point 或 低稳点 的选中状态。
     */
    fun setSelected(tempPointType: TempPointType, isSelected: Boolean) {
        for (i in dataArray.indices) {
            if (dataArray[i].tempPointType == tempPointType) {
                dataArray[i].isSelected = isSelected
                notifyItemChanged(i)
                break
            }
        }
    }

    /**
     * 清除所有menu的选中状态。
     * 这里维持原有逻辑，后续考虑是否直接给选中delete得了。
     */
    fun clearAllSelect() {
        for (data in dataArray) {
            data.isSelected = false
        }
        notifyDataSetChanged()
    }


    private val dataArray: Array<Data> = arrayOf(
        Data(R.string.main_tab_second_high_temperature_point, R.drawable.selector_menu2_temp_point_1, TempPointType.HIGH),
        Data(R.string.main_tab_second_low_temperature_point, R.drawable.selector_menu2_temp_point_2, TempPointType.LOW),
        Data(R.string.thermal_delete, R.drawable.selector_menu2_del, TempPointType.DELETE),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataArray[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = data.isSelected
        holder.binding.tvText.isSelected = data.isSelected
        holder.binding.clRoot.setOnClickListener {
            if (data.tempPointType == TempPointType.DELETE) {
                if (!data.isSelected) {//选中时再次delete没卵用，未选中时才处理
                    for (temp in dataArray) {
                        temp.isSelected = temp.tempPointType == TempPointType.DELETE
                    }
                    notifyDataSetChanged()
                    onTempPointListener?.invoke(TempPointType.DELETE, true)
                }
            } else {
                data.isSelected = !data.isSelected
                holder.binding.ivIcon.isSelected = data.isSelected
                holder.binding.tvText.isSelected = data.isSelected
                if (data.isSelected) {//选中High temperature point、Low temperature point时要把“delete”设为未选中；cancel选中时不耦合delete
                    for (i in dataArray.indices) {
                        if (dataArray[i].tempPointType == TempPointType.DELETE && dataArray[i].isSelected) {
                            dataArray[i].isSelected = false
                            notifyItemChanged(i)
                        }
                    }
                }
                onTempPointListener?.invoke(data.tempPointType, data.isSelected)
            }
        }
    }

    override fun getItemCount(): Int = dataArray.size

    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes val drawableId: Int,
        val tempPointType: TempPointType,
        var isSelected: Boolean = false,
    )
}