package com.academy.fundamentals.Movies.Wishlist;

import android.content.Context;
import android.content.SharedPreferences;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Wishlist implements Constants {

    private static ArrayList<MovieDetails> m_wishlist;
    private static Wishlist m_instance;
    private static Gson m_gson;

    public static ArrayList<MovieDetails> getWishlistMovies() {
        return m_wishlist;
    }

    public static boolean containsMovie(MovieDetails item){
        for (MovieDetails movie : m_wishlist) {
            if (item.getMovieId().equals(movie.getMovieId()))
            {
                return true;
            }
        }
        return false;
    }

    public static void saveSharedPrefs(Context context)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        String jsonWishlist = m_gson.toJson(m_wishlist);
        editor.putString(WISHLIST, jsonWishlist);
        editor.apply();
    }


    private static void initSharedPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        m_gson = new Gson();
        String json = prefs.getString(WISHLIST, "null");
        if (!json.equals("null")) {
            Type type = new TypeToken<ArrayList<MovieDetails>>() {}.getType();
            m_wishlist = m_gson.fromJson(json, type);
        }
        else {
            m_wishlist = new ArrayList<>();
        }
    }



    public static Wishlist getInstance(Context context) {
        if (m_instance == null) {
            m_instance = new Wishlist();
            initSharedPrefs(context);
        }
        return m_instance;
    }
}
