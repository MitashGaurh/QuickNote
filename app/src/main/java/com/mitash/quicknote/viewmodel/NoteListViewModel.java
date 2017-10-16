package com.mitash.quicknote.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import com.mitash.quicknote.database.DatabaseCreator;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.events.SingleLiveEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mitash Gaurh on 9/13/2017.
 */

public class NoteListViewModel extends AndroidViewModel {

    private static final MutableLiveData ABSENT = new MutableLiveData();

    public final ObservableBoolean mDataLoading = new ObservableBoolean(false);

    public final ObservableBoolean mDataAvailable = new ObservableBoolean(false);

    private final SingleLiveEvent<Void> mNewNoteEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Integer> mViewNoteEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<String> mFilterNoteEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<NoteEntity> mDeleteNoteEvent = new SingleLiveEvent<>();

    private final DatabaseCreator mDbCreator;

    private LiveData<List<NoteEntity>> mObservableNotes;

    static {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    NoteListViewModel(Application application) {
        super(application);
        mDbCreator = DatabaseCreator.getInstance(this.getApplication());
    }

    public void attach() {
        mObservableNotes = subscribeNoteListObservable();
    }

    private LiveData<List<NoteEntity>> subscribeNoteListObservable() {
        return Transformations.switchMap(mDbCreator.isDatabaseCreated(),
                new Function<Boolean, LiveData<List<NoteEntity>>>() {
                    @Override
                    public LiveData<List<NoteEntity>> apply(Boolean isDbCreated) {
                        if (!Boolean.TRUE.equals(isDbCreated)) { // Not needed here, but watch out for null
                            //noinspection unchecked
                            return ABSENT;
                        } else {
                            //noinspection ConstantConditions
                            return mDbCreator.getDatabase().getNoteDao().loadAllNotes();
                        }
                    }
                });
    }

    /**
     * Expose the LiveData Notes query so the UI can observe it.
     */
    public LiveData<List<NoteEntity>> loadNotes() {
        return mObservableNotes;
    }

    public SingleLiveEvent<Void> getNewNoteEvent() {
        return mNewNoteEvent;
    }

    public SingleLiveEvent<Integer> getViewNoteEvent() {
        return mViewNoteEvent;
    }

    public SingleLiveEvent<String> getFilterNoteEvent() {
        return mFilterNoteEvent;
    }

    public SingleLiveEvent<NoteEntity> getDeleteNoteEvent() {
        return mDeleteNoteEvent;
    }

    public void addNewNote() {
        mNewNoteEvent.call();
    }

    public void callDeleteEvent(NoteEntity noteEntity) {
        mDeleteNoteEvent.setValue(noteEntity);
    }

    public void callFilterEvent(String query) {
        mFilterNoteEvent.setValue(query);
    }

    public void deleteNote(final NoteEntity noteEntity) {
        if (null != mDbCreator.getDatabase()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mDbCreator.getDatabase().getNoteDao().deleteAll(noteEntity);
                }
            }).start();
        }
    }
}
