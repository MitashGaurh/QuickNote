package com.mitash.quicknote.view;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.TextView;

import com.mitash.quicknote.database.converter.DateConverter;
import com.mitash.quicknote.utils.HtmlUtils;
import com.mitash.quicknote.utils.TimeUtils;
import com.mitash.quicknote.view.shimmer.ShimmerRecyclerView;

import java.util.Date;

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

    @BindingAdapter({"bind:dateText", "bind:content"})
    public static void dateToText(TextView textView, Date date, String content) {
        textView.setText(TimeUtils.toSlashFormat(DateConverter.toTimestamp(date)) + " " + HtmlUtils.htmlToText(content));
    }

    @SuppressWarnings("deprecation")
    @BindingAdapter("htmlText")
    public static void htmlToText(TextView textView, String html) {
        textView.setText(HtmlUtils.htmlToText(html));
    }
}
