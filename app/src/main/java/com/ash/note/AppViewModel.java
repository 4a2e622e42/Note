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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

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


