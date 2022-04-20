package com.ash.note;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.ash.note.Data.Note;
import com.ash.note.Data.NoteDao;
import com.ash.note.Data.NoteDataBase;

import java.util.List;

public class AppRepository
{
    public NoteDao noteDao;
    public LiveData<List<Note>> readAllNote;

    public AppRepository(Application application)
    {
        NoteDataBase noteDataBase = NoteDataBase.getInstance(application);

        noteDao = noteDataBase.noteDao();
        readAllNote = noteDao.readAllNotes();
    }

    public void addNotes(Note note)
    {
        noteDao.addNote(note);
    }

    public void updateNote(Note note)
    {
        noteDao.updateNote(note);
    }




}
