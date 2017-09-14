package com.mitash.quicknote.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.view.View;

import com.mitash.quicknote.database.DatabaseCreator;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.events.SingleLiveEvent;

import java.util.List;

/**
 * Created by Mitash Gaurh on 9/13/2017.
 */

public class NoteListViewModel extends AndroidViewModel {

    private static final MutableLiveData ABSENT = new MutableLiveData();

    private final SingleLiveEvent<Void> mNewNotesEvent = new SingleLiveEvent<>();

    private final DatabaseCreator dbCreator;

    static {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    private LiveData<List<NoteEntity>> mObservableNotes;

    public NoteListViewModel(Application application) {
        super(application);
        dbCreator = DatabaseCreator.getInstance(this.getApplication());
    }

    public void attach() {
        mObservableNotes = subscribeNotesListObservable();
    }

    private LiveData<List<NoteEntity>> subscribeNotesListObservable() {
        return Transformations.switchMap(dbCreator.isDatabaseCreated(),
                new Function<Boolean, LiveData<List<NoteEntity>>>() {
                    @Override
                    public LiveData<List<NoteEntity>> apply(Boolean isDbCreated) {
                        if (!Boolean.TRUE.equals(isDbCreated)) { // Not needed here, but watch out for null
                            //noinspection unchecked
                            return ABSENT;
                        } else {
                            //noinspection ConstantConditions
                            return dbCreator.getDatabase().noteDao().loadAllNotes();
                        }
                    }
                });
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<NoteEntity>> loadNotes() {
        return mObservableNotes;
    }

    public SingleLiveEvent<Void> getNewNotesEvent() {
        return mNewNotesEvent;
    }

    public void showNewNotesTask() {
        mNewNotesEvent.call();
    }
}
