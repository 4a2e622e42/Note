package com.ash.note.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ash.note.Model.Note;
import com.ash.note.Repository.AppRepository;

import java.util.List;

import javax.inject.Inject;

public class AppViewModel extends AndroidViewModel
{

    public LiveData<List<Note>> readAllNote;
    public AppRepository appRepository;

    @Inject
    public AppViewModel(@NonNull Application application)
    {
        super(application);
        appRepository = new AppRepository(application);

        readAllNote = appRepository.readAllNote;

    }

    public void addNotes(Note note)
    {
        appRepository.addNotes(note);
    }

    public void updateNote(Note note)
    {
        appRepository.updateNote(note);
    }

    public void deleteNote(Note note)
    {
        appRepository.deleteNote(note);
    }





}


