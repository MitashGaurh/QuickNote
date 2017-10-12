package com.mitash.quicknote.view.widget.materialsearch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.mitash.quicknote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitash Gaurh on 9/19/2017.
 */
class SearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<String> mData;

    private String[] mSuggestions;

    private Drawable mSuggestionIcon;

    private LayoutInflater mLayoutInflater;

    private boolean mIsEllipsize;

    public SearchAdapter(Context context, String[] suggestions) {
        mLayoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        this.mSuggestions = suggestions;
    }

    SearchAdapter(Context context, String[] suggestions, Drawable suggestionIcon, boolean ellipsize) {
        mLayoutInflater = LayoutInflater.from(context);
        mData = Lists.newArrayList();
        this.mSuggestions = suggestions;
        this.mSuggestionIcon = suggestionIcon;
        this.mIsEllipsize = ellipsize;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {

                    List<String> searchData = new ArrayList<>();

                    for (String string : mSuggestions) {
                        if (string.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            searchData.add(string);
                        }
                    }

                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    mData = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SuggestionsViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_search_suggest, parent, false);
            viewHolder = new SuggestionsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SuggestionsViewHolder) convertView.getTag();
        }

        viewHolder.mTvSuggestion.setText((String) getItem(position));
        if (mIsEllipsize) {
            viewHolder.mTvSuggestion.setSingleLine();
            viewHolder.mTvSuggestion.setEllipsize(TextUtils.TruncateAt.END);
        }

        return convertView;
    }

    private class SuggestionsViewHolder {

        final TextView mTvSuggestion;

        SuggestionsViewHolder(View view) {
            mTvSuggestion = view.findViewById(R.id.tv_suggestion);

            if (mSuggestionIcon != null) {
                mTvSuggestion.setCompoundDrawablesWithIntrinsicBounds(mSuggestionIcon, null, null, null);
            }
        }
    }
}
