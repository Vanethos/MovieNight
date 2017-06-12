package com.sardinecorp.movienght.retrofit.search;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gonçalo on 01/06/2017.
 */

public interface SearchInterface {

    @GET("/3/search/multi")
    Observable<SearchResult> search(@Query("api_key") String apiKey, @Query("query") String query);

}
