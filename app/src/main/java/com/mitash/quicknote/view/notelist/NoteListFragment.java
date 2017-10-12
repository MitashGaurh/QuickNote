package com.mitash.quicknote.view.notelist;

import android.app.Activity;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mitash.quicknote.R;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.databinding.FragmentNoteListBinding;
import com.mitash.quicknote.model.Note;
import com.mitash.quicknote.utils.ActivityUtils;
import com.mitash.quicknote.view.stickyheader.DividerDecoration;
import com.mitash.quicknote.view.stickyheader.RecyclerItemTouchHelper;
import com.mitash.quicknote.view.stickyheader.StickyHeadersDecoration;
import com.mitash.quicknote.view.stickyheader.StickyHeadersTouchListener;
import com.mitash.quicknote.viewmodel.NoteListViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteListFragment extends LifecycleFragment implements NoteActionListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = "NoteListFragment";

    private FragmentNoteListBinding mBinding;

    private NoteListViewModel mNoteListViewModel;

    private NoteAdapter mNoteAdapter;

    private Context mContext;

    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    @SuppressWarnings("deprecation")
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
        mNoteAdapter.setNoteActionListener(this);

        mBinding.rvNote.setAdapter(mNoteAdapter);
        mBinding.rvNote.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mBinding.rvNote.setLayoutReference(R.layout.layout_note_list_shimmer);

        // Add the sticky headers decoration
        final StickyHeadersDecoration headersDecor = new StickyHeadersDecoration(mNoteAdapter);
        mBinding.rvNote.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        mBinding.rvNote.addItemDecoration(new DividerDecoration(mContext));

        // Add touch listeners
        mBinding.rvNote.addOnItemTouchListener(new StickyHeadersTouchListener(headersDecor));

        mNoteAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mBinding.rvNote);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpRecyclerView();

        mNoteListViewModel.mDataLoading.set(true);

        mNoteListViewModel.mDataAvailable.set(true);

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
                        Collections.sort(notes, new Comparator<NoteEntity>() {
                            @Override
                            public int compare(NoteEntity o1, NoteEntity o2) {
                                if (o2.getUpdatedDate().getTime() > o1.getUpdatedDate().getTime()) {
                                    return 1;
                                } else if (o2.getUpdatedDate().getTime() < o1.getUpdatedDate().getTime()) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
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
    public void onNoteClicked(Note note) {
        mNoteListViewModel.getViewNoteEvent().setValue(note.getId());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NoteAdapter.SectionViewHolder) {
            // get the removed item name to display it in snack bar
            String name = mNoteAdapter.getNote(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final NoteEntity deletedItem = mNoteAdapter.getNote(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mNoteAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar.make(mBinding.fabAddNote, name + getString(R.string.text_note_deleted), Snackbar.LENGTH_LONG)
                    .setAction(R.string.text_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            mNoteAdapter.restoreItem(deletedItem, deletedIndex);
                        }
                    }).setActionTextColor(Color.YELLOW).show();
        }
    }
}
