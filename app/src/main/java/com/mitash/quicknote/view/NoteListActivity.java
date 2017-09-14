package com.mitash.quicknote.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mitash.quicknote.R;
import com.mitash.quicknote.utils.ActivityUtils;

public class NoteListActivity extends AppCompatActivity {

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
    }
}
