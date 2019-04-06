package com.academy.fundamentals.Movies.Movies.CommonData.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.academy.fundamentals.Movies.Movies.CommonData.VideoTrailer;

import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.QUERY_DELETE_ALL_MOVIES;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.QUERY_DELETE_ALL_TRAILERS;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.QUERY_SELECT_TRAILER_OF_MOVIEID;

@Dao
public interface VideoTrailerDao {

    //Select video trailer of movieId
    @Query(QUERY_SELECT_TRAILER_OF_MOVIEID)
    VideoTrailer getVideo(String movieId);

    //Insert videoTrailer row to the VideoTrailer table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VideoTrailer videoTrailer);

    //Delete all videoTrailer from table
    @Query(QUERY_DELETE_ALL_TRAILERS)
    void deleteAllVideoTrailers();
}
