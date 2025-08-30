package com.topdon.lib.core.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.lib.core.constants.MenuConstants.MenuType
import com.topdon.lib.core.adapter.EnhancedColorSelectAdapter
import kotlinx.coroutines.*
import com.topdon.lib.core.constants.MenuConstants.FenceType
import com.topdon.lib.core.constants.MenuConstants.SettingType
import com.topdon.lib.core.constants.MenuConstants.TargetType
import com.topdon.lib.core.constants.MenuConstants.TempPointType
import com.topdon.lib.core.constants.MenuConstants.TwoLightType
import java.util.*

/**
 * Enhanced Menu Second View with comprehensive menu management capabilities
 * Consolidates menu view functionality with improved performance and maintainability
 */
open class EnhancedMenuSecondView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "EnhancedMenuSecondView"
    }

    // Enhanced menu adapters with improved functionality
    private var colorAdapter: EnhancedColorSelectAdapter? = null
    private var fenceAdapter: EnhancedMenuAdapter<FenceType>? = null
    private var settingAdapter: EnhancedMenuAdapter<SettingType>? = null
    private var targetAdapter: EnhancedMenuAdapter<TargetType>? = null
    private var tempLevelAdapter: EnhancedMenuAdapter<Int>? = null
    private var tempPointAdapter: EnhancedMenuAdapter<TempPointType>? = null
    private var tempSourceAdapter: EnhancedMenuAdapter<String>? = null
    private var twoLightAdapter: EnhancedMenuAdapter<TwoLightType>? = null

    // UI Components
    private var recyclerView: RecyclerView? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Menu configuration
    private var currentMenuType: MenuType? = null
    private var onMenuItemClickListener: ((Any) -> Unit)? = null

    init {
        initializeView(attrs)
    }

    private fun initializeView(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(
            context.resources.getIdentifier("view_menu_second", "layout", context.packageName),
            this, true
        )
        
        setupRecyclerView()
        attrs?.let { processAttributes(it) }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById<RecyclerView>(
            context.resources.getIdentifier("rv_menu", "id", context.packageName)
        )?.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
    }

    private fun processAttributes(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(attrs, intArrayOf(), 0, 0).apply {
            try {
                // Process any custom attributes if defined
            } finally {
                recycle()
            }
        }
    }

    /**
     * Set the menu type and configure the appropriate adapter
     */
    fun setMenuType(menuType: MenuType) {
        currentMenuType = menuType
        configureAdapterForMenuType(menuType)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configureAdapterForMenuType(menuType: MenuType) {
        scope.launch {
            when (menuType) {
                MenuType.COLOR -> {
                    setupColorAdapter()
                }
                MenuType.FENCE -> {
                    setupFenceAdapter()
                }
                MenuType.SETTING -> {
                    setupSettingAdapter()
                }
                MenuType.TARGET -> {
                    setupTargetAdapter()
                }
                MenuType.TEMP_LEVEL -> {
                    setupTempLevelAdapter()
                }
                MenuType.TEMP_POINT -> {
                    setupTempPointAdapter()
                }
                MenuType.TEMP_SOURCE -> {
                    setupTempSourceAdapter()
                }
                MenuType.TWO_LIGHT -> {
                    setupTwoLightAdapter()
                }
                else -> {
                    // Handle unknown menu types
                    recyclerView?.adapter = null
                }
            }
        }
    }

    private fun setupColorAdapter() {
        if (colorAdapter == null) {
            colorAdapter = EnhancedColorSelectAdapter(context).apply {
                setOnItemClickListener { color ->
                    onMenuItemClickListener?.invoke(color)
                }
            }
        }
        recyclerView?.adapter = colorAdapter
    }

    private fun setupFenceAdapter() {
        if (fenceAdapter == null) {
            fenceAdapter = EnhancedMenuAdapter<FenceType>(context) { item ->
                onMenuItemClickListener?.invoke(item)
            }.apply {
                setItems(FenceType.values().toList())
            }
        }
        recyclerView?.adapter = fenceAdapter
    }

    private fun setupSettingAdapter() {
        if (settingAdapter == null) {
            settingAdapter = EnhancedMenuAdapter<SettingType>(context) { item ->
                onMenuItemClickListener?.invoke(item)
            }.apply {
                setItems(SettingType.values().toList())
            }
        }
        recyclerView?.adapter = settingAdapter
    }

    private fun setupTargetAdapter() {
        if (targetAdapter == null) {
            targetAdapter = EnhancedMenuAdapter<TargetType>(context) { item ->
                onMenuItemClickListener?.invoke(item)
            }.apply {
                setItems(TargetType.values().toList())
            }
        }
        recyclerView?.adapter = targetAdapter
    }

    private fun setupTempLevelAdapter() {
        if (tempLevelAdapter == null) {
            tempLevelAdapter = EnhancedMenuAdapter<Int>(context) { item ->
                onMenuItemClickListener?.invoke(item)
            }.apply {
                setItems((1..10).toList()) // Default temperature levels
            }
        }
        recyclerView?.adapter = tempLevelAdapter
    }

    private fun setupTempPointAdapter() {
        if (tempPointAdapter == null) {
            tempPointAdapter = EnhancedMenuAdapter<TempPointType>(context) { item ->
                onMenuItemClickListener?.invoke(item)
            }.apply {
                setItems(TempPointType.values().toList())
            }
        }
        recyclerView?.adapter = tempPointAdapter
    }

    private fun setupTempSourceAdapter() {
        if (tempSourceAdapter == null) {
            tempSourceAdapter = EnhancedMenuAdapter<String>(context) { item ->
                onMenuItemClickListener?.invoke(item)
            }.apply {
                // Load temperature sources from repository
                scope.launch {
                    val sources = withContext(Dispatchers.IO) {
                        GalleryRepository.getInstance(context).getTemperatureSources()
                    }
                    setItems(sources)
                }
            }
        }
        recyclerView?.adapter = tempSourceAdapter
    }

    private fun setupTwoLightAdapter() {
        if (twoLightAdapter == null) {
            twoLightAdapter = EnhancedMenuAdapter<TwoLightType>(context) { item ->
                onMenuItemClickListener?.invoke(item)
            }.apply {
                setItems(TwoLightType.values().toList())
            }
        }
        recyclerView?.adapter = twoLightAdapter
    }

    /**
     * Set click listener for menu items
     */
    fun setOnMenuItemClickListener(listener: (Any) -> Unit) {
        onMenuItemClickListener = listener
    }

    /**
     * Update menu items dynamically
     */
    fun <T> updateMenuItems(items: List<T>) {
        scope.launch {
            when (currentMenuType) {
                MenuType.COLOR -> {
                    @Suppress("UNCHECKED_CAST")
                    colorAdapter?.updateColors(items as? List<Int> ?: emptyList())
                }
                MenuType.FENCE -> {
                    @Suppress("UNCHECKED_CAST")
                    fenceAdapter?.setItems(items as? List<FenceType> ?: emptyList())
                }
                MenuType.SETTING -> {
                    @Suppress("UNCHECKED_CAST")
                    settingAdapter?.setItems(items as? List<SettingType> ?: emptyList())
                }
                MenuType.TARGET -> {
                    @Suppress("UNCHECKED_CAST")
                    targetAdapter?.setItems(items as? List<TargetType> ?: emptyList())
                }
                MenuType.TEMP_LEVEL -> {
                    @Suppress("UNCHECKED_CAST")
                    tempLevelAdapter?.setItems(items as? List<Int> ?: emptyList())
                }
                MenuType.TEMP_POINT -> {
                    @Suppress("UNCHECKED_CAST")
                    tempPointAdapter?.setItems(items as? List<TempPointType> ?: emptyList())
                }
                MenuType.TEMP_SOURCE -> {
                    @Suppress("UNCHECKED_CAST")
                    tempSourceAdapter?.setItems(items as? List<String> ?: emptyList())
                }
                MenuType.TWO_LIGHT -> {
                    @Suppress("UNCHECKED_CAST")
                    twoLightAdapter?.setItems(items as? List<TwoLightType> ?: emptyList())
                }
                else -> {
                    // Handle unknown types
                }
            }
        }
    }

    /**
     * Get selected menu item
     */
    fun getSelectedItem(): Any? {
        return when (currentMenuType) {
            MenuType.COLOR -> colorAdapter?.getSelectedColor()
            MenuType.FENCE -> fenceAdapter?.getSelectedItem()
            MenuType.SETTING -> settingAdapter?.getSelectedItem()
            MenuType.TARGET -> targetAdapter?.getSelectedItem()
            MenuType.TEMP_LEVEL -> tempLevelAdapter?.getSelectedItem()
            MenuType.TEMP_POINT -> tempPointAdapter?.getSelectedItem()
            MenuType.TEMP_SOURCE -> tempSourceAdapter?.getSelectedItem()
            MenuType.TWO_LIGHT -> twoLightAdapter?.getSelectedItem()
            else -> null
        }
    }

    /**
     * Clear selection
     */
    fun clearSelection() {
        scope.launch {
            colorAdapter?.clearSelection()
            fenceAdapter?.clearSelection()
            settingAdapter?.clearSelection()
            targetAdapter?.clearSelection()
            tempLevelAdapter?.clearSelection()
            tempPointAdapter?.clearSelection()
            tempSourceAdapter?.clearSelection()
            twoLightAdapter?.clearSelection()
        }
    }

    /**
     * Enhanced menu visibility control
     */
    fun setMenuVisibility(visible: Boolean, animated: Boolean = true) {
        if (animated) {
            animate()
                .alpha(if (visible) 1.0f else 0.0f)
                .setDuration(200)
                .withEndAction {
                    isVisible = visible
                }
                .start()
        } else {
            isVisible = visible
            alpha = if (visible) 1.0f else 0.0f
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }

    /**
     * Generic enhanced menu adapter with improved functionality
     */
    private class EnhancedMenuAdapter<T>(
        private val context: Context,
        private val onItemClick: (T) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val items = mutableListOf<T>()
        private var selectedPosition = RecyclerView.NO_POSITION

        fun setItems(newItems: List<T>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        fun getSelectedItem(): T? {
            return if (selectedPosition != RecyclerView.NO_POSITION) {
                items.getOrNull(selectedPosition)
            } else null
        }

        fun clearSelection() {
            val previousPosition = selectedPosition
            selectedPosition = RecyclerView.NO_POSITION
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_1, parent, false
            )
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]
            val textView = holder.itemView as android.widget.TextView
            textView.text = item.toString()
            
            textView.isSelected = (position == selectedPosition)
            
            holder.itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                
                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition)
                }
                notifyItemChanged(selectedPosition)
                
                onItemClick(item)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}