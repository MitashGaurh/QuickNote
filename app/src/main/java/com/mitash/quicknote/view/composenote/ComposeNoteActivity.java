package com.mitash.quicknote.view.composenote;

import android.arch.lifecycle.Observer;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.mitash.quicknote.LifeCycleAppCompatActivity;
import com.mitash.quicknote.R;
import com.mitash.quicknote.utils.ActivityUtils;
import com.mitash.quicknote.viewmodel.ComposeNoteViewModel;

public class ComposeNoteActivity extends LifeCycleAppCompatActivity {

    public static final String EXTRA_NOTE_ID = "NOTE_ID";
    public static final String EXTRA_COMPOSE_TYPE = "COMPOSE_TYPE";

    private ComposeNoteViewModel mComposeNoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (null == savedInstanceState) {
            ComposeNoteFragment composeNoteFragment = ComposeNoteFragment.newInstance(getIntent().getExtras());

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager()
                    , composeNoteFragment
                    , R.id.frame_container);
        }

        mComposeNoteViewModel = ActivityUtils.obtainViewModel(this, ComposeNoteViewModel.class);

        // Subscribe to "save note" event
        mComposeNoteViewModel.getSaveNoteEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                mComposeNoteViewModel.saveNote();
                finish();
            }
        });
    }
}
