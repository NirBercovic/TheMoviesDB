package com.academy.fundamentals.Movies.Details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.academy.fundamentals.Movies.Downloader.DownloadActivity;
import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.Database.AppDatabase;
import com.academy.fundamentals.Movies.Movies.CommonData.Database.VideoTrailerDao;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieTrailerResponse.MovieTrailerResponse;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.MovieTrailerResponse.TrailerResult;
import com.academy.fundamentals.Movies.Movies.CommonData.Network.RestClient;
import com.academy.fundamentals.Movies.Movies.CommonData.VideoTrailer;
import com.academy.fundamentals.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsFragment extends Fragment implements Constants {

    private MovieDetails                m_movie;
    private ContentLoadingProgressBar   spinnerLoader;
    private VideoTrailerDao             m_DBConnector;
    private Button                      m_trailerButton;
    private TextView                    m_trailerNotFound;

    public static MovieDetailsFragment newInstance(MovieDetails movie) {
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SPECIFIC_DETAILS, movie);
        movieDetailsFragment.setArguments(bundle);

        return movieDetailsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details,container,false);
        m_movie = getArguments().getParcelable(SPECIFIC_DETAILS);
        m_DBConnector = AppDatabase.getInstance(getContext()).videoTrailerDao();

        spinnerLoader       = view.findViewById(R.id.pb_spinner);
        m_trailerButton     = view.findViewById(R.id.btn_trailer);
        m_trailerNotFound   = view.findViewById(R.id.tv_trailerNotFound);

        changeViewAccordingToItem(view);

        return view;
    }

    private void changeViewAccordingToItem(final View view)
    {
        TextView tvDateText,tvOverviewText,tvNameHeadline;
        ImageView ivSidePoster, ivBannerPoster;

        //Get the elements from the view in the order from top to bottom
        ivBannerPoster = view.findViewById(R.id.iv_bannerPoster);
        tvNameHeadline = view.findViewById(R.id.tv_nameHeadline);
        tvDateText = view.findViewById(R.id.tv_dateText);
        ivSidePoster = view.findViewById(R.id.iv_sidePoster);
        tvOverviewText = view.findViewById(R.id.tv_overviewText);

        //change the content of the elements according to the specific movie from the user
        tvNameHeadline.setText(m_movie.getName());
        tvDateText.setText(m_movie.getReleasedDate());
        tvOverviewText.setText(m_movie.getOverview());

        //images
        getImageFromPicasso(m_movie.getBannerPosterPath(), ivBannerPoster);
        getImageFromPicasso(m_movie.getSidePosterPath(), ivSidePoster);

        constructPosterDownloadButton(view, m_movie);

        //Call request just if needed - check DB first
        VideoTrailer trailer = m_DBConnector.getVideo(m_movie.getMovieId());
        if (trailer == null) {
            fetchTrailerFromWeb(view);
        }
        else
        {
            constructTrailerButton(trailer.getKey(), view);
        }
    }

    private void constructPosterDownloadButton(View view, final MovieDetails movie)
    {
        ImageButton ibDownloadPoster = view.findViewById(R.id.ib_downloadPoster);
        final Context context = getContext();
        ibDownloadPoster.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DownloadActivity.startActivity(context, movie);
            }
        });

    }

    private boolean getImageFromPicasso(String imagePath, ImageView imageResource)
    {
        if (!TextUtils.isEmpty(imagePath)) {
            Picasso.get()
                    .load(imagePath)
                    .into(imageResource);
            return true;
        }
        return false;
    }


    private VideoTrailer convertVideoResult(TrailerResult result, int movieId)
    {
        return new VideoTrailer(movieId, result.getId(), result.getKey());
    }

    void fetchTrailerFromWeb(final View view)
    {
        Call<MovieTrailerResponse> call = RestClient.moviesService.getMovieTrailerById(m_movie.getMovieId(), MOVIEDB_API_KEY);
        call.enqueue(new Callback<MovieTrailerResponse>() {

            @Override
            public void onResponse(Call<MovieTrailerResponse> call, Response<MovieTrailerResponse> response) {
                if (response.isSuccessful()) {

                    List<TrailerResult> results = response.body().getResults();
                    if (results != null && !results.isEmpty()) {
                        TrailerResult result = results.get(0);

                        //insert result to DB
                        m_DBConnector.insert(convertVideoResult(result, response.body().getId()));

                        constructTrailerButton(result.getKey(), view);
                    }
                    else
                    {
                        m_trailerNotFound.setVisibility(View.VISIBLE);
                        m_trailerButton.setEnabled(false);
                        spinnerLoader.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieTrailerResponse> call, Throwable t) {
            }
        });

    }

    void constructTrailerButton(String youtubeTrailerSuffix, View view)
    {
        final String videoURL = YOUTUBE_PATH + youtubeTrailerSuffix;
        m_trailerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent youtubeIntent = new Intent();
                youtubeIntent.setAction(Intent.ACTION_VIEW);
                youtubeIntent.setData(Uri.parse(videoURL));

                // Verify that the intent will resolve to an activity
                if (youtubeIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(youtubeIntent);
                }
            }
        });
        spinnerLoader.setVisibility(View.GONE);
    }
}
