package com.academy.fundamentals.Movies.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import com.academy.fundamentals.Movies.Details.ScreenSlidePagerActivity;
import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.Database.AppDatabase;
import com.academy.fundamentals.Movies.Movies.CommonData.Database.MovieDao;
import com.academy.fundamentals.Movies.Movies.CommonData.Database.VideoTrailerDao;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.DatabaseConfigurationResponse.DatabaseConfigurationResponse;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.DatabaseConfigurationResponse.ImageResult;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieDetailsResponse.MovieDetailsResponse;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieDetailsResponse.MovieResult;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.RestClient;
import com.academy.fundamentals.Movies.Services.ServicesActivity;
import com.academy.fundamentals.Movies.Threads.AsyncTask.AsyncActivity;
import com.academy.fundamentals.Movies.Threads.ThreadHandler.ThreadsActivity;
import com.academy.fundamentals.Movies.Wishlist.FavoriteActivity;
import com.academy.fundamentals.Movies.Wishlist.Wishlist;
import com.academy.fundamentals.R;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesActivity extends AppCompatActivity implements Constants, SearchView.OnQueryTextListener {

    ArrayList<MovieDetails> m_dataSources = new ArrayList<>();
    private RecyclerView m_recyclerView;
    private MovieViewAdapter m_adapter;
    private RecyclerView.LayoutManager m_layoutManager;
    public ImageResult m_imagesConfigurationResult;
    private ContentLoadingProgressBar spinnerLoader;
    static private int m_databaseMoviePageToUpload;
    private MovieDao m_MovieDBConnector;
    private VideoTrailerDao m_VideoTrailerDBConnector;
    private Stack<Boolean> backStack;
    private Wishlist m_wishlist;
    private Timer m_timer;
    private Context m_context;
    private final long DELAY = 1000;

    @Override
    public void onBackPressed() {
        if (!backStack.empty())
        {
            updateScreenWithNewMovies(backStack.pop());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_context = this;
        backStack = new Stack<>();
        m_timer = new Timer();
        m_wishlist = Wishlist.getInstance(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        m_databaseMoviePageToUpload = prefs.getInt(LAST_PAGE_OBSERVED, 1);

        m_MovieDBConnector = AppDatabase.getInstance(this).movieDao();
        m_VideoTrailerDBConnector = AppDatabase.getInstance(this).videoTrailerDao();

        setContentView(R.layout.activity_movies);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Progressbar Spinner
        spinnerLoader = findViewById(R.id.pb_spinner);

        m_recyclerView = findViewById(R.id.rv_movies);
        m_layoutManager = new LinearLayoutManager(this);
        m_recyclerView.setLayoutManager(m_layoutManager);

        m_recyclerView.setOnTouchListener(new OnSwipeTouchListener(MoviesActivity.this) {

            public void onSwipeRight() {
                //Toast.makeText(MoviesActivity.this, "right", Toast.LENGTH_SHORT).show();
                updateScreenWithNewMovies(false);
                backStack.push(true);
            }
            public void onSwipeLeft() {
                //Toast.makeText(MoviesActivity.this, "left", Toast.LENGTH_SHORT).show();
                updateScreenWithNewMovies(true);
                backStack.push(false);
            }
        });

        renderUIFromCache();
        renderUIFromWeb();
    }

    @Override
    protected void onPause() {

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(LAST_PAGE_OBSERVED, m_databaseMoviePageToUpload);
        editor.apply();

        m_wishlist.saveSharedPrefs(this);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.sv_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                m_adapter.setItems(m_dataSources);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        Intent openActivity = null;
        boolean isCleanDatabase = false, isStartActivity = false;

        switch (itemId)
        {
            case R.id.async:
                isStartActivity = true;
                openActivity = new Intent(this, AsyncActivity.class);
                break;

            case R.id.thread:
                isStartActivity = true;
                openActivity = new Intent(this, ThreadsActivity.class);
                break;

            case R.id.service:
                isStartActivity = true;
                openActivity = new Intent(this, ServicesActivity.class);
                break;

            case R.id.favorite:
                isStartActivity = true;
                openActivity = new Intent(this, FavoriteActivity.class);
                break;

            case R.id.clean_database:
                isCleanDatabase = true;
                break;

            default:
        }

        if (isCleanDatabase)
        {
            cleanDatabase();
        }
        else if (isStartActivity){
            startActivity(openActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    private void cleanDatabase() {
        //Clean all Database
        m_MovieDBConnector.deleteAllMovies();
        m_VideoTrailerDBConnector.deleteAllVideoTrailers();
    }

    private void updateScreenWithNewMovies(boolean updateForward){

        boolean isUpdateNeeded = false;

        if (updateForward && m_databaseMoviePageToUpload != MAX_DB_PAGE)
        {
            isUpdateNeeded = true;
            m_databaseMoviePageToUpload++;
        }
        else if (!updateForward && m_databaseMoviePageToUpload != MIN_DB_PAGE)
        {
            isUpdateNeeded = true;
            m_databaseMoviePageToUpload--;
        }

        if (isUpdateNeeded)
        {
            spinnerLoader.setVisibility(View.VISIBLE);
            renderUIFromCache();
            renderUIFromWeb();
        }
    }

    //page 1 = 0 to 19 m_databaseMoviePageToUpload = 1
    //page 2 = 20 to 39 m_databaseMoviePageToUpload = 2
    //page 3 = 40 to 59 m_databaseMoviePageToUpload = 3
    //(( m_databaseMoviePageToUpload - 1 ) * 20 )
    private int calculateMoviesRangeToQuery() {
        return ((m_databaseMoviePageToUpload - 1) * RESULTS_PER_PAGE);
    }

    private void renderUIFromCache() {
        int fromRaw = calculateMoviesRangeToQuery();
        ArrayList<MovieDetails> cachedMovies = (ArrayList)m_MovieDBConnector.getMoviesFromRange(fromRaw, RESULTS_PER_PAGE);
        if (cachedMovies != null)
        {
            if (m_adapter == null) {
                m_adapter = new MovieViewAdapter(this, cachedMovies);
                m_recyclerView.setAdapter(m_adapter);
            }
            else {
                m_adapter.setItems(cachedMovies);
            }
        }
    }

    private void renderUIFromWeb() {
        Call<DatabaseConfigurationResponse> call = RestClient.moviesService.getDBConfiguration(MOVIEDB_API_KEY);
        call.enqueue(new Callback<DatabaseConfigurationResponse>() {

            @Override
            public void onResponse(Call<DatabaseConfigurationResponse> call, Response<DatabaseConfigurationResponse> response){
                if (response.isSuccessful()) {

                    m_imagesConfigurationResult = response.body().getImageResult();
                    searchPopularMovies();
                }
            }

            @Override
            public void onFailure(Call<DatabaseConfigurationResponse> call, Throwable t) {

            }
        });
    }

    private void searchPopularMovies() {
        String pageNumber = String.valueOf(m_databaseMoviePageToUpload);
        Call<MovieDetailsResponse> call = RestClient.moviesService.getPopularMovies(MOVIEDB_API_KEY, pageNumber);
        call.enqueue(new Callback<MovieDetailsResponse>(){

            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response){
                if (response.isSuccessful()) {

                    m_dataSources = fetchMoviesListFromResponse(response);

                    initRecyclerView();
                }

            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Call fail", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private ArrayList<MovieDetails> fetchMoviesListFromResponse(Response<MovieDetailsResponse> response) {
        //initial the movies object with the amount of elements in the response (instead of 3)
        List<MovieResult> results = response.body().getResults();
        int resultsSize = results.size();

        String imageBaseURL = m_imagesConfigurationResult.getSecureBaseUrl();
        String sidePosterSize = m_imagesConfigurationResult.getPosterSizes().get(1);
        String bannerPosterSize = m_imagesConfigurationResult.getLogoSizes().get(5);

        ArrayList<MovieDetails> movies = new ArrayList<>(resultsSize);

        for (MovieResult result : results)
        {
            final MovieDetails movie = new MovieDetails();

            //Texts
            movie.setName(result.getTitle());
            movie.setReleasedDate(result.getReleaseDate());
            movie.setOverview(result.getOverview());

            //Images
            String posterPath = imageBaseURL + sidePosterSize + result.getPosterPath();
            movie.setSidePosterPath(posterPath);
            String bannerPath = imageBaseURL + bannerPosterSize + result.getBackdropPath();
            movie.setBannerPosterPath(bannerPath);

            //Trailer
            String movieId = String.valueOf(result.getId());
            movie.setMovieId(movieId);

            movie.setPopularity(result.getPopularity());
            movies.add(movie);
        }

        return movies;
    }

    private void initRecyclerView() {

        spinnerLoader.setVisibility(View.GONE);

        if (m_adapter == null) {
            m_adapter = new MovieViewAdapter(this, m_dataSources);
            m_recyclerView.setAdapter(m_adapter);
        } else {
            m_adapter.setItems(m_dataSources);
        }
        m_MovieDBConnector.insertAll(m_dataSources);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        m_timer.cancel();
        m_timer = new Timer();
        m_timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        m_adapter.getFilter().filter(newText);
                    }
                },
                DELAY
        );


        return true;
    }
}
