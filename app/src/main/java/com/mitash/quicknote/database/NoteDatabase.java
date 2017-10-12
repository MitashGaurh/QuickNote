package com.mitash.quicknote.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.mitash.quicknote.database.converter.DateConverter;
import com.mitash.quicknote.database.dao.NoteDao;
import com.mitash.quicknote.database.entity.NoteEntity;

/**
 * Created by Mitash Gaurh on 9/9/2017.
 */

@Database(entities = {NoteEntity.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class NoteDatabase extends RoomDatabase {

    static final String DATABASE_NAME = "quick-note-db";

    private static NoteDatabase INSTANCE;

    public static NoteDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext()
                    , NoteDatabase.class, DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract NoteDao getNoteDao();
}
