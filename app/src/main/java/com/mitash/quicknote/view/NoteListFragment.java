package com.mitash.quicknote.view;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mitash.quicknote.R;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.databinding.FragmentNoteListBinding;
import com.mitash.quicknote.viewmodel.NoteListViewModel;

import java.util.List;

public class NoteListFragment extends LifecycleFragment implements View.OnClickListener {

    private static final String TAG = "NoteListFragment";

    private FragmentNoteListBinding mBinding;

    private NoteListViewModel mNoteListViewModel;

    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_list, container, false);

        mBinding.rvNote.setLayoutReference(R.layout.layout_note_list_shimmer);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNoteListViewModel = ViewModelProviders.of(this).get(NoteListViewModel.class);
        mBinding.setIsLoading(true);
        mBinding.setIsDataAvailable(true);

        mBinding.fabAddNote.setOnClickListener(this);

        subscribeView();
    }

    private void subscribeView() {
        // Update the list when the data changes
        mNoteListViewModel.loadNotes().observe(this, new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntity> notes) {
                mBinding.setIsLoading(false);
                if (null != notes) {
                    Log.d(TAG, "onChanged: count " + notes.size());
                } else {
                    mBinding.setIsDataAvailable(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ComposeNoteActivity.class);
        startActivity(intent);
    }
}
