package com.ash.note.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao
{
    @Insert
    void addNote(Note... note);


    @Query("SELECT * FROM noteTable")
    LiveData<List<Note>> readAllNotes();


    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);








}
