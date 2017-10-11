package com.mitash.quicknote.view;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.support.design.widget.FloatingActionButton;
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

    @BindingAdapter("fabGone")
    public static void showHideFab(FloatingActionButton fab, boolean show) {
        if (show) {
            fab.show();
        } else {
            fab.hide();
        }
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
    public static void createContent(TextView textView, Date date, String content) {
        String dateString = TimeUtils.toSlashFormat(DateConverter.toTimestamp(date));
        SpannableStringBuilder stringBuilder =
                HtmlUtils.toSpannableText(dateString, "#00BCD4");

        stringBuilder.append(" ").append(HtmlUtils.htmlToText(content));

        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor("#80000000"));
        stringBuilder.setSpan(fcs, dateString.length(), stringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        textView.setText(stringBuilder);
    }

    @BindingAdapter("createHeader")
    public static void createHeader(TextView textView, Date date) {
        textView.setText(TimeUtils.toMonthFormat(DateConverter.toTimestamp(date)));
    }

    @SuppressWarnings("deprecation")
    @BindingAdapter("htmlText")
    public static void htmlToText(TextView textView, String html) {
        textView.setText(HtmlUtils.htmlToText(html));
    }
}
