package com.mitash.quicknote.view;

import android.databinding.BindingAdapter;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.mitash.quicknote.database.converter.DateConverter;
import com.mitash.quicknote.view.shimmer.ShimmerRecyclerView;

import org.joda.time.format.DateTimeFormat;

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

    @BindingAdapter("dateText")
    public static void dateToText(TextView textView, Date date) {
        textView.setText(DateTimeFormat.forPattern("dd-MMM-yy").print(DateConverter.toTimestamp(date)));
    }

    @SuppressWarnings("deprecation")
    @BindingAdapter("htmlText")
    public static void htmlToText(TextView textView, String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString());
        } else {
            textView.setText(Html.fromHtml(html).toString());
        }
    }
}
