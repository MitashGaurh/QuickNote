/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mitash.quicknote.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.mitash.quicknote.database.NoteDatabase.DATABASE_NAME;

/**
 * Creates the {@link NoteDatabase} asynchronously, exposing a LiveData object to notify of creation.
 */
public class DatabaseCreator {

    private static final String TAG = "DatabaseCreator";

    private static DatabaseCreator sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    private NoteDatabase mNoteDatabase;

    private final AtomicBoolean mInitializing = new AtomicBoolean(true);

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    public synchronized static DatabaseCreator getInstance(Application application) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new DatabaseCreator(application);
                }
            }
        }
        return sInstance;
    }

    private DatabaseCreator(Application application) {
        createDb(application);
    }

    /**
     * Used to observe when the database initialization is done
     */
    public LiveData<Boolean> isDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    @Nullable
    public NoteDatabase getDatabase() {
        return mNoteDatabase;
    }

    /**
     * Creates or returns a previously-created database.
     * <p>
     * Although this uses an AsyncTask which currently uses a serial executor, it's thread-safe.
     */
    private void createDb(Context context) {

        Log.d(TAG, "Creating DB from " + Thread.currentThread().getName());

        if (!mInitializing.compareAndSet(true, false)) {
            return; // Already initializing
        }

        mIsDatabaseCreated.setValue(false);// Trigger an update to show a loading screen.
        new AsyncTask<Context, Void, Void>() {

            @Override
            protected Void doInBackground(Context... params) {
                Log.d(TAG, "Starting bg job " + Thread.currentThread().getName());

                Context context = params[0].getApplicationContext();

                // Build the database!
                NoteDatabase database = NoteDatabase.getAppDatabase(context);

                // Add a delay to simulate a long-running operation
                addDelay();

                mNoteDatabase = database;
                return null;
            }

            @Override
            protected void onPostExecute(Void ignored) {
                // Now on the main thread, notify observers that the db is created and ready.
                mIsDatabaseCreated.setValue(true);
            }
        }.execute(context.getApplicationContext());
    }

    private void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
        }
    }
}
