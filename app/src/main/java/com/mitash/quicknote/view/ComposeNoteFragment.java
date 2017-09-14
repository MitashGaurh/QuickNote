package com.mitash.quicknote.view;


import android.arch.lifecycle.LifecycleFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mitash.quicknote.R;
import com.mitash.quicknote.databinding.FragmentComposeNoteBinding;
import com.mitash.quicknote.editor.Editor;
import com.mitash.quicknote.editor.RichTextEditor;

import java.util.Map;

public class ComposeNoteFragment extends LifecycleFragment implements Editor.EditorListener {

    private FragmentComposeNoteBinding mBinding;

    private Editor mEditor;

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

        int formatRestId = R.layout.layout_format_bar_richtext;

        return mBinding.getRoot();
    }

    @Override
    public void onPageLoaded() {

    }

    @Override
    public void onClickedLink(String title, String url) {

    }

    @Override
    public void onStyleChanged(Editor.Format style, boolean enabled) {

    }

    @Override
    public void onFormatChanged(Map<Editor.Format, Object> enabledFormats) {

    }

    @Override
    public void onCursorChanged(Map<Editor.Format, Object> enabledFormats) {

    }

    @Override
    public void linkTo(String url) {

    }

    @Override
    public void onClickedImage(String url) {

    }
}
