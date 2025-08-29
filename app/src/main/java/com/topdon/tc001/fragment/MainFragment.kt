package com.topdon.tc001.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.SocketMsgEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.repository.BatteryInfo
import com.topdon.lib.core.repository.TC007Repository
import com.topdon.lib.core.socket.SocketCmdUtil
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.tools.LocaleContextWrapper
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lib.core.utils.WsCmdConstants
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.tc001.DeviceTypeActivity
import com.topdon.tc001.R
import com.topdon.tc001.popup.DelPopup
import kotlinx.android.synthetic.main.fragment_main.cl_has_device
import kotlinx.android.synthetic.main.fragment_main.cl_no_device
import kotlinx.android.synthetic.main.fragment_main.iv_add
import kotlinx.android.synthetic.main.fragment_main.recycler_view
import kotlinx.android.synthetic.main.fragment_main.tv_connect_device
import kotlinx.android.synthetic.main.item_device_connect.view.battery_view
import kotlinx.android.synthetic.main.item_device_connect.view.iv_bg
import kotlinx.android.synthetic.main.item_device_connect.view.iv_image
import kotlinx.android.synthetic.main.item_device_connect.view.tv_battery
import kotlinx.android.synthetic.main.item_device_connect.view.tv_device_name
import kotlinx.android.synthetic.main.item_device_connect.view.tv_device_state
import kotlinx.android.synthetic.main.item_device_connect.view.tv_title
import kotlinx.android.synthetic.main.item_device_connect.view.view_device_state
import kotlinx.coroutines.launch
import org.bytedeco.librealsense.context
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


/**
 * 首页 Fragment.
 *
 * Created by LCG on 2024/4/18.
 */
@SuppressLint("NotifyDataSetChanged")
class MainFragment : BaseFragment(), View.OnClickListener {

    private lateinit var adapter : MyAdapter

    override fun initContentView(): Int = R.layout.fragment_main

