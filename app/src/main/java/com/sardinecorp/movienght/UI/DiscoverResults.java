package com.sardinecorp.movienght.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sardinecorp.movienght.R;
import com.sardinecorp.movienght.retrofit.Movie;
import com.sardinecorp.movienght.retrofit.discovery.DiscoverInterface;
import com.sardinecorp.movienght.retrofit.discovery.MoviesAndTV;
import com.sardinecorp.movienght.retrofit.search.SearchResult;
import com.sardinecorp.movienght.utils.APIUtils;
import com.sardinecorp.movienght.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class DiscoverResults extends AppCompatActivity {


    int mType;
    String mGenre;
    String mMinRatings;
    String mMaxRatings;
    String mNumberofRatings;
    String mMinReleaseDate;
    String mMaxReleaseDate;
    int mSortIndex;

    String[] mOutputSortMovies = new String[] {
            "popularity.asc", "popularity.desc",
            "release_date.asc", "release_date.desc",
            "revenue.asc", "revenue.desc",
            "primary_release_date.asc", "primary_release_date.desc",
            "vote_average.desc", "vote_average.asc",
            "vote_count.desc", "vote_count.asc"
    };

    String[] mOutputSortTV = new String[] {
            "popularity.asc", "popularity.desc",
            "first_air_date.asc", "rfirst_air_date.desc",
            "revenue.asc", "revenue.desc",
            "primary_release_date.asc", "primary_release_date.desc",
            "vote_average.desc", "vote_average.asc",
            "vote_count.desc", "vote_count.asc"
    };

    ProgressDialog mSearchProgress;
    DiscoverInterface mDiscoverInterface;

    @BindView(R.id.discoverRecyclerView)
    RecyclerView mDiscoverRecycle;
    @BindView(R.id.emptyDiscoverRecycler)
    View mDiscoverEmptyRecyclerView;


    private CardRecycleAdapter mAdapter;
    private List<Movie> mMovies;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // enable back button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDiscoverInterface = APIUtils.getDiscoverInterface();
        mMovies = new ArrayList<>();

        mAdapter = new CardRecycleAdapter(mMovies, this, getFragmentManager());
        mDiscoverRecycle.setAdapter(mAdapter);

        // create a layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mDiscoverRecycle.setLayoutManager(layoutManager);
        mDiscoverRecycle.setHasFixedSize(true);

        // get the data from the intent
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", 0);
        mGenre = parseStringIntent("genre", intent);
        mMinRatings = parseStringIntent("minRatings", intent);
        mMaxRatings = parseStringIntent("maxRatings", intent);
        mNumberofRatings = parseStringIntent("numberofRatings", intent);
        mMinReleaseDate = parseStringIntent("minReleaseDate", intent);
        mMaxReleaseDate = parseStringIntent("maxReleaseDate", intent);
        mSortIndex = intent.getIntExtra("sort", 0);

        // display a progress dialog that will be exited when the query is complete
        mSearchProgress = new ProgressDialog(DiscoverResults.this);
        mSearchProgress.setMessage("Searching for your next favorite movie");
        mSearchProgress.setIndeterminate(true);
        mSearchProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mSearchProgress.setProgress(50);
        mSearchProgress.setMax(100);
        mSearchProgress.setCanceledOnTouchOutside(false);
        mSearchProgress.show();

        // do the query search with the results that we have
        switch(mType) {
            case 0:
                // movie
                discoverMovie();
                break;
            case 1:
                // tv
                discoverTV();
                break;
            case 2:
                // both
                discoverBoth();
                break;
            default:
                discoverBoth();
                break;
        }
    }

    public void discoverMovie() {
        mDiscoverInterface.discoverMovie(APIUtils.API_KEY, mMinReleaseDate, mMaxReleaseDate, mMinRatings, mMaxRatings, mNumberofRatings,
                mOutputSortMovies[mSortIndex], mGenre).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchResult>() {
                    @Override
                    public void onCompleted() {
                        mSearchProgress.dismiss();
                        disableEmptyView();
                        if (mAdapter.adapterSize() == 0) {
                            noResults();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SearchResult searchResult) {
                        Common.setTypeOfMedia(searchResult, "movie");
                        mAdapter.updateAnswers(searchResult.getResults());
                    }
                });
    }

    public void discoverTV() {
        mDiscoverInterface.discoverTV(APIUtils.API_KEY, mMinReleaseDate, mMaxReleaseDate, mMinRatings, mMaxRatings, mNumberofRatings,
                mOutputSortTV[mSortIndex], mGenre).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchResult>() {
                    @Override
                    public void onCompleted() {
                        
                        mSearchProgress.dismiss();
                        disableEmptyView();
                        if (mAdapter.adapterSize() == 0) {
                            noResults();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSearchProgress.dismiss();
                        Toast.makeText(DiscoverResults.this, "An error has occured", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(SearchResult searchResult) {
                        Common.setTypeOfMedia(searchResult, "tv");
                        mAdapter.updateAnswers(searchResult.getResults());
                    }
                });
    }

    public void discoverBoth() {
        // combine both results
        Observable<MoviesAndTV> combined = Observable.zip(
                mDiscoverInterface.discoverMovie(APIUtils.API_KEY, mMinReleaseDate, mMaxReleaseDate,
                        mMinRatings, mMaxRatings, mNumberofRatings, mOutputSortMovies[mSortIndex], mGenre)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()),
                mDiscoverInterface.discoverTV(APIUtils.API_KEY, mMinReleaseDate, mMaxReleaseDate,
                        mMinRatings, mMaxRatings, mNumberofRatings, mOutputSortTV[mSortIndex], mGenre)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                ,
                new Func2<SearchResult, SearchResult, MoviesAndTV>() {
                    @Override
                    public MoviesAndTV call(SearchResult searchResult, SearchResult searchResult2) {
                        return new MoviesAndTV(searchResult, searchResult2);
                    }
                });

        combined.subscribe(new Subscriber<MoviesAndTV>() {
            @Override
            public void onCompleted() {
                mSearchProgress.dismiss();
                disableEmptyView();
                if (mAdapter.adapterSize() == 0) {
                    noResults();
                }
            }

            @Override
            public void onError(Throwable e) {
                mSearchProgress.dismiss();

            }

            @Override
            public void onNext(MoviesAndTV moviesAndTV) {
                mAdapter.updateAnswers(moviesAndTV);
            }
        });

    }

    private void disableEmptyView () {
        // at the moment, this code would make both views disappear.
//        if (mDiscoverEmptyRecyclerView.getVisibility() == View.VISIBLE) {
//            mDiscoverEmptyRecyclerView.setVisibility(View.GONE);
//        }
    }

    private void noResults() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle("Ups!")
                .setMessage("There are no results for your query. Please try with another query!")
                .setPositiveButton("Ok!", null)
                .create();
        alert.show();
    }

    private String parseStringIntent(String tag, Intent intent) {
        return intent.getStringExtra(tag).equals("null") ? null : intent.getStringExtra(tag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            //NavUtils.navigateUpFromSameTask(this);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
