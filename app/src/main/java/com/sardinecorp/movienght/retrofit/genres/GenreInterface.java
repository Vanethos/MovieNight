package com.sardinecorp.movienght.retrofit.genres;

import com.sardinecorp.movienght.retrofit.search.SearchResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gonçalo on 06/06/2017.
 */

public interface GenreInterface {
    @GET("/3/genre/movie/list")
    Observable<Genres> getGenres(@Query("api_key") String apiKey, @Query("language") String language);
}
