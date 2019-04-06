package com.academy.fundamentals.Movies.Movies.CommonData.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.QUERY_DELETE_ALL_MOVIES;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.QUERY_SELECT_MOVIES_AND_ORDER_BY_POPULARITY;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.QUERY_SELECT_TWENTY_MOVIES_AND_ORDER_BY_POPULARITY;

@Dao
public interface MovieDao {

    //Select all movieDetails from table and order by popularity in descending order
    @Query(QUERY_SELECT_MOVIES_AND_ORDER_BY_POPULARITY)
    List<MovieDetails> getAll();

    //Select specific 20 movieDetails from table and order by popularity in descending order
    @Query(QUERY_SELECT_TWENTY_MOVIES_AND_ORDER_BY_POPULARITY)
    List<MovieDetails> getMoviesFromRange(int fromRow, int numberOfRows);

    //Insert all movieDetails to the table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Collection<MovieDetails> movies);

    //Delete all movieDetails from table
    @Query(QUERY_DELETE_ALL_MOVIES)
    void deleteAllMovies();






}
