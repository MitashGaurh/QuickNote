package com.mitash.quicknote.view.widget.stickyheader;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

public class StickyHeadersTouchListener implements RecyclerView.OnItemTouchListener {

    private final StickyHeadersDecoration mHeadersDecoration;

    public StickyHeadersTouchListener(StickyHeadersDecoration decor) {
        mHeadersDecoration = decor;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int position = mHeadersDecoration.findHeaderPositionUnder((int) e.getX(), (int) e.getY());
            return position != -1;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent e) {
        // Do nothing.
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // Do nothing.
    }
}