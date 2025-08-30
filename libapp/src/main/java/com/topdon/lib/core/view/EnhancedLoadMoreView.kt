package com.topdon.lib.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.util.getItemView
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Enhanced load more view with comprehensive loading state management
 * Consolidated from multiple lib* modules with additional capabilities
 */
class EnhancedLoadMoreView @JvmOverloads constructor(
    private val context: Context,
    private val customLayoutRes: Int = 0,
    private val loadingText: String? = null,
    private val completeText: String? = null,
    private val endText: String? = null,
    private val failText: String? = null
) : BaseLoadMoreView() {
    
    companion object {
        private const val DEFAULT_LOADING_TEXT = "Loading..."
        private const val DEFAULT_COMPLETE_TEXT = "Load Complete"
        private const val DEFAULT_END_TEXT = "No More Data"
        private const val DEFAULT_FAIL_TEXT = "Load Failed"
    }
    
    // State management
    private var currentState: LoadState = LoadState.LOADING
    private var onStateChangeListener: ((LoadState) -> Unit)? = null
    
    enum class LoadState {
        LOADING, COMPLETE, END, FAIL
    }

    override fun getRootView(parent: ViewGroup): View {
        val layoutRes = if (customLayoutRes != 0) {
            customLayoutRes
        } else {
            getDefaultLayoutRes(parent.context)
        }
        
        return parent.getItemView(layoutRes)
    }

    override fun getLoadingView(holder: BaseViewHolder): View {
        currentState = LoadState.LOADING
        onStateChangeListener?.invoke(currentState)
        
        val view = holder.getView(getViewId(holder, "load_more_loading_view"))
        updateViewText(view, loadingText ?: DEFAULT_LOADING_TEXT)
        return view
    }

    override fun getLoadComplete(holder: BaseViewHolder): View {
        currentState = LoadState.COMPLETE
        onStateChangeListener?.invoke(currentState)
        
        val view = holder.getView(getViewId(holder, "load_more_load_complete_view"))
        updateViewText(view, completeText ?: DEFAULT_COMPLETE_TEXT)
        return view
    }

    override fun getLoadEndView(holder: BaseViewHolder): View {
        currentState = LoadState.END
        onStateChangeListener?.invoke(currentState)
        
        val view = holder.getView(getViewId(holder, "load_more_load_end_view"))
        updateViewText(view, endText ?: DEFAULT_END_TEXT)
        return view
    }

    override fun getLoadFailView(holder: BaseViewHolder): View {
        currentState = LoadState.FAIL
        onStateChangeListener?.invoke(currentState)
        
        val view = holder.getView(getViewId(holder, "load_more_load_fail_view"))
        updateViewText(view, failText ?: DEFAULT_FAIL_TEXT)
        return view
    }
    
    private fun getDefaultLayoutRes(context: Context): Int {
        return try {
            context.resources.getIdentifier("layout_load_more_view", "layout", context.packageName)
        } catch (e: Exception) {
            try {
                context.resources.getIdentifier("layout_enhanced_load_more", "layout", context.packageName)
            } catch (e2: Exception) {
                android.R.layout.simple_list_item_1
            }
        }
    }
    
    private fun getViewId(holder: BaseViewHolder, viewName: String): Int {
        return try {
            holder.itemView.context.resources.getIdentifier(viewName, "id", holder.itemView.context.packageName)
        } catch (e: Exception) {
            // Fallback to common view IDs
            when (viewName) {
                "load_more_loading_view" -> android.R.id.progress
                "load_more_load_complete_view" -> android.R.id.text1
                "load_more_load_end_view" -> android.R.id.text1
                "load_more_load_fail_view" -> android.R.id.button1
                else -> android.R.id.text1
            }
        }
    }
    
    private fun updateViewText(view: View?, text: String) {
        if (view is android.widget.TextView) {
            view.text = text
        }
    }
    
    /**
     * Get current loading state
     */
    fun getCurrentState(): LoadState = currentState
    
    /**
     * Set listener for state changes
     */
    fun setOnStateChangeListener(listener: (LoadState) -> Unit) {
        onStateChangeListener = listener
    }
    
    /**
     * Create a themed load more view for specific use cases
     */
    class Builder(private val context: Context) {
        private var layoutRes = 0
        private var loadingText: String? = null
        private var completeText: String? = null
        private var endText: String? = null
        private var failText: String? = null
        
        fun setCustomLayout(layoutRes: Int) = apply {
            this.layoutRes = layoutRes
        }
        
        fun setLoadingText(text: String) = apply {
            this.loadingText = text
        }
        
        fun setCompleteText(text: String) = apply {
            this.completeText = text
        }
        
        fun setEndText(text: String) = apply {
            this.endText = text
        }
        
        fun setFailText(text: String) = apply {
            this.failText = text
        }
        
        fun build(): EnhancedLoadMoreView {
            return EnhancedLoadMoreView(
                context = context,
                customLayoutRes = layoutRes,
                loadingText = loadingText,
                completeText = completeText,
                endText = endText,
                failText = failText
            )
        }
    }
}