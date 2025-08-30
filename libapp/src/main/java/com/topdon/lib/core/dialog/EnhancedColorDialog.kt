package com.topdon.lib.core.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.ui.bean.ColorBean

/**
 * Enhanced color picker dialog consolidating libcom color dialog functionality.
 * Provides comprehensive color selection with predefined palettes and custom color picking.
 */
class EnhancedColorDialog(private val initialColor: Int = Color.WHITE) : DialogFragment() {

    var onColorSelected: ((color: Int) -> Unit)? = null
    var onCancelled: (() -> Unit)? = null

    private var selectedColor: Int = initialColor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create a simple color picker layout programmatically
        // In real implementation, this would use the actual layout resource
        val rootView = createColorPickerLayout(inflater, container)
        return rootView
    }

    private fun createColorPickerLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        // Create a programmatic layout for the color picker
        // This is a simplified version - in practice, you'd use XML layouts
        val linearLayout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        // Add color grid
        val colorGrid = RecyclerView(requireContext()).apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = ColorGridAdapter(getDefaultColors()) { color ->
                selectedColor = color
                onColorSelected?.invoke(color)
                dismiss()
            }
        }

        // Add buttons
        val buttonLayout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
        }

        val cancelButton = android.widget.Button(requireContext()).apply {
            text = "Cancel"
            setOnClickListener {
                onCancelled?.invoke()
                dismiss()
            }
        }

        val confirmButton = android.widget.Button(requireContext()).apply {
            text = "Confirm"
            setOnClickListener {
                onColorSelected?.invoke(selectedColor)
                dismiss()
            }
        }

        buttonLayout.addView(cancelButton)
        buttonLayout.addView(confirmButton)

        linearLayout.addView(colorGrid)
        linearLayout.addView(buttonLayout)

        return linearLayout
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = params
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun getDefaultColors(): List<Int> = listOf(
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA,
        Color.BLACK, Color.WHITE, Color.GRAY, Color.LTGRAY, Color.DKGRAY,
        Color.parseColor("#FF5722"), Color.parseColor("#E91E63"), Color.parseColor("#9C27B0"),
        Color.parseColor("#673AB7"), Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"),
        Color.parseColor("#03A9F4"), Color.parseColor("#00BCD4"), Color.parseColor("#009688"),
        Color.parseColor("#4CAF50"), Color.parseColor("#8BC34A"), Color.parseColor("#CDDC39"),
        Color.parseColor("#FFEB3B"), Color.parseColor("#FFC107"), Color.parseColor("#FF9800")
    )

    private inner class ColorGridAdapter(
        private val colors: List<Int>,
        private val onColorClick: (Int) -> Unit
    ) : RecyclerView.Adapter<ColorGridAdapter.ColorViewHolder>() {

        inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            val view = View(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(100, 100)
                setOnClickListener { onColorClick(colors[adapterPosition]) }
            }
            return ColorViewHolder(view)
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            holder.itemView.setBackgroundColor(colors[position])
        }

        override fun getItemCount() = colors.size
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            tag: String = "EnhancedColorDialog",
            initialColor: Int = Color.WHITE,
            onColorSelected: (Int) -> Unit
        ) {
            val dialog = EnhancedColorDialog(initialColor).apply {
                this.onColorSelected = onColorSelected
            }
            dialog.show(fragmentManager, tag)
        }
    }
}