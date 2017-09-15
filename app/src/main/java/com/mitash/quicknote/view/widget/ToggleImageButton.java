package com.mitash.quicknote.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.mitash.quicknote.R;

public class ToggleImageButton extends AppCompatImageView implements Checkable {

    private boolean mIsChecked = false;
    private Drawable mCheckedDrawable;
    private Drawable mUncheckedDrawable;

    public ToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ToggleImageButton);
        mCheckedDrawable = array.getDrawable(R.styleable.ToggleImageButton_checkedDrawable);
        mUncheckedDrawable = array.getDrawable(R.styleable.ToggleImageButton_uncheckedDrawable);
        array.recycle();
        setClickable(true);
        setChecked(mIsChecked);
    }

    @Override
    public void setChecked(boolean checked) {
        mIsChecked = checked;
        if (mIsChecked) {
            if (null != mCheckedDrawable) {
                setImageDrawable(mCheckedDrawable);
            }
        } else {
            if (null != mUncheckedDrawable) {
                setImageDrawable(mUncheckedDrawable);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mIsChecked);
    }
}
