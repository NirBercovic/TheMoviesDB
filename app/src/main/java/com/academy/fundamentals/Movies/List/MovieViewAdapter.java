package com.academy.fundamentals.Movies.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.academy.fundamentals.Movies.Details.ScreenSlidePagerActivity;
import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.ItemClickListener;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieDetailsResponse.MovieDetailsResponse;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieDetailsResponse.MovieResult;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.RestClient;
import com.academy.fundamentals.Movies.Wishlist.Wishlist;
import com.academy.fundamentals.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieViewAdapter extends RecyclerView.Adapter<MovieViewAdapter.ViewHolder> implements ItemClickListener, Constants, Filterable {

    private LayoutInflater m_inflater;
    private ArrayList<MovieDetails> m_dataSource, m_dataSourceAfterSearch, m_dataSourceOnScreen;
    private ArrayList<MovieDetails> m_wishlist;
    private MoviesActivity m_context;

    public MovieViewAdapter(MoviesActivity context, ArrayList<MovieDetails> items)
    {
        m_wishlist              = Wishlist.getInstance(context).getWishlistMovies();
        m_dataSource            = items;
        m_inflater              = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_context               = context;
        m_dataSourceOnScreen    = m_dataSource;
    }

    protected void startDetailsActivity(int position) {
        Intent openActivity = new Intent(m_context, ScreenSlidePagerActivity.class);
        openActivity.putExtra(SPECIFIC_POSITION, position);
        openActivity.putParcelableArrayListExtra(DATA_SOURCE, m_dataSourceOnScreen);
        m_context.startActivity(openActivity);
    }

    @Override
    public int getItemCount() {
        return m_dataSourceOnScreen.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = m_inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindViewHolder(m_dataSourceOnScreen.get(position));
        holder.setItemClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        startDetailsActivity(position);
    }

    @Override
    public void onLongClick(View view, int position) {
        MovieDetails movie = m_dataSourceOnScreen.get(position);
        String toastMessage = WISHLIST_ADD;

        if (Wishlist.containsMovie(movie)) {
            toastMessage = WISHLIST_EXIST;
        }
        else
        {
            m_wishlist.add(m_dataSourceOnScreen.get(position));
        }
        Toast.makeText(m_context, movie.getName() + toastMessage, Toast.LENGTH_SHORT).show();

    }

    public void setItems(ArrayList<MovieDetails> items) {
        m_dataSourceOnScreen.clear();
        m_dataSourceOnScreen.addAll(items);
        notifyDataSetChanged();
    }

    private ArrayList<MovieDetails> fetchMoviesListFromResponse(MovieDetailsResponse response) {

        List<MovieResult> results = response.getResults();
        int resultsSize = results.size();

        String imageBaseURL = m_context.m_imagesConfigurationResult.getSecureBaseUrl();
        String sidePosterSize = m_context.m_imagesConfigurationResult.getPosterSizes().get(1);
        String bannerPosterSize = m_context.m_imagesConfigurationResult.getLogoSizes().get(5);

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

    private void getMoviesFromNetworkDB(String query)
    {
        try {
            URLEncoder.encode(query, ENCODED_METHOD);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Call<MovieDetailsResponse> call = RestClient.moviesService.getMoviesByName(MOVIEDB_API_KEY, query);
        try {
            MovieDetailsResponse response = call.execute().body();
            m_dataSourceAfterSearch = fetchMoviesListFromResponse(response);
        } catch (IOException e) {
            // show an error to the user
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public Filter getFilter() {
        return movieFilter;
    }

    private Filter movieFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MovieDetails> filteredList = new ArrayList<>();
            String filterPath = constraint.toString();

            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(m_dataSource);
                m_dataSourceOnScreen = m_dataSource;
            } else if (isNetworkAvailable()) {
                //query DB API
                getMoviesFromNetworkDB(filterPath);
                filteredList.addAll(m_dataSourceAfterSearch);
                m_dataSourceOnScreen = m_dataSourceAfterSearch;
            } else {
                //query local DB

            }

            FilterResults results  = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            setItems((ArrayList<MovieDetails>) results.values);
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final ImageView m_image;
        public final TextView m_tv_title;
        public final TextView m_tv_overview;
        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            m_image = view.findViewById(R.id.iv_smallIcon);
            m_tv_title = view.findViewById(R.id.tv_nameHeadline);
            m_tv_overview = view.findViewById(R.id.tv_overviewText);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void onBindViewHolder(MovieDetails movie) {
            m_tv_title.setText(movie.getName());
            m_tv_overview.setText(movie.getOverview());
            if (!TextUtils.isEmpty(movie.getSidePosterPath())) {
                Picasso.get()
                        .load(movie.getSidePosterPath())
                        .into(m_image);
            }
        }

        public void setItemClickListener(ItemClickListener onClickCallback) {
            itemClickListener = onClickCallback;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onLongClick(v,getAdapterPosition());
            return true;
        }
    }



}
