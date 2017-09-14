package com.mitash.quicknote.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.mitash.quicknote.database.DatabaseCreator;
import com.mitash.quicknote.database.entity.NoteEntity;

import java.util.List;

/**
 * Created by Mitash Gaurh on 9/13/2017.
 */

public class NoteListViewModel extends AndroidViewModel {

    private static final MutableLiveData ABSENT = new MutableLiveData();

    static {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    private final LiveData<List<NoteEntity>> mObservableNotes;

    public NoteListViewModel(Application application) {
        super(application);

        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance();

        LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();

        mObservableNotes = Transformations.switchMap(databaseCreated,
                new Function<Boolean, LiveData<List<NoteEntity>>>() {
                    @Override
                    public LiveData<List<NoteEntity>> apply(Boolean isDbCreated) {
                        if (!Boolean.TRUE.equals(isDbCreated)) { // Not needed here, but watch out for null
                            //noinspection unchecked
                            return ABSENT;
                        } else {
                            //noinspection ConstantConditions
                            return databaseCreator.getDatabase().noteDao().loadAllNotes();
                        }
                    }
                });

        databaseCreator.createDb(this.getApplication());
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<NoteEntity>> loadNotes() {
        return mObservableNotes;
    }
}
