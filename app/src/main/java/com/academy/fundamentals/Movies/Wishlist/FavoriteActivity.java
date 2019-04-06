package com.academy.fundamentals.Movies.Wishlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.academy.fundamentals.Movies.Details.ScreenSlidePagerActivity;
import com.academy.fundamentals.Movies.List.MovieViewAdapter;
import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.R;

import java.util.ArrayList;

import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.DATA_SOURCE;

public class FavoriteActivity extends AppCompatActivity implements Constants {

    private FavoriteViewAdapter m_adapter;
    private RecyclerView m_recyclerView;
    private RecyclerView.LayoutManager m_layoutManager;
    private Wishlist m_wishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_wishlist = Wishlist.getInstance(this);
        setContentView(R.layout.activity_favorite);

        m_recyclerView = findViewById(R.id.rv_movies);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(WISHLIST_TITLE);
        setSupportActionBar(toolbar);
        if (!m_wishlist.getWishlistMovies().isEmpty())
        {
            TextView text = findViewById(R.id.tv_empty_wishlist);
            text.setVisibility(View.GONE);
        }

        m_layoutManager = new LinearLayoutManager(this);
        m_recyclerView.setLayoutManager(m_layoutManager);
        m_adapter = new FavoriteViewAdapter(this);
        m_recyclerView.setAdapter(m_adapter);

    }

    protected void startDetailsActivity(int position) {
        Intent openActivity = new Intent(this, ScreenSlidePagerActivity.class);
        openActivity.putExtra(SPECIFIC_POSITION, position);
        openActivity.putParcelableArrayListExtra(DATA_SOURCE, m_wishlist.getWishlistMovies());
        startActivity(openActivity);
    }


    @Override
    protected void onPause() {
        m_wishlist.saveSharedPrefs(this);

        super.onPause();
    }
}
