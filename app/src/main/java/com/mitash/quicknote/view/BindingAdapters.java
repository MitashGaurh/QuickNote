package com.mitash.quicknote.view;

import android.databinding.BindingAdapter;
import android.view.View;

import com.mitash.quicknote.view.shimmer.ShimmerRecyclerView;

/**
 * Created by Mitash Gaurh on 9/7/2017.
 */

public class BindingAdapters {

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("shimmerGone")
    public static void showHideShimmer(ShimmerRecyclerView shimmerRecyclerView, boolean show) {
        if (show) {
            shimmerRecyclerView.showShimmerAdapter();
        } else {
            shimmerRecyclerView.hideShimmerAdapter();
        }
    }
}
