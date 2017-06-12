package com.sardinecorp.movienght.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;

import com.sardinecorp.movienght.retrofit.Movie;
import com.sardinecorp.movienght.retrofit.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonçalo on 05/06/2017.
 */

public class Common {
    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    // check if there is any internet connection
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    // since on the discover api we do not get the "type" of media as a parameter, we add it a posteriori.
    public static SearchResult setTypeOfMedia(SearchResult result, String type) {
        List<Movie> movies = new ArrayList<>(result.getResults());

        movies = setListTypeOfMedia(movies, type);

        result.setResults(movies);

        return result;
    }

    public static List<Movie> setListTypeOfMedia(List<Movie> movies, String type) {
        for (Movie movie : movies) {
            movie.setMediaType(type);
        }
        return movies;
    }

}
