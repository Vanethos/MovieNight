package com.sardinecorp.movienght.retrofit.discovery;

import com.sardinecorp.movienght.retrofit.search.SearchResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gonçalo on 07/06/2017.
 */

public interface DiscoverInterface {
    // we need to create two GET methods for Retrofit:
    //  - one for the TV Queries
    //  - one for the Movie Queries
    // ADD ON CHALLENGE - execute the two queries and display the results using RXJava

    // example: https://api.themoviedb.org/3/discover/movie?api_key=dd723c301c346868a15a4f965165a281&primary_release_date.gte=2014-09-15&primary_release_date.lte=2014-10-22
    /* On OPTIONAL QUERY PARAMETERS
     * Retrofit skips null parameters and ignores them while assembling the request. Keep in mind,
     * that you can’t pass null for primitive data types like int, float, long, etc.
     * Instead, use Integer, Float, Long, etc and the compiler won’t be grumpy.
     */


    // fields that I will be using on the query:
    /* MOVIES *
     * primary_release_date.gte - lower boundary of the release date
     * primary_release_date.lte - higher boundary of the release date
     * vote_average.gte - lb of stars
     * vote_average.lte - hb of stars
     * vote_count.gte - minimum number of votes
     * sort_by - popularity.asc, popularity.desc, release_date.asc, release_date.desc
     *           revenue.asc, revenue.desc, primary_release_date.asc, primary_release_date.desc,
     *           vote_average.desc, vote_average.asc, vote_count.desc, vote_count.asc
     * with_genres - comma separated list with int from the list of genres
     *          -> multi choice alert dialog https://android--code.blogspot.pt/2015/08/android-alertdialog-multichoice.html
     */

    @GET("/3/discover/movie")
    Observable<SearchResult> discoverMovie(
            @Query("api_key") String apiKey,
            @Query("primary_release_date.gte") String releaseDateLower,
            @Query("primary_release_date.lte") String releaseDateHigher,
            @Query("vote_average.gte") String voteAverageLowe,
            @Query("vote_average.lte") String voteAverageHigher,
            @Query("vote_count") String minimumVotes,
            @Query("sort_by") String sortBy,
            @Query("with_genres") String genresList
            );

    /* * TV *
     * first_air_date.gte - lower boundary air date
     * first_air_date.lte - higher boundary air date
     * vote_average.gte - lower boundary stars
     * vote_average.gte - higher boundary stars
     * vote_count.gte - minimum number of votes
     * sort_by: vote_average.desc, vote_average.asc, first_air_date.desc, first_air_date.asc,
     *          popularity.desc, popularity.asc
     * with_genres -   comma separated list with int from the list of genres
     *          -> multi choice alert dialog https://android--code.blogspot.pt/2015/08/android-alertdialog-multichoice.html
     */

    @GET("/3/discover/tv")
    Observable<SearchResult> discoverTV(
            @Query("api_key") String apiKey,
            @Query("first_air_date.gte") String releaseDateLower,
            @Query("first_air_date.lte") String releaseDateHigher,
            @Query("vote_average.gte") String voteAverageLowe,
            @Query("vote_average.lte") String voteAverageHigher,
            @Query("vote_count") String minimumVotes,
            @Query("sort_by") String sortBy,
            @Query("with_genres") String genresList
    );
}
