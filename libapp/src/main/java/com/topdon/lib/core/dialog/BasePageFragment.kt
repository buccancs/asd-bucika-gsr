package com.topdon.lib.core.dialog

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.topdon.lib.core.ktbase.BaseFragment
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

/**
 * Consolidated base page fragment for displaying image resources.
 * Consolidates dialog and fragment functionality from various lib* modules.
 */
abstract class BasePageFragment : BaseFragment() {

    companion object {
        private const val RESOURCE_KEY = "res"
        
        /**
         * Create new instance with image resource.
         */
        @JvmStatic
        fun <T : BasePageFragment> newInstance(fragmentClass: Class<T>, resourceId: Int): T {
            return try {
                val fragment = fragmentClass.newInstance()
                val bundle = Bundle()
                bundle.putInt(RESOURCE_KEY, resourceId)
                fragment.arguments = bundle
                fragment
            } catch (e: Exception) {
                throw RuntimeException("Failed to create fragment instance", e)
            }
        }
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupImageView(view)
        initializeFragment(view)
    }

    private fun setupImageView(view: View) {
        val imageViewId = getImageViewId()
        if (imageViewId != 0) {
            val imageView = view.findViewById<ImageView>(imageViewId)
            val resourceId = arguments?.getInt(RESOURCE_KEY, 0) ?: 0
            if (resourceId != 0) {
                imageView?.setImageResource(resourceId)
            }
        }
    }

    /**
     * Override to provide the ImageView resource ID.
     * Return 0 if no ImageView setup is needed.
     */
    protected abstract fun getImageViewId(): Int

    /**
     * Override for additional fragment initialization.
     */
    protected open fun initializeFragment(view: View) {
        // Default implementation - override if needed
    }
}