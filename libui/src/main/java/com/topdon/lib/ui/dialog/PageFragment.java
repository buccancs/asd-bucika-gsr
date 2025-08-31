package com.topdon.lib.ui.dialog;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.topdon.lib.core.dialog.BasePageFragment;
import com.topdon.lib.ui.R;

/**
 * Backward compatibility wrapper for PageFragment functionality.
 * Delegates to the consolidated BasePageFragment implementation.
 * 
 * This wrapper eliminates duplicate dialog fragment code while maintaining
 * full backward compatibility with existing libui usage.
 */
public class PageFragment extends BasePageFragment {

    public static PageFragment newInstance(int res) {
        return BasePageFragment.newInstance(PageFragment.class, res);
    }

    @Override
    protected int getImageViewId() {
        return R.id.img;
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_page;
    }

    @Override
    public void initView() {
        // Original implementation preserved
    }

    @Override
    public void initData() {
        // Original implementation preserved  
    }
}
