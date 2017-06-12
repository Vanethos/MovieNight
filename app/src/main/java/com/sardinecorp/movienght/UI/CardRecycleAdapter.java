package com.sardinecorp.movienght.UI;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sardinecorp.movienght.MainActivity;
import com.sardinecorp.movienght.R;
import com.sardinecorp.movienght.retrofit.Movie;
import com.sardinecorp.movienght.retrofit.discovery.MoviesAndTV;
import com.sardinecorp.movienght.utils.Common;
import com.sardinecorp.movienght.utils.OverviewFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gonçalo on 05/06/2017.
 */

public class CardRecycleAdapter extends RecyclerView.Adapter<CardRecycleAdapter.CardViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;
    private FragmentManager mFragmentManager;

    private static String IMAGE_WIDTH = "640";
    private static String IMAGE_HEAD = "https://image.tmdb.org/t/p/w"+IMAGE_WIDTH+"/";

    public CardRecycleAdapter(List<Movie> movies, Context context, FragmentManager fragmentManager) {
        mMovies = movies;
        mContext = context;
        mFragmentManager = fragmentManager;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card_view, parent, false);
        CardViewHolder viewHolder = new CardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.bindMovie(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void updateAnswers(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    // to get the data form both search results, we will have to
    // trim the results and display up until 5 of each
    public void updateAnswers(MoviesAndTV combinedResults) {
        List<Movie> firstResult =combinedResults.mMovieResult.getResults().
                subList(0, Math.min(4, combinedResults.mMovieResult.getResults().size()));
        firstResult = Common.setListTypeOfMedia(firstResult, "movie");
        List<Movie> secondResult = combinedResults.mTvResult.getResults().
                subList(0, Math.min(4, combinedResults.mTvResult.getResults().size()));;
        secondResult = Common.setListTypeOfMedia(secondResult, "tv");
        firstResult.addAll(secondResult);
        mMovies = firstResult;
        notifyDataSetChanged();
    }

    public int adapterSize() {
        return mMovies.size();
    }

    // View Holder
    public class CardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_card) android.support.v7.widget.CardView mMovieCard;
        @BindView(R.id.movie_card_movie_name) TextView mMovieName;
        @BindView(R.id.movie_card_rating) TextView mMovieRating;
        @BindView(R.id.movie_card_poster) ImageView mMoviePoster;
        @BindView(R.id.overview) TextView mOverview;
        @BindView(R.id.runtime) TextView mRuntime;
        @BindView(R.id.movie_card_hidden) RelativeLayout mHiddenLayout;
        @BindView(R.id.genres) TextView mGenres;
        @BindView(R.id.type) TextView mType;

        @OnClick(R.id.movie_card_poster)
        void openDialog(View v) {

        }

        @OnClick(R.id.movie_card_opener)
        void openLayout(View v){

            int visibility = mHiddenLayout.getVisibility()==View.GONE ? View.VISIBLE : View.GONE;
            mHiddenLayout.setVisibility(
                    visibility
            );

            // get the movie poster height
            int posterHeight = mMoviePoster.getHeight();
            // adjust the size of the poster image
            if (visibility == View.GONE) {
                mMoviePoster.getLayoutParams().height = posterHeight/2;
            } else {
                mMoviePoster.getLayoutParams().height = posterHeight*2;
            }
        }

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindMovie(Movie movie) {
            // TODO: colocar aqui um check para verificar se é um resultado de search ou discovery, se for de discovery entao
            // temos de por qual é o resultado que estamos a ter
            mType.setText(movie.getMediaType().equals("movie")?"Movie":"TV");
            final String title = movie.getMediaType().equals("movie")?movie.getTitle():movie.getName();
            mMovieName.setText(title);
            mMovieRating.setText(movie.getVoteAverage()+"");
            final String overview = movie.getOverview();
            mOverview.setText(overview.trim().length() == 0 ? ("No information for " + title + ".") : overview);
            String runtime = movie.getReleaseDate();
            mRuntime.setText(runtime == null ? "N/A" : runtime);

            String posterURL = movie.getPosterPath();
            if (posterURL != null) {
                // download the movie poster image using Picasso
                String imageUrl = IMAGE_HEAD + posterURL;
                int imageSize = Common.dpToPx(100, mContext);


                Picasso.with(mContext)
                        .load(imageUrl)
                        .fit().centerCrop()
                        //.resize(imageSize, imageSize)
                        .into(mMoviePoster);

                mMoviePoster.getLayoutParams().height = imageSize;
            } else {
                mMoviePoster.setImageResource(R.mipmap.launcher);
                mMoviePoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            // set the genres
            if (movie.getGenreIds() != null) {
                String genres = "";
                int index = 0;
                List<String> genresArray = new ArrayList<>(MainActivity.mListOfGenres.keySet());
                List<Integer> ids = new ArrayList<>(MainActivity.mListOfGenres.values());
                for (int id : movie.getGenreIds()) {
                    int genreIndex = ids.indexOf(id);
                    if (genreIndex>=0) {
                        genres+=genresArray.get(ids.indexOf(id));
                        Log.d("genres", "Genre: "+genresArray.get(ids.indexOf(id)));
                        Log.d("genres", "Genre ID : "+ids.indexOf(id));
                    } else {
                        genres+="Unknown";
                    }


                    if (index < movie.getGenreIds().size()-1) {
                        genres+=", ";
                    }
                    index++;
                }
                mGenres.setText(genres);
            } else {
                mGenres.setText("N/A");
            }

            mMoviePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment frag = OverviewFragment.newInstance(title, overview.trim().length()==0?"Not Available":overview);
                    frag.show(mFragmentManager, "overview");
                }
            });
        }




    }
}
