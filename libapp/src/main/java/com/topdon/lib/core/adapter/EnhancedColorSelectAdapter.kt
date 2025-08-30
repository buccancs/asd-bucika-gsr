package com.topdon.lib.core.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.bean.ColorSelectBean

/**
 * Enhanced color selection adapter with comprehensive color management capabilities  
 * Consolidated from libcom.adapter.DColorSelectAdapter with additional features
 */
class EnhancedColorSelectAdapter(
    private val context: Context,
    private val colorList: MutableList<ColorSelectBean> = mutableListOf(),
    private val layoutResId: Int = 0
) : RecyclerView.Adapter<EnhancedColorSelectAdapter.ColorViewHolder>() {

    companion object {
        private val DEFAULT_COLORS = intArrayOf(
            0xFFFF0000.toInt(), // Red
            0xFF00FF00.toInt(), // Green  
            0xFF0000FF.toInt(), // Blue
            0xFFFFFF00.toInt(), // Yellow
            0xFFFF00FF.toInt(), // Magenta
            0xFF00FFFF.toInt(), // Cyan
            0xFFFFFFFF.toInt(), // White
            0xFF000000.toInt()  // Black
        )
    }

    // Selection handling
    private var selectedPosition = RecyclerView.NO_POSITION
    private var multiSelectEnabled = false
    private val selectedPositions = mutableSetOf<Int>()
    
    // Callbacks
    var onColorSelectedListener: ((color: ColorSelectBean, position: Int) -> Unit)? = null
    var onColorLongClickListener: ((color: ColorSelectBean, position: Int) -> Boolean)? = null
    var onMultiSelectListener: ((selectedColors: List<ColorSelectBean>) -> Unit)? = null

    init {
        if (colorList.isEmpty()) {
            loadDefaultColors()
        }
    }

    private fun loadDefaultColors() {
        DEFAULT_COLORS.forEach { color ->
            colorList.add(ColorSelectBean().apply {
                colorValue = color
                isSelected = false
                colorName = getColorName(color)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val layoutId = if (layoutResId != 0) layoutResId else getDefaultLayoutRes()
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorBean = colorList[position]
        holder.bind(colorBean, position)
    }

    override fun getItemCount(): Int = colorList.size

    private fun getDefaultLayoutRes(): Int {
        // Try to find a color item layout
        return try {
            context.resources.getIdentifier("item_color_select", "layout", context.packageName)
        } catch (e: Exception) {
            android.R.layout.simple_list_item_1
        }
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorView: View? = findColorView(itemView)
        private val selectionIndicator: View? = findSelectionIndicator(itemView)
        private val colorNameView: android.widget.TextView? = findColorNameView(itemView)

        private fun findColorView(view: View): View? {
            return findViewById(view, "color_preview") ?: findViewById(view, "iv_color") ?: view
        }

        private fun findSelectionIndicator(view: View): View? {
            return findViewById(view, "selection_indicator") ?: findViewById(view, "iv_selected")
        }

        private fun findColorNameView(view: View): android.widget.TextView? {
            return findViewById(view, "color_name") as? android.widget.TextView
                ?: findViewById(view, "tv_color_name") as? android.widget.TextView
        }

        private fun findViewById(parent: View, name: String): View? {
            return try {
                val id = context.resources.getIdentifier(name, "id", context.packageName)
                if (id != 0) parent.findViewById(id) else null
            } catch (e: Exception) {
                null
            }
        }

        fun bind(colorBean: ColorSelectBean, position: Int) {
            // Set color background
            colorView?.setBackgroundColor(colorBean.colorValue)
            
            // Set color name if available
            colorNameView?.text = colorBean.colorName ?: getColorName(colorBean.colorValue)
            
            // Handle selection state
            updateSelectionState(colorBean, position)
            
            // Set click listener
            itemView.setOnClickListener {
                handleColorSelection(colorBean, position)
            }
            
            // Set long click listener
            itemView.setOnLongClickListener {
                onColorLongClickListener?.invoke(colorBean, position) ?: false
            }
        }

        private fun updateSelectionState(colorBean: ColorSelectBean, position: Int) {
            val isSelected = if (multiSelectEnabled) {
                selectedPositions.contains(position)
            } else {
                position == selectedPosition
            }
            
            colorBean.isSelected = isSelected
            selectionIndicator?.visibility = if (isSelected) View.VISIBLE else View.GONE
            
            // Update visual feedback
            itemView.alpha = if (isSelected) 1.0f else 0.7f
            itemView.scaleX = if (isSelected) 1.1f else 1.0f
            itemView.scaleY = if (isSelected) 1.1f else 1.0f
        }
    }

    private fun handleColorSelection(colorBean: ColorSelectBean, position: Int) {
        if (multiSelectEnabled) {
            handleMultiSelection(position)
        } else {
            handleSingleSelection(colorBean, position)
        }
    }

    private fun handleSingleSelection(colorBean: ColorSelectBean, position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        
        // Update UI
        notifyItemChanged(previousPosition)
        notifyItemChanged(position)
        
        onColorSelectedListener?.invoke(colorBean, position)
    }

    private fun handleMultiSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
        
        notifyItemChanged(position)
        
        val selectedColors = selectedPositions.map { colorList[it] }
        onMultiSelectListener?.invoke(selectedColors)
    }

    /**
     * Enable or disable multi-selection mode
     */
    fun setMultiSelectEnabled(enabled: Boolean) {
        if (multiSelectEnabled != enabled) {
            multiSelectEnabled = enabled
            clearSelection()
        }
    }

    /**
     * Add a new color to the list
     */
    fun addColor(color: Int, name: String? = null) {
        val colorBean = ColorSelectBean().apply {
            colorValue = color
            colorName = name ?: getColorName(color)
            isSelected = false
        }
        
        colorList.add(colorBean)
        notifyItemInserted(colorList.size - 1)
    }

    /**
     * Remove color at position
     */
    fun removeColor(position: Int) {
        if (position in 0 until colorList.size) {
            colorList.removeAt(position)
            notifyItemRemoved(position)
            
            // Adjust selection if needed
            if (selectedPosition >= position) {
                selectedPosition = RecyclerView.NO_POSITION
            }
            selectedPositions.removeIf { it >= position }
        }
    }

    /**
     * Clear all selections
     */
    fun clearSelection() {
        val oldSelected = selectedPosition
        val oldMultiSelected = selectedPositions.toSet()
        
        selectedPosition = RecyclerView.NO_POSITION
        selectedPositions.clear()
        
        if (oldSelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(oldSelected)
        }
        
        oldMultiSelected.forEach { position ->
            notifyItemChanged(position)
        }
    }

    /**
     * Get currently selected color(s)
     */
    fun getSelectedColors(): List<ColorSelectBean> {
        return if (multiSelectEnabled) {
            selectedPositions.map { colorList[it] }
        } else {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                listOf(colorList[selectedPosition])
            } else {
                emptyList()
            }
        }
    }

    /**
     * Set selected color by color value
     */
    fun setSelectedColor(color: Int) {
        val position = colorList.indexOfFirst { it.colorValue == color }
        if (position != -1) {
            handleSingleSelection(colorList[position], position)
        }
    }

    /**
     * Update color list with new colors
     */
    fun updateColors(newColors: List<ColorSelectBean>) {
        colorList.clear()
        colorList.addAll(newColors)
        clearSelection()
        notifyDataSetChanged()
    }

    /**
     * Get readable color name from color value
     */
    private fun getColorName(color: Int): String {
        return when (color) {
            0xFFFF0000.toInt() -> "Red"
            0xFF00FF00.toInt() -> "Green"
            0xFF0000FF.toInt() -> "Blue"
            0xFFFFFF00.toInt() -> "Yellow"
            0xFFFF00FF.toInt() -> "Magenta"
            0xFF00FFFF.toInt() -> "Cyan"
            0xFFFFFFFF.toInt() -> "White"
            0xFF000000.toInt() -> "Black"
            0xFFFFA500.toInt() -> "Orange"
            0xFF800080.toInt() -> "Purple"
            0xFF008000.toInt() -> "Dark Green"
            0xFF800000.toInt() -> "Maroon"
            else -> String.format("#%08X", color)
        }
    }
}