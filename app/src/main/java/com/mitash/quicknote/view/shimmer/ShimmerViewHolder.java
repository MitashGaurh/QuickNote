package com.mitash.quicknote.view.shimmer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mitash.quicknote.R;


/**
 * Created by sharish on 22/11/16.
 */

public class ShimmerViewHolder extends RecyclerView.ViewHolder {

    public ShimmerViewHolder(LayoutInflater inflater, ViewGroup parent, int innerViewResId) {
        super(inflater.inflate(R.layout.place_holder_shimmer, parent, false));
        ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) itemView;

        inflater.inflate(innerViewResId, shimmerFrameLayout, true);
    }

    /**
     * Binds the view
     */
    public void bind() {
        ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) itemView;
        shimmerFrameLayout.startShimmerAnimation();
    }
}