package com.mitash.quicknote.view.widget.materialsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitash.quicknote.R;
import com.mitash.quicknote.utils.AnimationUtil;
import com.mitash.quicknote.utils.AppUtils;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mitashgaurh on 10/12/17.
 */
public class MaterialSearchView extends FrameLayout implements Filter.FilterListener, View.OnClickListener {

    private static final String TAG = "MaterialSearchView";

    public static final int REQUEST_VOICE = 9999;

    private boolean mIsSearchOpen = false;

    private int mAnimationDuration;

    private boolean mClearingFocus;

    private FrameLayout mLayoutSearch;

    private RelativeLayout mSearchTopBar;

    private EditText mEtSearchView;

    private ImageButton mBtnActionUp;

    private ImageButton mBtnActionVoice;

    private ImageButton mBtnActionEmpty;

    private View mViewTransparent;

    private ListView mListSuggestion;

    private CharSequence mOldQueryText;

    private CharSequence mUserQuery;

    private OnQueryTextListener mOnQueryChangeListener;

    private SearchViewListener mSearchViewListener;

    private ListAdapter mAdapter;

    private SavedState mSavedState;

    private boolean submit = false;

    private boolean ellipsize = false;

    private boolean allowVoiceSearch;

    private Drawable suggestionIcon;

    private Context mContext;

    public MaterialSearchView(Context context) {
        this(context, null);
    }

