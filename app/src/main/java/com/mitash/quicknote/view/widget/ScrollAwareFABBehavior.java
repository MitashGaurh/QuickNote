package com.mitash.quicknote.view.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mitashgaurh on 10/12/17.
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    public boolean onStartNestedScroll(CoordinatorLayout parent, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               FloatingActionButton child, View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 || dyConsumed < 0 && child.isShown()) {
            child.hide();
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target) {
        child.show();
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }
}
