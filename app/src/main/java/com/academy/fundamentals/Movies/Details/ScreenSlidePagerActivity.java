package com.academy.fundamentals.Movies.Details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.R;

import java.util.ArrayList;

public class ScreenSlidePagerActivity extends FragmentActivity  implements Constants {

    private ViewPager m_pager;
    private PagerAdapter m_pagerAdapter;
    private static ArrayList<MovieDetails> m_dataSource;
    private static int m_startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        m_startPosition = getIntent().getIntExtra(SPECIFIC_POSITION,0);
        m_dataSource = getIntent().getParcelableArrayListExtra(DATA_SOURCE);

        // Instantiate a ViewPager and a PagerAdapter.
        m_pager = findViewById(R.id.pager);
        m_pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        m_pager.setAdapter(m_pagerAdapter);
        m_pager.setCurrentItem(m_startPosition);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MovieDetails movie = m_dataSource.get(position);
            MovieDetailsFragment movieDetailsFragment = MovieDetailsFragment.newInstance(movie);

            return movieDetailsFragment;
        }

        @Override
        public int getCount() {
            return m_dataSource.size();
        }
    }
}
