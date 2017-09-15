package com.mitash.quicknote.view;


import android.animation.ObjectAnimator;
import android.arch.lifecycle.LifecycleFragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mitash.quicknote.R;
import com.mitash.quicknote.databinding.FragmentComposeNoteBinding;
import com.mitash.quicknote.databinding.LayoutFormatBarRichTextBinding;
import com.mitash.quicknote.editor.Editor;
import com.mitash.quicknote.editor.RichTextEditor;

import java.util.Map;

public class ComposeNoteFragment extends LifecycleFragment implements Editor.EditorListener, View.OnClickListener {

    private static final String TAG = "ComposeNoteFragment";

    private FragmentComposeNoteBinding mBinding;

    private Editor mEditor;

    private boolean mIsEditingEnabled = true;

    private LayoutFormatBarRichTextBinding mFormatBarBinding;

    public static ComposeNoteFragment newInstance() {
        return new ComposeNoteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_note, container, false);

        mEditor = new RichTextEditor(this);

        mEditor.init(mBinding.webViewEditor);

        int formatRestId = R.layout.layout_format_bar_rich_text;

        mFormatBarBinding = DataBindingUtil.inflate(inflater, formatRestId, mBinding.editorBarContainer, false);

        mBinding.editorBarContainer.addView(mFormatBarBinding.getRoot(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));

        mBinding.setIsEditingEnabled(true);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for (int i = 0; i < mFormatBarBinding.layoutFormatButtons.getChildCount(); i++) {
            mFormatBarBinding.layoutFormatButtons.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onPageLoaded() {
        Log.d(TAG, "onPageLoaded: ");
        mEditor.setEditingEnabled(mIsEditingEnabled);
    }

    @Override
    public void onClickedLink(String title, String url) {
        Log.d(TAG, "onClickedLink: ");
    }

    @Override
    public void onStyleChanged(final Editor.Format style, final boolean enabled) {
        Log.d(TAG, "onStyleChanged: ");
        mFormatBarBinding.btnBold.post(new Runnable() {
            @Override
            public void run() {
                switch (style) {
                    case BOLD:
                        mFormatBarBinding.btnBold.setChecked(enabled);
                        break;
                    case ITALIC:
                        mFormatBarBinding.btnItalic.setChecked(enabled);
                        break;
                    case ORDERED_LIST:
                        mFormatBarBinding.btnOrderList.setChecked(enabled);
                        break;
                    case BULLET_LIST:
                        mFormatBarBinding.btnUnOrderList.setChecked(enabled);
                        break;
                }
            }
        });
    }

    @Override
    public void onFormatChanged(final Map<Editor.Format, Object> enabledFormats) {
        Log.d(TAG, "onFormatChanged: ");
        mFormatBarBinding.btnBold.post(new Runnable() {
            @Override
            public void run() {
                refreshFormatStatus(enabledFormats);
            }
        });
    }

    @Override
    public void onCursorChanged(final Map<Editor.Format, Object> enabledFormats) {
        Log.d(TAG, "onCursorChanged: ");
        mFormatBarBinding.btnBold.post(new Runnable() {
            @Override
            public void run() {
                mFormatBarBinding.btnBold.setChecked(false);
                mFormatBarBinding.btnItalic.setChecked(false);

                mFormatBarBinding.btnOrderList.setChecked(false);
                mFormatBarBinding.btnUnOrderList.setChecked(false);

                mFormatBarBinding.btnQuote.setChecked(false);
                mFormatBarBinding.btnLink.setChecked(false);

                refreshFormatStatus(enabledFormats);
            }
        });
    }

    @Override
    public void linkTo(String url) {
        Log.d(TAG, "linkTo: ");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClickedImage(String url) {
        Log.d(TAG, "onClickedImage: ");
    }

    private void refreshFormatStatus(Map<Editor.Format, Object> formatStatus) {
        Log.d(TAG, "refreshFormatStatus: ");
        for (Map.Entry<Editor.Format, Object> entry : formatStatus.entrySet()) {
            switch (entry.getKey()) {
                case BOLD:
                    mFormatBarBinding.btnBold.setChecked((Boolean) entry.getValue());
                    break;
                case ITALIC:
                    mFormatBarBinding.btnItalic.setChecked((Boolean) entry.getValue());
                    break;
                case ORDERED_LIST:
                    mFormatBarBinding.btnOrderList.setChecked((Boolean) entry.getValue());
                    break;
                case BULLET_LIST:
                    mFormatBarBinding.btnUnOrderList.setChecked((Boolean) entry.getValue());
                    break;
                case BLOCK_QUOTE:
                    mFormatBarBinding.btnQuote.setChecked((Boolean) entry.getValue());
                    break;
                case LINK:
                    Object linkValue = entry.getValue();
                    mFormatBarBinding.btnLink.setChecked(null != linkValue);
                    mFormatBarBinding.btnLink.setTag(linkValue);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bold:
                mFormatBarBinding.btnBold.toggle();
                mEditor.toggleBold();
                break;

            case R.id.btn_italic:
                mFormatBarBinding.btnItalic.toggle();
                mEditor.toggleItalic();
                break;

            case R.id.btn_quote:
                mFormatBarBinding.btnQuote.toggle();
                mEditor.toggleQuote();
                break;

            case R.id.btn_order_list:
                mFormatBarBinding.btnOrderList.toggle();
                mEditor.toggleOrderList();
                break;

            case R.id.btn_un_order_list:
                mFormatBarBinding.btnUnOrderList.toggle();
                mEditor.toggleUnOrderList();
                break;

            case R.id.btn_undo:
                mEditor.undo();
                break;

            case R.id.btn_redo:
                mEditor.redo();
                break;

            case R.id.btn_link:
                mFormatBarBinding.btnLink.toggle();
                if (mFormatBarBinding.btnLink.isChecked()) {
                    showEditLinkPanel(mFormatBarBinding.btnLink);
                }
        }
    }

    private void showEditLinkPanel(View anchorView) {
        View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.layout_link_popup, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final EditText linkEt = (EditText) contentView.findViewById(R.id.et_link);
        TextView multipleLinksTv = (TextView) contentView.findViewById(R.id.tv_multiple_links);
        TextView confirmTv = (TextView) contentView.findViewById(R.id.tv_confirm);
        TextView clearTv = (TextView) contentView.findViewById(R.id.tv_clear);
        final PopupWindow popupWindow = new PopupWindow(contentView);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.ComposeTheme_PopUpAnimation);

        if (mFormatBarBinding.btnLink.isChecked()) {
            Object status = mFormatBarBinding.btnLink.getTag();
            if (null == status) {
                return;
            }
            boolean canEdit = status instanceof String;
            linkEt.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            confirmTv.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            multipleLinksTv.setVisibility(canEdit ? View.GONE : View.VISIBLE);
            if (canEdit) {
                linkEt.setText((String) mFormatBarBinding.btnLink.getTag());
                linkEt.setSelection(linkEt.getText().length());
            }
        }
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mEditor.updateLink("", linkEt.getText().toString());
            }
        });
        clearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mEditor.removeLink();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFormatBarBinding.btnLink.setChecked(false);
            }
        });

        int[] tempLocation = new int[2];
        anchorView.getLocationOnScreen(tempLocation);
        int measure = contentView.getMeasuredHeight();
        int offsetY = tempLocation[1] - measure;
        popupWindow.showAtLocation(anchorView, Gravity.TOP | GravityCompat.START, 0, offsetY);
    }
}
