package com.mitash.quicknote.view;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mitash.quicknote.R;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.databinding.FragmentNoteListBinding;
import com.mitash.quicknote.utils.ActivityUtils;
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
        mBinding = FragmentNoteListBinding.inflate(inflater, container, false);

        mNoteListViewModel = ActivityUtils.obtainViewModel(getActivity(), NoteListViewModel.class);

        mNoteListViewModel.attach();

        mBinding.setViewmodel(mNoteListViewModel);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.rvNote.setLayoutReference(R.layout.layout_note_list_shimmer);

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
                if (null != notes && !notes.isEmpty()) {
                    Log.d(TAG, "onChanged: count " + notes.size());
                    mBinding.setIsDataAvailable(true);
                } else {
                    mBinding.setIsDataAvailable(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        mNoteListViewModel.addNewNote();
    }
}
