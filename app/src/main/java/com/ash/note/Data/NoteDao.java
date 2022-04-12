package com.ash.note.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addNote(Note note);


    @Query("SELECT * FROM note_table ORDER BY uid ASC")
     LiveData<List<Note>> readAllNotes();





}
