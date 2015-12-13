package chipset.pone.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.Intent.EXTRA_TEXT;

/**
 * Developer: chipset
 * Package : chipset.pone.fragments
 * Project : Popular Movies
 * Date : 13/12/15
 */
public class MovieDetailFragment extends Fragment {

    private ImageView mBackdropImageView, mPosterImageView;
    private TextView mMovieOverviewTextView, mRatingTextView, mReleaseTextView;
    private CollapsingToolbarLayout mToolbarLayout;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton mFavouriteFab;
    private HListView mVideosListView, mReviewsListView;
    private MoviesVideoListAdapter mMoviesVideoListAdapter;
    private MoviesReviewListAdapter mMoviesReviewListAdapter;
    private String mID;
    private String mVideoURL, mMovieTitle;
    private View mView;
    private AppCompatActivity mActivity;

    public static MovieDetailFragment newInstance(String id) {
        return new MovieDetailFragment(id);
    }

    public MovieDetailFragment(String mID) {
        this.mID = mID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = ((AppCompatActivity) getActivity());
        if (!getResources().getBoolean(R.bool.is_tablet)) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mActivity.setSupportActionBar(toolbar);
            if (mActivity.getSupportActionBar() != null)
                mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();

        mFavouriteFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFavouriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
        mBackdropImageView = (ImageView) view.findViewById(R.id.toolbar_background_image_view);
        mMovieOverviewTextView = (TextView) view.findViewById(R.id.movie_overview_text_view);
        mRatingTextView = (TextView) view.findViewById(R.id.rating_text_view);
        mReleaseTextView = (TextView) view.findViewById(R.id.release_text_view);
        mPosterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
        mVideosListView = (HListView) view.findViewById(R.id.videos_list_view);
        mReviewsListView = (HListView) view.findViewById(R.id.reviews_list_view);
//        mID = String.valueOf(getIntent().getLongExtra(Constants.EXTRA_MOVIE_ID, 0));

        APIClient.getApi().getMovieFromId(mID, new Callback<Movie>() {
            @Override
            public void success(Movie movie, Response response) {
                mToolbarLayout.setTitle(movie.getOriginalTitle());
                mMovieTitle = movie.getOriginalTitle();

                Picasso.with(getContext())
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

                Picasso.with(getContext())
                        .load(Constants.URL_POSTER_IMAGE + movie.getPosterPath())
                        .into(mPosterImageView);

                APIClient.getApi().getMovieVideosFromId(mID, new Callback<MovieVideos>() {
                    @Override
                    public void success(final MovieVideos movieVideos, Response response) {
                        mMoviesVideoListAdapter = new MoviesVideoListAdapter(getContext(), movieVideos.getResults());
                        mVideosListView.setAdapter(mMoviesVideoListAdapter);
                        mMoviesVideoListAdapter.notifyDataSetChanged();
                        if (movieVideos.getResults().size() > 0)
                            mVideoURL = Constants.URL_YOUTUBE + movieVideos.getResults().get(0).getKey();
                        else mVideoURL = "none";
                        mVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String url = Constants.URL_YOUTUBE + movieVideos.getResults().get(position).getKey();
                                startActivity((new Intent(Intent.ACTION_VIEW)).setData(Uri.parse(url)));
                            }
                        });

                        APIClient.getApi().getMovieReviewsFromId(mID, new Callback<MovieReviews>() {
                            @Override
                            public void success(final MovieReviews movieReviews, Response response) {
                                mMoviesReviewListAdapter = new MoviesReviewListAdapter(getContext(), movieReviews.getResults());
                                mReviewsListView.setAdapter(mMoviesReviewListAdapter);
                                mMoviesReviewListAdapter.notifyDataSetChanged();
                                mReviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        MovieReviewsResults result = movieReviews.getResults().get(position);
                                        new AlertDialog.Builder(getContext())
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
                                Snackbar.make(mView, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                                error.printStackTrace();

                            }
                        });

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Snackbar.make(mView, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Snackbar.make(mView, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share_movie) {
            if (!mVideoURL.equalsIgnoreCase("none")) {
                String text = getString(R.string.checkout_share) + mMovieTitle + "\n\n" + mVideoURL;
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(EXTRA_TEXT, text)
                        , getString(R.string.share_using)));
            } else {
                Snackbar.make(mView, R.string.nothing_to_share, Snackbar.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
