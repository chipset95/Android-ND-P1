package chipset.pone.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import chipset.pone.R;
import chipset.pone.adapters.MoviesReviewListAdapter;
import chipset.pone.adapters.MoviesVideoListAdapter;
import chipset.pone.models.Movie;
import chipset.pone.models.MovieReviews;
import chipset.pone.models.MovieReviewsResults;
import chipset.pone.models.MovieVideos;
import chipset.pone.network.APIClient;
import chipset.pone.resources.Constants;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mBackdropImageView, mPosterImageView;
    private TextView mMovieOverviewTextView, mRatingTextView, mReleaseTextView;
    private CollapsingToolbarLayout mToolbarLayout;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton mFavouriteFab;
    private HListView mVideosListView, mReviewsListView;
    private MoviesVideoListAdapter mMoviesVideoListAdapter;
    private MoviesReviewListAdapter mMoviesReviewListAdapter;
    private String mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(MovieDetailActivity.this);
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();

        mFavouriteFab = (FloatingActionButton) findViewById(R.id.fab);
        mFavouriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mBackdropImageView = (ImageView) findViewById(R.id.toolbar_background_image_view);
        mMovieOverviewTextView = (TextView) findViewById(R.id.movie_overview_text_view);
        mRatingTextView = (TextView) findViewById(R.id.rating_text_view);
        mReleaseTextView = (TextView) findViewById(R.id.release_text_view);
        mPosterImageView = (ImageView) findViewById(R.id.poster_image_view);
        mVideosListView = (HListView) findViewById(R.id.videos_list_view);
        mReviewsListView = (HListView) findViewById(R.id.reviews_list_view);
        mID = String.valueOf(getIntent().getLongExtra(Constants.EXTRA_MOVIE_ID, 0));

        APIClient.getApi().getMovieFromId(mID, new Callback<Movie>() {
            @Override
            public void success(Movie movie, Response response) {
                mToolbarLayout.setTitle(movie.getOriginalTitle());

                Picasso.with(getApplicationContext())
                        .load(Constants.URL_BACKDROP_IMAGE + movie.getBackdropPath())
                        .into(mBackdropImageView);

                mMovieOverviewTextView.setText(movie.getOverview());

                String rating = getString(R.string.rating_header)
                        + String.valueOf(movie.getVoteAverage()) + getString(R.string.rating_footer);
                mRatingTextView.setText(rating);

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
                try {
                    cal.setTime(sdfFrom.parse(movie.getReleaseDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                String release = getString(R.string.release_header) + sdfTo.format(cal.getTime());
                mReleaseTextView.setText(release);

                Picasso.with(getApplicationContext())
                        .load(Constants.URL_POSTER_IMAGE + movie.getPosterPath())
                        .into(mPosterImageView);

                APIClient.getApi().getMovieVideosFromId(mID, new Callback<MovieVideos>() {
                    @Override
                    public void success(final MovieVideos movieVideos, Response response) {
                        mMoviesVideoListAdapter = new MoviesVideoListAdapter(MovieDetailActivity.this, movieVideos.getResults());
                        mVideosListView.setAdapter(mMoviesVideoListAdapter);
                        mMoviesVideoListAdapter.notifyDataSetChanged();
                        mVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String url = Constants.URL_YOUTUBE + movieVideos.getResults().get(position).getKey();
                                startActivity((new Intent("android.intent.action.VIEW")).setData(Uri.parse(url)));
                            }
                        });

                        APIClient.getApi().getMovieReviewsFromId(mID, new Callback<MovieReviews>() {
                            @Override
                            public void success(final MovieReviews movieReviews, Response response) {
                                mMoviesReviewListAdapter = new MoviesReviewListAdapter(MovieDetailActivity.this, movieReviews.getResults());
                                mReviewsListView.setAdapter(mMoviesReviewListAdapter);
                                mMoviesReviewListAdapter.notifyDataSetChanged();
                                mReviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        MovieReviewsResults result = movieReviews.getResults().get(position);
                                        new AlertDialog.Builder(MovieDetailActivity.this)
                                                .setTitle(result.getAuthor())
                                                .setMessage(result.getContent())
                                                .setPositiveButton(R.string.close, null)
                                                .create().show();
                                    }
                                });
                                if (mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                if (mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();
                                Snackbar.make(mToolbarLayout, "Connection Error", Snackbar.LENGTH_SHORT).show();
                                error.printStackTrace();

                            }
                        });

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Snackbar.make(mToolbarLayout, "Connection Error", Snackbar.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Snackbar.make(mToolbarLayout, "Connection Error", Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }
}
