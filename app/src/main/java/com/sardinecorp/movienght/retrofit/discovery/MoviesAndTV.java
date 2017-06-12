package com.sardinecorp.movienght.retrofit.discovery;

import com.sardinecorp.movienght.retrofit.search.SearchResult;

/**
 * Created by Gonçalo on 11/06/2017.
 */

public class MoviesAndTV {
    // this class is used to combine the results from both discovery queries at the same time

    public SearchResult mTvResult;
    public SearchResult mMovieResult;

    public MoviesAndTV(SearchResult movieResult, SearchResult tvResult) {
        mTvResult = tvResult;
        mMovieResult = movieResult;
    }
}
