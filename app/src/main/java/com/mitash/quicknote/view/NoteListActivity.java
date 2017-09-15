package com.mitash.quicknote.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mitash.quicknote.R;
import com.mitash.quicknote.events.LifecycleAppCompatActivity;
import com.mitash.quicknote.utils.ActivityUtils;
import com.mitash.quicknote.viewmodel.NoteListViewModel;
import com.mitash.quicknote.viewmodel.ViewModelFactory;

public class NoteListActivity extends LifecycleAppCompatActivity {

    //private NoteListViewModel mViewModel = null;

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

//        mViewModel = obtainViewModel(this);
//        // Subscribe to "new notes" event
//        mViewModel.getNewNotesEvent().observe(this, new Observer<Void>() {
//            @Override
//            public void onChanged(@Nullable Void _) {
//                //onFabButtonClicked();
//            }
//        });

    }

//    public static NoteListViewModel obtainViewModel(FragmentActivity activity) {
//        // Use a Factory to inject dependencies into the ViewModel
//        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
//
//        NoteListViewModel viewModel =
//                ViewModelProviders.of(activity, factory).get(NoteListViewModel.class);
//
//        return viewModel;
//    }
}
