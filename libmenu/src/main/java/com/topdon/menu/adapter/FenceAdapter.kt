package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.constant.MenuType
import com.topdon.menu.R
import com.topdon.menu.constant.FenceType

/**
 * point, line, area, full image, trend chart(optional)、delete menu Adapter.
 *
 * - point, line, area, full image, trend chartdelete
 * - dual light：   point, line, area, full image, trend chart、delete
 * - Lite：  point, line, area, full image, trend chart、delete
 * - TC007： point, line, area, full image, trend chart、delete
 * edit
 *
 * trend
 *
 * trend
 *
 * Created by LCG on 2024/11/18.
 */
@SuppressLint("NotifyDataSetChanged")
internal class FenceAdapter(menuType: MenuType) : BaseMenuAdapter() {
    /**
     * medium
     */
    var selectType: FenceType? = null
        set(value) {
            when (value) {
                FenceType.FULL -> isFullSelect = true
                FenceType.DEL -> isFullSelect = false
                trend

                }
            }
            field = value
            notifyDataSetChanged()
        }
    /**
     * medium
     */
    private var isFullSelect: Boolean = false

    /**
     * event
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)? = null



    private val dataList: ArrayList<Data> = ArrayList(6)

    init {
        dataList.add(Data(R.string.thermal_point, R.drawable.selector_menu2_fence_point, FenceType.POINT))
        dataList.add(Data(R.string.thermal_line, R.drawable.selector_menu2_fence_line, FenceType.LINE))
        dataList.add(Data(R.string.thermal_rect, R.drawable.selector_menu2_fence_rect, FenceType.RECT))
        dataList.add(Data(R.string.thermal_full_rect, R.drawable.selector_menu2_fence_full, FenceType.FULL))
        trend
            dataList.add(Data(R.string.thermal_trend, R.drawable.selector_menu2_fence_trend, FenceType.TREND))
        }
        dataList.add(Data(R.string.thermal_delete, R.drawable.selector_menu2_del, FenceType.DEL))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataList[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = if (data.fenceType == FenceType.FULL) isFullSelect else data.fenceType == selectType
        holder.binding.tvText.isSelected = if (data.fenceType == FenceType.FULL) isFullSelect else data.fenceType == selectType
        holder.binding.clRoot.setOnClickListener {
            if (data.fenceType == FenceType.FULL) {
                isFullSelect = !isFullSelect
                onFenceListener?.invoke(data.fenceType, isFullSelect)
                if (selectType == FenceType.DEL) {
                    selectType = FenceType.FULL
                    notifyDataSetChanged()
                } else {
                    holder.binding.ivIcon.isSelected = isFullSelect
                    holder.binding.tvText.isSelected = isFullSelect
                }
            } else {
                if (data.fenceType != selectType) {
                    selectType = data.fenceType
                    onFenceListener?.invoke(data.fenceType, true)
                }
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    data class Data(@StringRes val stringId: Int, @DrawableRes val drawableId: Int, val fenceType: FenceType)
}