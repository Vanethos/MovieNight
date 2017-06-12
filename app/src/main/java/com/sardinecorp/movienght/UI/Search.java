package com.sardinecorp.movienght.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sardinecorp.movienght.R;
import com.sardinecorp.movienght.retrofit.Movie;
import com.sardinecorp.movienght.retrofit.search.SearchInterface;
import com.sardinecorp.movienght.retrofit.search.SearchResult;
import com.sardinecorp.movienght.utils.APIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class Search extends AppCompatActivity {



    @BindView(R.id.searchButton)
    ImageButton mSearchButton;
    @BindView(R.id.searchEditText)
    EditText mSearchEditText;
    @BindView(R.id.searchRecyclerView)
    RecyclerView mSearchRecyclerView;
    @BindView(R.id.emptySearchrecycler)
    View mEmptyRecyclerView;

    private ProgressDialog mSearchProgress;
    private SearchInterface mSearchInterface;
    private List<Movie> mMovies;
    private CardRecycleAdapter mAdapter;

    private Boolean mCreatedDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // enable back button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set up the Retrofit interface
        mSearchInterface = APIUtils.getSearchInterface();

        mMovies = new ArrayList<>();

        // set up the recycler view adapter
        mAdapter = new CardRecycleAdapter(mMovies, this, getFragmentManager());
        mSearchRecyclerView.setAdapter(mAdapter);

        // create a layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mSearchRecyclerView.setLayoutManager(layoutManager);
        mSearchRecyclerView.setHasFixedSize(true);


        // when pressing enter on the keyboard we will perform a search
        mSearchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KEYCODE_ENTER) {
                    searchText();
                }
                return false;
            }
        });
    }

    @OnClick(R.id.searchButton)
    public void searchText() {
        if (mSearchEditText.getText().toString().trim().length()>0) {
            // create a progress dialog
            mSearchProgress = new ProgressDialog(Search.this);
            mSearchProgress.setMessage("Searching for your next favorite movie");
            mSearchProgress.setIndeterminate(true);
            mSearchProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mSearchProgress.setProgress(50);
            mSearchProgress.setMax(100);
            mSearchProgress.setCanceledOnTouchOutside(false);
            mSearchProgress.show();

            searchQuery(mSearchEditText.getText().toString(), mSearchProgress);

            // we can create a new dialog on error
            mCreatedDialog = false;
        }
    }

    public void searchQuery(String query, final ProgressDialog dialog) {
        mSearchInterface.search(APIUtils.API_KEY, query).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchResult>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
                                   dialog.dismiss();
                                   // todo: error message
                               }

                               @Override
                               public void onNext(SearchResult searchResult) {
                                   dialog.dismiss();
                                   Log.d("main", searchResult.getResults().toString());
                                   mAdapter.updateAnswers(searchResult.getResults());

                                   // if we did not have a result from our query
                                   if (searchResult.getResults().size()==0) {
                                       if (!mCreatedDialog) {
                                           AlertDialog.Builder noResults = new AlertDialog.Builder(Search.this)
                                                   .setTitle("Error")
                                                   .setMessage("There are no Movies or TV programs matching your query.\n\nPlease try again with a different query.")
                                                   .setPositiveButton("Ok", null);

                                           noResults.show();

                                           mCreatedDialog = true;
                                       }
                                   } else {
                                       // put the empty recycler as gone
                                       if (mEmptyRecyclerView.getVisibility() == View.VISIBLE) {
                                           mEmptyRecyclerView.setVisibility(View.GONE);
                                       }
                                   }
                               }
                           }
                );


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
