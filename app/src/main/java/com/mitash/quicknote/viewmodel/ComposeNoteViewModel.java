package com.mitash.quicknote.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.mitash.quicknote.database.DatabaseCreator;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.events.SingleLiveEvent;

import java.util.Date;

/**
 * Created by Mitash Gaurh on 9/13/2017.
 */

public class ComposeNoteViewModel extends AndroidViewModel {

    private static final MutableLiveData ABSENT = new MutableLiveData();

    private final DatabaseCreator mDbCreator;

    public final ObservableField<String> mNoteTitle = new ObservableField<>();

    public final ObservableField<String> mNoteContent = new ObservableField<>();

    private final SingleLiveEvent<Void> mSaveNoteEvent = new SingleLiveEvent<>();

    public final ObservableBoolean mEditingEnabled = new ObservableBoolean(false);

    private LiveData<NoteEntity> mObservableNote;

    static {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    ComposeNoteViewModel(Application application) {
        super(application);
        mDbCreator = DatabaseCreator.getInstance(this.getApplication());
    }

    public void attach(int noteId) {
        mObservableNote = subscribeNoteObservable(noteId);
    }

    private LiveData<NoteEntity> subscribeNoteObservable(final int noteId) {
        return Transformations.switchMap(mDbCreator.isDatabaseCreated(),
                new Function<Boolean, LiveData<NoteEntity>>() {
                    @Override
                    public LiveData<NoteEntity> apply(Boolean isDbCreated) {
                        if (!Boolean.TRUE.equals(isDbCreated)) { // Not needed here, but watch out for null
                            //noinspection unchecked
                            return ABSENT;
                        } else {
                            //noinspection ConstantConditions
                            return mDbCreator.getDatabase().noteDao().loadNote(noteId);
                        }
                    }
                });
    }

    /**
     * Expose the LiveData Note query so the UI can observe it.
     */
    public LiveData<NoteEntity> loadNote() {
        return mObservableNote;
    }

    public SingleLiveEvent<Void> getSaveNoteEvent() {
        return mSaveNoteEvent;
    }

    public void callSaveEvent() {
        mSaveNoteEvent.call();
    }

    public void saveNote() {
        final NoteEntity note = new NoteEntity();
        note.setTitle(mNoteTitle.get());
        note.setNoteText(mNoteContent.get());
        note.setCreatedDate(new Date());
        note.setUpdatedDate(new Date());
        if (null != mDbCreator.getDatabase()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mDbCreator.getDatabase().noteDao().insertAll(note);
                }
            }).start();
        }
    }
}
