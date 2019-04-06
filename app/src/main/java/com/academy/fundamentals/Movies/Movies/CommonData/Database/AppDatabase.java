package com.academy.fundamentals.Movies.Movies.CommonData.Database;


import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.Movies.Movies.CommonData.VideoTrailer;

@Database(entities = {MovieDetails.class, VideoTrailer.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase implements Constants {

    private static AppDatabase INSTANCE;

    public abstract MovieDao movieDao();
    public abstract VideoTrailerDao videoTrailerDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {


            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }








    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
