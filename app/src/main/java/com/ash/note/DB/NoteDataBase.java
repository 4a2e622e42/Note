package com.ash.note.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ash.note.Model.Note;

@Database(entities = Note.class,version = 1,exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase
{
    private static final String DbName = "noteDB";
    private static NoteDataBase instance;
    public abstract NoteDao noteDao();


    public static synchronized NoteDataBase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context,NoteDataBase.class,DbName)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }


}
