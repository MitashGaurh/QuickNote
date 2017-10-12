package com.mitash.quicknote.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.widget.Toast;

import com.mitash.quicknote.R;
import com.mitash.quicknote.database.DatabaseCreator;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.events.SingleLiveEvent;
import com.mitash.quicknote.utils.HtmlUtils;

import java.util.Date;

/**
 * Created by Mitash Gaurh on 9/13/2017.
 */

public class ComposeNoteViewModel extends AndroidViewModel {

    private static final String TAG = "ComposeNoteViewModel";

    private static final MutableLiveData ABSENT = new MutableLiveData();

    private final DatabaseCreator mDbCreator;

    public final ObservableField<String> mNoteTitle = new ObservableField<>();

    public final ObservableField<String> mNoteContent = new ObservableField<>();

    private final SingleLiveEvent<Void> mSaveNoteEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mUpdateNoteEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mEditNoteEvent = new SingleLiveEvent<>();

    public final ObservableBoolean mFrameEnabled = new ObservableBoolean(false);

    public final ObservableBoolean mFabEnabled = new ObservableBoolean(false);

    public final ObservableBoolean mViewSubscribed = new ObservableBoolean(false);

    public final ObservableField<NoteEntity> mNoteToUpdate = new ObservableField<>();

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
                            return mDbCreator.getDatabase().getNoteDao().loadNote(noteId);
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

    public SingleLiveEvent<Void> getUpdateNoteEvent() {
        return mUpdateNoteEvent;
    }

    public SingleLiveEvent<Void> getEditNoteEvent() {
        return mEditNoteEvent;
    }

    public void callSaveEvent() {
        mSaveNoteEvent.call();
    }

    public void callUpdateEvent() {
        mUpdateNoteEvent.call();
    }

    public void saveNote(Context context) {
        final NoteEntity note = new NoteEntity();
        String title = context.getString(R.string.text_untitled_note);
        if (null != mNoteTitle.get() && !mNoteTitle.get().equals("")) {
            title = HtmlUtils.htmlToText(mNoteTitle.get());
        } else {
            mNoteTitle.set(title);
        }
        note.setTitle(title);
        if (null != mNoteContent.get() && !mNoteContent.get().equals("")) {
            note.setNoteText(mNoteContent.get());
        } else {
            Toast.makeText(context, R.string.text_error_empty_note, Toast.LENGTH_SHORT).show();
            return;
        }
        note.setCreatedDate(new Date());
        note.setUpdatedDate(new Date());

        if (null != mDbCreator.getDatabase()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    attach(mDbCreator.getDatabase().getNoteDao().insertAll(note).get(0).intValue());
                }
            }).start();
        }
    }

    public void editNote() {
        mEditNoteEvent.call();
    }

    public void updateNote() {
        final NoteEntity noteEntity = mNoteToUpdate.get();
        if (null != mNoteTitle.get() && !mNoteTitle.get().equals("")) {
            noteEntity.setTitle(mNoteTitle.get());
        }
        if (null != mNoteContent.get() && !mNoteContent.get().equals("")) {
            noteEntity.setNoteText(mNoteContent.get());
        }
        noteEntity.setUpdatedDate(new Date());

        if (null != mDbCreator.getDatabase()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mDbCreator.getDatabase().getNoteDao().updateAll(noteEntity);
                }
            }).start();
        }
    }
}
