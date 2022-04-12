package com.ash.note;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ash.note.Data.Note;
import com.ash.note.Data.NoteDao;
import com.ash.note.Data.NoteDataBase;

import java.util.List;

import javax.inject.Inject;

public class AppViewModel extends AndroidViewModel
{
    Application application;
    @Inject
    AppRepository appRepository;

    @Inject
    public AppViewModel(@NonNull Application application)
    {
        super(application);
        this.application = application;

    }

    public void addNote(Note note)
    {
        appRepository.addNote(note);
    }

    public LiveData<List<Note>> readAllNotes()
    {
        return appRepository.readAllNote();
    }
}
