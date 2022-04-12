package com.ash.note.Hilt;

import android.content.Context;

import com.ash.note.Data.NoteDao;
import com.ash.note.Data.NoteDataBase;

import javax.inject.Singleton;

import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@dagger.Module
@InstallIn(SingletonComponent.class)
public class RoomModule
{

    @Provides
    @Singleton
    NoteDataBase provideNoteDataBase(@ApplicationContext Context context)
    {
        return NoteDataBase.getInstance(context);
    }

    @Provides
    @Singleton
    NoteDao provideNoteDao(NoteDataBase noteDataBase)
    {
        return noteDataBase.noteDao();
    }




}
