package com.academy.fundamentals.Movies.Movies.CommonData.Network;

import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieDetailsResponse.MovieDetailsResponse;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieTrailerResponse.MovieTrailerResponse;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.DatabaseConfigurationResponse.DatabaseConfigurationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.DB_CONFIGURATION_PATH;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.DB_POPULAR_PATH;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.DB_SEARCH_PATH;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.DB_TRAILER_PATH;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.MOVIEDB_API_KEY;
import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.MOVIEID_PATH_PARAM;

public interface MoviesService {

    String m_apiKey = MOVIEDB_API_KEY;

    // https://api.themoviedb.org/3/movie/popular?api_key=m_apiKey&page=m_databaseMoviePageToUpload
    @GET(DB_POPULAR_PATH)
    Call<MovieDetailsResponse> getPopularMovies(@Query("api_key") String key, @Query("page") String pageNumber);

    // https://api.themoviedb.org/3/movie/<movie_id>/videos?api_key=m_apiKey
    @GET(DB_TRAILER_PATH)
    Call<MovieTrailerResponse> getMovieTrailerById(@Path(MOVIEID_PATH_PARAM) String id, @Query("api_key") String key);

    // https://api.themoviedb.org/3/configuration?api_key=m_apiKey
    @GET(DB_CONFIGURATION_PATH)
    Call<DatabaseConfigurationResponse> getDBConfiguration(@Query("api_key") String key);

    // https://api.themoviedb.org/3/search/movie?api_key=m_apiKey&page=m_databaseMoviePageToUpload
    @GET(DB_SEARCH_PATH)
    Call<MovieDetailsResponse> getMoviesByName(@Query("api_key") String key, @Query("query") String textToSearch);




}
