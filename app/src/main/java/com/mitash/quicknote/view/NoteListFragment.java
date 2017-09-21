package com.mitash.quicknote.view;

import android.app.Activity;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mitash.quicknote.R;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.databinding.FragmentNoteListBinding;
import com.mitash.quicknote.utils.ActivityUtils;
import com.mitash.quicknote.view.adapter.NoteAdapter;
import com.mitash.quicknote.viewmodel.NoteListViewModel;

import java.util.List;

public class NoteListFragment extends LifecycleFragment implements View.OnClickListener {

    private static final String TAG = "NoteListFragment";

    private FragmentNoteListBinding mBinding;

    private NoteListViewModel mNoteListViewModel;

    private NoteAdapter mNoteAdapter;

    private Context mContext;

    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentNoteListBinding.inflate(inflater, container, false);

        setUpViewModel();

        return mBinding.getRoot();
    }

    private void setUpViewModel() {
        mNoteListViewModel = ActivityUtils.obtainViewModel(getActivity(), NoteListViewModel.class);

        mNoteListViewModel.attach();
        mBinding.setViewModel(mNoteListViewModel);
    }

    private void setUpRecyclerView() {
        mNoteAdapter = new NoteAdapter(null);

        mBinding.rvNote.setAdapter(mNoteAdapter);
        mBinding.rvNote.setLayoutManager(new LinearLayoutManager(mContext));

        mBinding.rvNote.setLayoutReference(R.layout.layout_note_list_shimmer);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpRecyclerView();

        mNoteListViewModel.mDataLoading.set(true);

        mNoteListViewModel.mDataAvailable.set(true);

        mBinding.fabAddNote.setOnClickListener(this);

        subscribeView();
    }

    private void subscribeView() {
        // Update the list when the data changes
        mNoteListViewModel.loadNotes().observe(this, new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntity> notes) {
                if (null != notes) {
                    mNoteListViewModel.mDataLoading.set(false);
                    if (!notes.isEmpty()) {
                        mNoteAdapter.swapNoteList(notes);
                        mNoteListViewModel.mDataAvailable.set(true);
                    } else {
                        mNoteListViewModel.mDataAvailable.set(false);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        mNoteListViewModel.addNewNote();
    }
}
