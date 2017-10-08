package com.mitash.quicknote.view.notelist;

import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.model.Note;

/**
 * Created by Mitash Gaurh on 10/7/2017.
 */

public interface NoteActionListener {

    void onNoteClicked(Note note);
}
