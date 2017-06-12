package com.sardinecorp.movienght.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;


import com.sardinecorp.movienght.MainActivity;
import com.sardinecorp.movienght.R;
import com.sardinecorp.movienght.utils.APIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Discover extends AppCompatActivity {



    @BindView(R.id.discover_genre_edittext)
    EditText mGenreEditText;
    @BindView(R.id.discover_maximum_date)
    EditText mMaxDateEditText;
    @BindView(R.id.discover_minimum_date)
    EditText mMinDateEditText;
    @BindView(R.id.discover_minimum_ratings)
    EditText mMinRatingsEditText;
    @BindView(R.id.discover_maximum_ratings)
    EditText mMaxRatingsEditText;
    @BindView(R.id.discover_type_spinner)
    Spinner mTypeSpinner;
    @BindView(R.id.discover_sort_spinner)
    Spinner mSortSpinner;
    @BindView(R.id.discover_scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.discover_linear_layout_ratings)
    LinearLayout mRatingsLinearLayout;
    @BindView(R.id.discover_number_of_ratings_edittext)
    EditText mNumberOfRatingsEditText;
    @BindView(R.id.discover_search_button)
    Button mDiscoverButton;

    // Spinner input
    String[] mStringTypes = new String[] {"Movie", "TV", "Both"};
    /* Sort Types
     *  popularity.asc, popularity.desc, release_date.asc, release_date.desc
     *           revenue.asc, revenue.desc, primary_release_date.asc, primary_release_date.desc,
     *           vote_average.desc, vote_average.asc, vote_count.desc, vote_count.asc
     */
    String[] mStringSort = new String[]  {"+ Popularity", "- Popularity",
                                          "+ Release Date", "- Release Date",
                                          "+ Revenue", "- Revenue",
                                          "+ Release Date", "- Release Date",
                                          "+ Vote Average", "- Vote Average",
                                          "+ Vote Count", "- Vote Count"

    };


    private List<String> mChosenGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // enable back button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChosenGenres = new ArrayList<>();


        mGenreEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a dialog with all the genres on it
                List<String> listOfGenres = new ArrayList<>(MainActivity.mListOfGenres.keySet());
                final String[] arrayOfGenres = new String[listOfGenres.size()];
                listOfGenres.toArray(arrayOfGenres);
                final boolean[] listChecks = new boolean[listOfGenres.size()];
                for (int i = 0; i < listChecks.length; i++) {
                    listChecks[i] = false;
                }

                AlertDialog.Builder genreAlert = new AlertDialog.Builder(Discover.this)
                        .setMultiChoiceItems(arrayOfGenres, listChecks, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                listChecks[i] = b;
                            }
                        })
                        .setTitle("Discover - Genres")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // clear the current list of genres
                                mChosenGenres.clear();
                                for (int k = 0; k < listChecks.length; k++) {
                                    if (listChecks[k]) {
                                        mChosenGenres.add(arrayOfGenres[k]);
                                    }
                                }
                                updateGenreEditText();
                            }
                        });
                genreAlert.show();

            }
        });


        mMinRatingsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingsPickerDialog(0, 10, 5, "%02d", "Choose Ratings Range", mMinRatingsEditText, mMaxRatingsEditText);
            }
        });

        mMaxRatingsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingsPickerDialog(0, 10, 10, "%02d", "Choose Ratings Range", mMinRatingsEditText, mMaxRatingsEditText);
            }
        });

        mMinRatingsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingsPickerDialog(0, 10, 5, "%02d", "Choose Ratings Range", mMinRatingsEditText, mMaxRatingsEditText);
            }
        });

        mMaxDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                ratingsPickerDialog(1900, currentYear+5, currentYear, "%4d", "Choose Year Range", mMinDateEditText, mMaxDateEditText);
            }
        });

        mMinDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                ratingsPickerDialog(1900, currentYear+5, currentYear, "%4d", "Choose Year Range", mMinDateEditText, mMaxDateEditText);
            }
        });

        // Spinner arrays
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStringTypes);
        mTypeSpinner.setAdapter(typeAdapter);
        mTypeSpinner.setSelection(0);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStringSort);
        mSortSpinner.setAdapter(sortAdapter);
        mSortSpinner.setSelection(0);

        mDiscoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchDiscoveryContent();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        mScrollView.requestFocus();
        super.onResume();
    }

    @Override
    protected void onStart() {
        // disable the focus from the edit text
        mScrollView.requestFocus();
        super.onStart();
    }

    private void updateGenreEditText() {
        mGenreEditText.setText(TextUtils.join(", ", mChosenGenres));
    }

    private void ratingsPickerDialog(int minValue, int maxValue, int currentValue, final String format, String title, final EditText minEdit, final EditText maxEdit) {
        View view = getLayoutInflater().inflate(R.layout.dialog_min_max_picker, null);
        final NumberPicker min = (NumberPicker) view.findViewById(R.id.dialog_number_picker_min);
        min.setMaxValue(maxValue);
        min.setMinValue(minValue);
        min.setValue(currentValue);
        min.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(format,i);
            }
        });
        min.setWrapSelectorWheel(false);

        final NumberPicker max = (NumberPicker) view.findViewById(R.id.dialog_number_picker_max);
        max.setMaxValue(maxValue);
        max.setMinValue(minValue);
        max.setValue(currentValue);
        max.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(format,i);
            }
        });
        max.setWrapSelectorWheel(false);



        AlertDialog.Builder alert = new AlertDialog.Builder(Discover.this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        minEdit.setText(min.getValue()+"");
                        maxEdit.setText(max.getValue()+"");
                    }
                })
                .setNegativeButton("Cancel", null);
        alert.show();
    }

    private void fetchDiscoveryContent() {
        Intent intent = new Intent(this, DiscoverResults.class);
        // fetch the data from each field
        int type = mTypeSpinner.getSelectedItemPosition();
        String genre = mGenreEditText.getText() == null ? null : getGenres(mGenreEditText.getText().toString());
        String minRatings  = getStringFromEditText(mMinRatingsEditText);
        String maxRatings = getStringFromEditText(mMaxRatingsEditText);
        String numberOfRatings = getStringFromEditText(mNumberOfRatingsEditText);
        String minReleaseDate = getStringFromEditText(mMinDateEditText);
        String maxReleaseDate = getStringFromEditText(mMaxDateEditText);

        int sort = mSortSpinner.getSelectedItemPosition();

        intent.putExtra("type", type);
        intent.putExtra("genre", genre);
        intent.putExtra("minRatings", minRatings);
        intent.putExtra("maxRatings", maxRatings);
        intent.putExtra("numberofRatings", numberOfRatings);
        intent.putExtra("minReleaseDate", minReleaseDate);
        intent.putExtra("maxReleaseDate", maxReleaseDate);
        intent.putExtra("sort", sort);
        startActivity(intent);

    }

    private String getGenres (String genres) {
        String result = "";
        List<String> genresList = Arrays.asList(genres.split("\\s*,\\s*"));

        for (String genre : genresList) {
            result += ""+MainActivity.mListOfGenres.get(genre)+",";
        }
        Log.d("main", "Before substring: "+result);
        result = result.substring(0, result.length()-1);
        Log.d("main", "After substring: "+result);

        Log.d("main", "Comma separated list of genres: " + genresList);
        Log.d("main", "List of genre ids: " + result);
        return result;
    }

    private String getStringFromEditText(EditText edit) {
        return edit.getText() == null ? null : edit.getText().toString();
    }


}
