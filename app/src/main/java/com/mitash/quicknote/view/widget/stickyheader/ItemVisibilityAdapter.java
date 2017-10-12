package com.mitash.quicknote.view.widget.stickyheader;

public interface ItemVisibilityAdapter {

    /**
     * Return true the specified adapter position is visible, false otherwise
     * <p>
     * The implementation of this method will typically return true if
     * the position is between the layout manager's findFirstVisibleItemPosition
     * and findLastVisibleItemPosition (inclusive).
     *
     * @param position the adapter position
     */
    boolean isPositionVisible(final int position);
}