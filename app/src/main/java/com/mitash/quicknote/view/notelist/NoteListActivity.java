package com.mitash.quicknote.view.notelist;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.mitash.quicknote.LifeCycleAppCompatActivity;
import com.mitash.quicknote.R;
import com.mitash.quicknote.utils.ActivityUtils;
import com.mitash.quicknote.view.composenote.ComposeNoteActivity;
import com.mitash.quicknote.viewmodel.NoteListViewModel;

import static com.mitash.quicknote.view.NoteComposeType.VIEW_NOTE;

public class NoteListActivity extends LifeCycleAppCompatActivity {

    private NoteListViewModel mNoteListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.text_notes));

        if (null == savedInstanceState) {
            NoteListFragment noteListFragment = NoteListFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager()
                    , noteListFragment
                    , R.id.frame_container);
        }

        mNoteListViewModel = ActivityUtils.obtainViewModel(this, NoteListViewModel.class);

        subscribeToNoteEvents();
    }

    private void subscribeToNoteEvents() {
        mNoteListViewModel.getNewNoteEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                addNewNote();
            }
        });

        mNoteListViewModel.getViewNoteEvent().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                viewNote(integer);
            }
        });
    }

    private void addNewNote() {
        Intent intent = new Intent(this, ComposeNoteActivity.class);
        startActivity(intent);
    }

    private void viewNote(Integer noteId) {
        Intent intent = new Intent(this, ComposeNoteActivity.class);
        intent.putExtra(ComposeNoteActivity.EXTRA_NOTE_ID, noteId);
        intent.putExtra(ComposeNoteActivity.EXTRA_COMPOSE_TYPE, VIEW_NOTE);
        startActivity(intent);
    }
}
