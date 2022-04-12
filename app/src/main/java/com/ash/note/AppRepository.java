package com.ash.note;

import androidx.lifecycle.LiveData;

import com.ash.note.Data.Note;
import com.ash.note.Data.NoteDao;

import java.util.List;

public class AppRepository
{
    NoteDao noteDao;

    public AppRepository(NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }

    public  LiveData<List<Note>> readAllNote()
    {
       return noteDao.readAllNotes();

    }


    public void addNote(Note note)
    {
        noteDao.addNote(note);
    }



}