    override fun initView() {
        adapter = MyAdapter()
        tv_connect_device.setOnClickListener(this)
        iv_add.setOnClickListener(this)
        adapter.hasConnectLine = DeviceTools.isConnect()
        adapter.hasConnectTS004 = WebSocketProxy.getInstance().isTS004Connect()
        adapter.hasConnectTC007 = WebSocketProxy.getInstance().isTC007Connect()
        adapter.onItemClickListener = {
            when (it) {
                ConnectType.LINE -> {
                    ARouter.getInstance()
                        .build(RouterConfig.IR_MAIN)
                        .withBoolean(ExtraKeyConfig.IS_TC007, false)
                        .navigation(requireContext())
                }
            }
        }
        adapter.onItemLongClickListener = { view, type ->
            val popup = DelPopup(requireContext())
            popup.onDelListener = {
                TipDialog.Builder(requireContext())
                    .setTitleMessage(AppLanguageUtils.attachBaseContext(
                        context, SharedManager.getLanguage(requireContext())).getString(R.string.tc_delete_device))
                    .setMessage(R.string.tc_delete_device_tips)
                    .setPositiveListener(R.string.report_delete) {
                        when (type) {
                            ConnectType.LINE -> SharedManager.hasTcLine = false
                        }
                        refresh()
                        TToast.shortToast(requireContext(), R.string.test_results_delete_success)
                    }
                    .setCancelListener(R.string.app_cancel)
                    .create().show()
            }
            popup.show(view)
        }

        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        recycler_view.adapter = adapter

        if (WebSocketProxy.getInstance().isTC007Connect()) {
            lifecycleScope.launch {
                val batteryInfo: BatteryInfo? = TC007Repository.getBatteryInfo()
                if (batteryInfo != null) {
                    adapter.tc007Battery = batteryInfo
                }
            }
        }
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                // 要是当前已连接 TS004、TC007，切到流量上，不然登录注册意见反馈那些没网
                if (WebSocketProxy.getInstance().isConnected()) {
                    NetWorkUtils.switchNetwork(true)
                }
            }
        })
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        refresh()
        adapter?.notifyDataSetChanged()
    }

    private fun refresh() {
        val hasAnyDevice = SharedManager.hasTcLine || SharedManager.hasTS004 || SharedManager.hasTC007
        cl_has_device.isVisible = hasAnyDevice
        cl_no_device.isVisible = !hasAnyDevice
        adapter.hasConnectLine = DeviceTools.isConnect(isAutoRequest = false)
        adapter.hasConnectTS004 = WebSocketProxy.getInstance().isTS004Connect()
        adapter.hasConnectTC007 = WebSocketProxy.getInstance().isTC007Connect()
        adapter.notifyDataSetChanged()
    }

    override fun connected() {
        adapter.hasConnectLine = true
        SharedManager.hasTcLine = true
        refresh()
    }

    override fun disConnected() {
        adapter.hasConnectLine = false
    }

    override fun onSocketConnected(isTS004: Boolean) {
        if (isTS004) {
            SharedManager.hasTS004 = true
            adapter.hasConnectTS004 = true
        } else {
            SharedManager.hasTC007 = true
            adapter.hasConnectTC007 = true
            lifecycleScope.launch {
                val batteryInfo: BatteryInfo? = TC007Repository.getBatteryInfo()
                if (batteryInfo != null) {
                    adapter.tc007Battery = batteryInfo
                }
            }
        }
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        if (isTS004) {
            adapter.hasConnectTS004 = false
        } else {
            adapter.hasConnectTC007 = false
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            tv_connect_device, iv_add -> {//添加设备
                startActivity(Intent(requireContext(), DeviceTypeActivity::class.java))
//                ARouter.getInstance().build(RoutePath.UsbIrModule.PAGE_IR_MAIN_ACTIVITY)
//                    .navigation()
//                startActivity(Intent(requireContext(), IRThermalLiteActivity::class.java))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSocketMsgEvent(event: SocketMsgEvent) {
        if (SocketCmdUtil.getCmdResponse(event.text) == WsCmdConstants.APP_EVENT_HEART_BEATS) {//心跳
            if (!adapter.hasConnectTC007) {//当前连接的不是 TC007
                return
            }
            try {
                val battery: JSONObject = JSONObject(event.text).getJSONObject("battery")
                adapter.tc007Battery = BatteryInfo(battery.getString("status"), battery.getString("remaining"))
            } catch (_: Exception) {

            }
        }
    }

    private class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        /**
         * 有线设备当前是否已连接.
         */
        var hasConnectLine: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, 3)
            }
        /**
         * TS004 当前是否已连接.
         */
        var hasConnectTS004: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        /**
         * TC007 当前是否已连接.
         */
        var hasConnectTC007: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        /**
         * TC007 设备电池信息.
         */
        var tc007Battery: BatteryInfo? = null
            set(value) {
                if (field != value) {
                    field = value
                    notifyItemRangeChanged(0, itemCount)
                }
            }


        var onItemClickListener: ((type: ConnectType) -> Unit)? = null
        var onItemLongClickListener: ((view: View, type: ConnectType) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device_connect, parent, false))
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val type = holder.getConnectType(position)
            val hasTitle: Boolean = position == 0  // Only first item has title now
            val hasConnect: Boolean = when (type) {
                ConnectType.LINE -> hasConnectLine
            }

            holder.itemView.tv_title.isVisible = hasTitle
            holder.itemView.tv_title.text = AppLanguageUtils.attachBaseContext(
                holder.itemView.context, SharedManager.getLanguage(holder.itemView.context!!))
                .getString(R.string.tc_connect_line)  // Only LINE type supported

            holder.itemView.iv_bg.isSelected = hasConnect
            holder.itemView.tv_device_name.isSelected = hasConnect
            holder.itemView.view_device_state.isSelected = hasConnect
            holder.itemView.tv_device_state.isSelected = hasConnect
            holder.itemView.tv_device_state.text = if (hasConnect) "online" else "offline"
            holder.itemView.tv_battery.isVisible = false  // Only TC007 had battery display, removed
            holder.itemView.battery_view.isVisible = false

            when (type) {
                ConnectType.LINE -> {
                    holder.itemView.tv_device_name.setText(AppLanguageUtils.attachBaseContext(
                        holder.itemView.context, SharedManager.getLanguage(holder.itemView.context!!))
                        .getString(R.string.tc_has_line_device))
                    if (hasConnect) {
                        holder.itemView.iv_image.setImageResource(R.drawable.ic_main_device_line_connect)
                    } else {
                        holder.itemView.iv_image.setImageResource(R.drawable.ic_main_device_line_disconnect)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            // Only TC001 (LINE) is supported now
            return if (SharedManager.hasTcLine) 1 else 0
        }

        inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
            init {
                rootView.iv_bg.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.invoke(getConnectType(position))
                    }
                }
                rootView.iv_bg.setOnLongClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        //只有离线设备才能长按删除
                        val deviceType = getConnectType(position)
                        when (deviceType) {
                            ConnectType.LINE -> {
                                if (DeviceTools.isConnect()) {
                                    return@setOnLongClickListener true
                                }
                            }
                        }
                        onItemLongClickListener?.invoke(it, deviceType)
                    }
                    true
                }
            }

            fun getConnectType(position: Int): ConnectType {
                // Only TC001 (LINE) is supported now
                return ConnectType.LINE
            }
        }
    }

    enum class ConnectType {
        LINE,
    }
}