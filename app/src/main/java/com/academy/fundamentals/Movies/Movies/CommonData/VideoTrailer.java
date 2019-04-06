package com.academy.fundamentals.Movies.Movies.CommonData;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class VideoTrailer {

    @PrimaryKey
    private int movieId;
    private String id;
    private String key;

    public VideoTrailer(int movieId, String id, String key) {
        this.movieId = movieId;
        this.id = id;
        this.key = key;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
