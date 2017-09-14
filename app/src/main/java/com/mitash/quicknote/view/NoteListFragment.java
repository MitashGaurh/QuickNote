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
import com.mitash.quicknote.viewmodel.ViewModelFactory;
import com.mitash.quicknote.viewmodel.NoteListViewModel;

import java.util.List;

public class NoteListFragment extends LifecycleFragment {

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
    public void onResume() {
        super.onResume();

        mNoteListViewModel.attach();
        subscribeView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());

        mNoteListViewModel = ViewModelProviders.of(this, factory).get(NoteListViewModel.class);

        mBinding.setIsLoading(true);
        mBinding.setIsDataAvailable(true);
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

        // Subscribe to "new notes" event
        mNoteListViewModel.getNewNotesEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void _) {
                onFabButtonClicked();
            }
        });
    }

    public void onFabButtonClicked() {
        Intent intent = new Intent(getActivity(), ComposeNoteActivity.class);
        startActivity(intent);
    }
}
