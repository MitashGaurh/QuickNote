package com.mitash.quicknote.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mitash.quicknote.database.entity.NoteEntity;

import java.util.List;

/**
 * Created by Mitash Gaurh on 9/9/2017.
 */

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    LiveData<List<NoteEntity>> loadAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(NoteEntity... notes);

    @Update
    void updateAll(NoteEntity... notes);

    @Delete
    void deleteAll(NoteEntity... notes);

    @Query("select * from notes where id = :noteId")
    LiveData<NoteEntity> loadNote(int noteId);

    @Query("select * from notes where id = :noteId")
    NoteEntity loadNoteSync(int noteId);
}
