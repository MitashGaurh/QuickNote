package com.mitash.quicknote.view;


import android.arch.lifecycle.LifecycleFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mitash.quicknote.R;
import com.mitash.quicknote.databinding.FragmentComposeNoteBinding;
import com.mitash.quicknote.databinding.LayoutFormatBarRichTextBinding;
import com.mitash.quicknote.editor.Editor;
import com.mitash.quicknote.editor.RichTextEditor;
import com.mitash.quicknote.utils.ActivityUtils;
import com.mitash.quicknote.utils.DialogUtils;
import com.mitash.quicknote.viewmodel.ComposeNoteViewModel;

import java.util.Map;

public class ComposeNoteFragment extends LifecycleFragment implements Editor.EditorListener, View.OnClickListener {

    private static final String TAG = "ComposeNoteFragment";

    private FragmentComposeNoteBinding mBinding;

    private Editor mEditor;

    private boolean mIsEditingEnabled = true;

    private LayoutFormatBarRichTextBinding mFormatBarBinding;

    private ComposeNoteViewModel mComposeNoteViewModel;

    public static ComposeNoteFragment newInstance() {
        return new ComposeNoteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentComposeNoteBinding.inflate(inflater, container, false);

        mComposeNoteViewModel = ActivityUtils.obtainViewModel(getActivity(), ComposeNoteViewModel.class);

        mEditor = new RichTextEditor(this);

        mEditor.init(mBinding.webViewEditor);

        mFormatBarBinding = LayoutFormatBarRichTextBinding.inflate(inflater, mBinding.editorBarContainer, false);

        mBinding.editorBarContainer.addView(mFormatBarBinding.getRoot(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));

        mBinding.setViewModel(mComposeNoteViewModel);

        mComposeNoteViewModel.mEditingEnabled.set(true);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for (int i = 0; i < mFormatBarBinding.layoutFormatButtons.getChildCount(); i++) {
            mFormatBarBinding.layoutFormatButtons.getChildAt(i).setOnClickListener(this);
        }

        mFormatBarBinding.btnLink.setTag("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_compose, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_save:
                mComposeNoteViewModel.callSaveEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                    case STRIKE_THROUGH:
                        mFormatBarBinding.btnStrikeThrough.setChecked(enabled);
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
                mFormatBarBinding.btnStrikeThrough.setChecked(false);

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

    @Override
    public void onTitleChanged(String title) {
        mComposeNoteViewModel.mNoteTitle.set(title);
    }

    @Override
    public void onContentChanged(String content) {
        mComposeNoteViewModel.mNoteContent.set(content);
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
                case STRIKE_THROUGH:
                    mFormatBarBinding.btnStrikeThrough.setChecked((Boolean) entry.getValue());
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

            case R.id.btn_strike_through:
                mFormatBarBinding.btnStrikeThrough.toggle();
                mEditor.toggleStrikeThrough();
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
                    DialogUtils.showEditLinkPanel(mFormatBarBinding.btnLink, mEditor);
                }
        }
    }


}