    public MaterialSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
                setBackground(a.getDrawable(R.styleable.MaterialSearchView_searchBackground));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColor, 0));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColorHint, 0));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_hint)) {
                setHint(a.getString(R.styleable.MaterialSearchView_android_hint));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchVoiceIcon)) {
                setVoiceIcon(a.getDrawable(R.styleable.MaterialSearchView_searchVoiceIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
                setCloseIcon(a.getDrawable(R.styleable.MaterialSearchView_searchCloseIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(a.getDrawable(R.styleable.MaterialSearchView_searchBackIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionBackground));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionIcon)) {
                setSuggestionIcon(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionIcon));
            }

            if (a.hasValue(R.styleable.MaterialSearchView_android_inputType)) {
                setInputType(a.getInt(R.styleable.MaterialSearchView_android_inputType, EditorInfo.TYPE_NULL));
            }

            a.recycle();
        }
    }

    private void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_search_view, this, true);
        mLayoutSearch = findViewById(R.id.layout_search);

        mSearchTopBar = mLayoutSearch.findViewById(R.id.search_top_bar);
        mEtSearchView = mLayoutSearch.findViewById(R.id.et_search_view);
        mBtnActionUp = mLayoutSearch.findViewById(R.id.btn_action_up);
        mBtnActionEmpty = mLayoutSearch.findViewById(R.id.btn_action_empty);
        mBtnActionVoice = mLayoutSearch.findViewById(R.id.btn_action_voice);
        mViewTransparent = mLayoutSearch.findViewById(R.id.view_transparent);
        mViewTransparent = mLayoutSearch.findViewById(R.id.view_transparent);
        mListSuggestion = mLayoutSearch.findViewById(R.id.list_suggestion);

        mEtSearchView.setOnClickListener(this);
        mBtnActionUp.setOnClickListener(this);
        mBtnActionVoice.setOnClickListener(this);
        mBtnActionEmpty.setOnClickListener(this);
        mViewTransparent.setOnClickListener(this);

        allowVoiceSearch = false;

        showVoice(true);

        initSearchView();

        mListSuggestion.setVisibility(GONE);

        setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);
    }

    private void initSearchView() {
        mEtSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery();
                return true;
            }
        });

        mEtSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserQuery = s;
                startFilter(s);
                MaterialSearchView.this.onTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtSearchView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    AppUtils.showKeyboard(mEtSearchView);
                    showSuggestions();
                }
            }
        });
    }

    private void startFilter(CharSequence s) {
        if (mAdapter != null && mAdapter instanceof Filterable) {
            ((Filterable) mAdapter).getFilter().filter(s, MaterialSearchView.this);
        }
    }

    private void onVoiceClicked() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);    // setting recognition model, optimized for short phrases – search queries
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);    // quantity of results we want to receive
        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent, REQUEST_VOICE);
        }
    }

    private void onTextChanged(CharSequence newText) {
        CharSequence text = mEtSearchView.getText();
        mUserQuery = text;
        boolean hasText = !TextUtils.isEmpty(text);
        if (hasText) {
            mBtnActionEmpty.setVisibility(VISIBLE);
            showVoice(false);
        } else {
            mBtnActionEmpty.setVisibility(GONE);
            showVoice(true);
        }

        if (null != mOnQueryChangeListener && !TextUtils.equals(newText, mOldQueryText)) {
            mOnQueryChangeListener.onQueryTextChange(newText.toString());
        }
        mOldQueryText = newText.toString();
    }

    private void onSubmitQuery() {
        CharSequence query = mEtSearchView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (null == mOnQueryChangeListener || !mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
                closeSearch(false);
                mEtSearchView.setText(null);
            }
        }
    }

    private boolean isVoiceAvailable() {
        if (isInEditMode()) {
            return true;
        }
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return activities.size() == 0;
    }

    //Public Attributes
    @Override
    public void setBackground(Drawable background) {
        mSearchTopBar.setBackground(background);
    }

    @Override
    public void setBackgroundColor(int color) {
        mSearchTopBar.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        mEtSearchView.setTextColor(color);
    }

    public void setHintTextColor(int color) {
        mEtSearchView.setHintTextColor(color);
    }

    public void setHint(CharSequence hint) {
        mEtSearchView.setHint(hint);
    }

    public void setVoiceIcon(Drawable drawable) {
        mBtnActionVoice.setImageDrawable(drawable);
    }

    public void setCloseIcon(Drawable drawable) {
        mBtnActionEmpty.setImageDrawable(drawable);
    }

    public void setBackIcon(Drawable drawable) {
        mBtnActionUp.setImageDrawable(drawable);
    }

    public void setSuggestionIcon(Drawable drawable) {
        suggestionIcon = drawable;
    }

    public void setInputType(int inputType) {
        mEtSearchView.setInputType(inputType);
    }

    public void setSuggestionBackground(Drawable background) {
        mListSuggestion.setBackground(background);
    }

    public void setCursorDrawable(int drawable) {
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(mEtSearchView, drawable);
        } catch (Exception ignored) {
            Log.e(TAG, ignored.toString());
        }
    }

    public void setVoiceSearch(boolean voiceSearch) {
        allowVoiceSearch = voiceSearch;
    }

    //Public Methods

    /**
     * Call this method to show suggestions list. This shows up when adapter is set. Call {@link #setAdapter(ListAdapter)} before calling this.
     */
    public void showSuggestions() {
        if (null != mAdapter && mAdapter.getCount() > 0 && mListSuggestion.getVisibility() == GONE) {
            mListSuggestion.setVisibility(VISIBLE);
        }
    }

    /**
     * Submit the query as soon as the user clicks the item.
     *
     * @param submit submit state
     */
    public void setSubmitOnClick(boolean submit) {
        this.submit = submit;
    }

    /**
     * Set Suggest List OnItemClickListener
     *
     * @param listener onItemClickListener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListSuggestion.setOnItemClickListener(checkNotNull(listener));
    }

    /**
     * Set Adapter for suggestions list. Should implement Filterable.
     *
     * @param adapter adapter attached to list
     */
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        mListSuggestion.setAdapter(adapter);
        startFilter(mEtSearchView.getText());
    }

    /**
     * Set Adapter for suggestions list with the given suggestion array
     *
     * @param suggestions array of suggestions
     */
    public void setSuggestions(String[] suggestions) {
        if (suggestions != null && suggestions.length > 0) {
            mViewTransparent.setVisibility(VISIBLE);
            final SearchAdapter adapter = new SearchAdapter(mContext, suggestions, suggestionIcon, ellipsize);
            setAdapter(adapter);

            setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setQuery((String) adapter.getItem(position), submit);
                }
            });
        } else {
            mViewTransparent.setVisibility(GONE);
        }
    }

    /**
     * Dismiss the suggestions list.
     */
    public void dismissSuggestions() {
        if (mListSuggestion.getVisibility() == VISIBLE) {
            mListSuggestion.setVisibility(GONE);
        }
    }


    /**
     * Calling this will set the query to search text box. if submit is true, it'll submit the query.
     *
     * @param query  text entered
     * @param submit submit query
     */
    public void setQuery(CharSequence query, boolean submit) {
        mEtSearchView.setText(query);
        if (query != null) {
            mEtSearchView.setSelection(mEtSearchView.length());
            mUserQuery = query;
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    /**
     * if show is true, this will enable voice search. If voice is not available on the device, this method call has not effect.
     *
     * @param show boolean for voice input
     */
    public void showVoice(boolean show) {
        if (show && isVoiceAvailable() && allowVoiceSearch) {
            mBtnActionVoice.setVisibility(VISIBLE);
        } else {
            mBtnActionVoice.setVisibility(GONE);
        }
    }

    /**
     * Call this method and pass the menu item so this class can handle click events for the Menu Item.
     *
     * @param menuItem menu item for search view
     */
    public void setMenuItem(MenuItem menuItem) {
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSearch();
                return true;
            }
        });
    }

    /**
     * Return true if search is open
     */
    public boolean isSearchOpen() {
        return mIsSearchOpen;
    }

    /**
     * Sets animation duration. ONLY FOR PRE-LOLLIPOP!!
     *
     * @param duration duration of the animation
     */
    public void setAnimationDuration(int duration) {
        mAnimationDuration = duration;
    }

    /**
     * Open Search View. This will animate the showing of the view.
     */
    public void showSearch() {
        showSearch(true);
    }

    /**
     * Open Search View. If animate is true, Animate the showing of the view.
     *
     * @param animate true for animate
     */
    public void showSearch(boolean animate) {
        if (isSearchOpen()) {
            return;
        }

        //Request Focus
        mEtSearchView.setText(null);
        mEtSearchView.requestFocus();

        if (animate) {
            setVisibleWithAnimation();
        } else {
            mLayoutSearch.setVisibility(VISIBLE);
            if (null != mSearchViewListener) {
                mSearchViewListener.onSearchViewShown();
            }
        }
        mIsSearchOpen = true;
    }

    private void setVisibleWithAnimation() {
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }

            @Override
            public boolean onAnimationEnd(View view) {
                if (mSearchViewListener != null) {
                    mSearchViewListener.onSearchViewShown();
                }
                return false;
            }

            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLayoutSearch.setVisibility(View.VISIBLE);
            AnimationUtil.reveal(mSearchTopBar, animationListener);
        } else {
            AnimationUtil.fadeInView(mLayoutSearch, mAnimationDuration, animationListener);
        }
    }

    private void setHideWithAnimation() {
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }

            @Override
            public boolean onAnimationEnd(View view) {
                if (mSearchViewListener != null) {
                    mSearchViewListener.onSearchViewClosed();
                }
                mLayoutSearch.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimationUtil.conceal(mSearchTopBar, animationListener);
        } else {
            AnimationUtil.fadeOutView(mLayoutSearch, mAnimationDuration, animationListener);
        }
    }

    /**
     * Close search view.
     */
    public void closeSearch(boolean animate) {
        if (!isSearchOpen()) {
            return;
        }

        mEtSearchView.setText(null);
        dismissSuggestions();

        if (animate) {
            setHideWithAnimation();
        } else {
            mLayoutSearch.setVisibility(GONE);
            if (null != mSearchViewListener) {
                mSearchViewListener.onSearchViewClosed();
            }
        }
        clearFocus();
        mIsSearchOpen = false;
    }

    /**
     * Set this listener to listen to Query Change events.
     *
     * @param listener query text listener
     */
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mOnQueryChangeListener = listener;
    }

    /**
     * Set this listener to listen to Search View open and close events
     *
     * @param listener searchView Listener
     */
    public void setOnSearchViewListener(SearchViewListener listener) {
        mSearchViewListener = listener;
    }

    /**
     * Ellipsize suggestions longer than one line.
     *
     * @param ellipsize dots
     */
    public void setEllipsize(boolean ellipsize) {
        this.ellipsize = ellipsize;
    }

    @Override
    public void onFilterComplete(int count) {
        if (count > 0) {
            showSuggestions();
        } else {
            dismissSuggestions();
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept focus if in the middle of clearing focus
        if (mClearingFocus) return false;
        // Check if SearchView is focusable.
        if (!isFocusable()) return false;
        return mEtSearchView.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void clearFocus() {
        mClearingFocus = true;
        AppUtils.hideKeyboard(this);
        super.clearFocus();
        mEtSearchView.clearFocus();
        mClearingFocus = false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        mSavedState = new SavedState(superState);
        mSavedState.query = mUserQuery != null ? mUserQuery.toString() : null;
        mSavedState.isSearchOpen = this.mIsSearchOpen;

        return mSavedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        mSavedState = (SavedState) state;

        if (mSavedState.isSearchOpen) {
            showSearch(false);
            setQuery(mSavedState.query, false);
        }

        super.onRestoreInstanceState(mSavedState.getSuperState());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_action_up:
                closeSearch(true);
                break;

            case R.id.btn_action_voice:
                onVoiceClicked();
                break;

            case R.id.btn_action_empty:
                mEtSearchView.setText(null);
                break;

            case R.id.et_search_view:
                showSuggestions();
                break;

            case R.id.view_transparent:
                closeSearch(true);
                break;
        }
    }

    private static class SavedState extends BaseSavedState {

        String query;
        boolean isSearchOpen;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.query = in.readString();
            this.isSearchOpen = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(query);
            out.writeInt(isSearchOpen ? 1 : 0);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public interface OnQueryTextListener {

        /**
         * Called when the user submits the query. This could be due to a key press on the
         * keyboard or due to pressing a submit button.
         * The listener can override the standard behavior by returning true
         * to indicate that it has handled the submit request. Otherwise return false to
         * let the SearchView handle the submission by launching any associated intent.
         *
         * @param query the query text that is to be submitted
         * @return true if the query has been handled by the listener, false to let the
         * SearchView perform the default action.
         */
        boolean onQueryTextSubmit(String query);

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        boolean onQueryTextChange(String newText);
    }

    public interface SearchViewListener {

        void onSearchViewShown();

        void onSearchViewClosed();
    }


}
