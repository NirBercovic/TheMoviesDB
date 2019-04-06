package com.academy.fundamentals.Movies.Movies.CommonData;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity
public class MovieDetails implements Parcelable {

    @PrimaryKey @NonNull
    private String movieId;
    private String name;
    private String releasedDate;
    private String overview;
    private String sidePosterPath;
    private String bannerPosterPath;
    private double popularity;

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getSidePosterPath() {
        return sidePosterPath;
    }

    public void setSidePosterPath(String sidePosterPath) {
        this.sidePosterPath = sidePosterPath;
    }

    public String getBannerPosterPath() {
        return bannerPosterPath;
    }

    public void setBannerPosterPath(String bannerPosterPath) {
        this.bannerPosterPath = bannerPosterPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(releasedDate);
        dest.writeString(overview);
        dest.writeString(movieId);
        dest.writeString(sidePosterPath);
        dest.writeString(bannerPosterPath);
    }

    public MovieDetails(){}

    private MovieDetails(Parcel in) {
        name = in.readString();
        releasedDate = in.readString();
        overview = in.readString();
        movieId = in.readString();
        sidePosterPath = in.readString();
        bannerPosterPath = in.readString();
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR
            = new Parcelable.Creator<MovieDetails>() {

        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };


}
