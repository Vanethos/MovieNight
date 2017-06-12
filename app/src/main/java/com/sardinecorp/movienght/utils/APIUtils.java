package com.sardinecorp.movienght.utils;

import android.util.Log;

import com.sardinecorp.movienght.UI.CardRecycleAdapter;
import com.sardinecorp.movienght.retrofit.RetrofitClient;
import com.sardinecorp.movienght.retrofit.discovery.DiscoverInterface;
import com.sardinecorp.movienght.retrofit.genres.Genre;
import com.sardinecorp.movienght.retrofit.genres.GenreInterface;
import com.sardinecorp.movienght.retrofit.genres.Genres;
import com.sardinecorp.movienght.retrofit.search.SearchInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gonçalo on 04/06/2017.
 */

public class APIUtils {

    public static final String BASE_URL = "https://api.themoviedb.org/";
    public static final String API_KEY = "dd723c301c346868a15a4f965165a281";

    public static SearchInterface getSearchInterface() {
        return RetrofitClient.getClient(BASE_URL).create(SearchInterface.class);
    }

    public static DiscoverInterface getDiscoverInterface() {
        return RetrofitClient.getClient(BASE_URL).create(DiscoverInterface.class);
    }

    public static HashMap<String, Integer> genreIdMap() {
        final HashMap<String, Integer> result = new HashMap<String, Integer >();
        GenreInterface genreInterface = RetrofitClient.getClient(BASE_URL).create(GenreInterface.class);
        genreInterface.getGenres(API_KEY, "en-US").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Genres>() {
                    @Override
                    public void onCompleted() {
//                        if (adapter != null) {
//                            adapter.setListOfGenres(result);
//                        }
                        Log.d("main", "task completed - list of genres");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Genres genres) {
                        List<Genre> listOfGenres = genres.getGenres();
                        for (Genre genre: listOfGenres) {
                            result.put(genre.getName(), genre.getId());
                            Log.d("main", "ID: "+genre.getId()+" Name: "+genre.getName());
                        }
                    }
                });
        return result;
    }

}
