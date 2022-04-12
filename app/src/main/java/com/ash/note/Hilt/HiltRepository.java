package com.ash.note.Hilt;

import com.ash.note.AppRepository;
import com.ash.note.Data.NoteDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class HiltRepository
{


    @Provides
    @Singleton
    AppRepository provideAppRepository(NoteDao noteDao)
    {
        return new AppRepository(noteDao);
    }



}
