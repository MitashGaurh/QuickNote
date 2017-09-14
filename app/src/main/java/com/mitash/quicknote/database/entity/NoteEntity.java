package com.mitash.quicknote.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mitash.quicknote.model.Note;

import java.util.Date;

/**
 * Created by Mitash Gaurh on 9/9/2017.
 */

@Entity(tableName = "notes")
public class NoteEntity implements Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String noteText;
    private Date createdDate;
    private Date updatedDate;

    public NoteEntity() {

    }

    public NoteEntity(Note note){
        this.id = note.getId();
        this.title = note.getTitle();
        this.noteText = note.getNoteText();
        this.createdDate = note.getCreatedDate();
        this.updatedDate = note.getUpdatedDate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
