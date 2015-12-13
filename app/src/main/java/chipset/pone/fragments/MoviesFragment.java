package chipset.pone.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import chipset.pone.R;
import chipset.pone.activities.MovieDetailActivity;
import chipset.pone.adapters.MoviesGridAdapter;
import chipset.pone.contracts.MoviesContract;
import chipset.pone.models.Movies;
import chipset.pone.models.MoviesResults;
import chipset.pone.network.APIClient;
import chipset.pone.resources.Constants;
import chipset.potato.Potato;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Developer: chipset
 * Package : chipset.pone.fragments
 * Project : Popular Movies
 * Date : 13/12/15
 */

public class MoviesFragment extends Fragment {

    private GridView mMoviesGridView;
    private MoviesGridAdapter mMoviesGridAdapter;
    private ProgressBar mMoviesProgressBar;
    private int sort;
    private View mView;
    private boolean isTablet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_movies, container, false);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMoviesGridView = (GridView) view.findViewById(R.id.movies_grid_view);
        mMoviesProgressBar = (ProgressBar) view.findViewById(R.id.movies_progress_bar);
        mMoviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isTablet)
                    startActivity(new Intent(getContext(), MovieDetailActivity.class).putExtra(Constants.EXTRA_MOVIE_ID, id));
                else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_frame, MovieDetailFragment.newInstance(String.valueOf(id)))
                            .commit();
                }
            }
        });
        initialize();
    }

    @Override
    public void onResume() {
        sort = Potato.potate().Preferences().getSharedPreferenceInteger(getContext(), Constants.PREF_SORT_ORDER);
        super.onResume();
    }

    @Override
    public void onPause() {
        Potato.potate().Preferences().putSharedPreference(getContext(), Constants.PREF_SORT_ORDER, sort);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_popularity && sort != 0) {
            fetchByPopularity();
            sort = 0;
        } else if (item.getItemId() == R.id.action_sort_rating && sort != 1) {
            fetchByRating();
            sort = 1;
        } else if (item.getItemId() == R.id.action_favourites) {
            fetchFromFavourites();
            Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
        }
        Potato.potate().Preferences().putSharedPreference(getContext(), Constants.PREF_SORT_ORDER, sort);
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        sort = Potato.potate().Preferences().getSharedPreferenceInteger(getContext(), Constants.PREF_SORT_ORDER);
        if (sort == 0) {
            fetchByPopularity();
            sort = 0;
        } else {
            fetchByRating();
            sort = 1;
        }
        Potato.potate().Preferences().putSharedPreference(getContext(), Constants.PREF_SORT_ORDER, sort);
    }

    private void fetchByPopularity() {
        mMoviesGridView.setVisibility(View.GONE);
        mMoviesProgressBar.setVisibility(View.VISIBLE);
        APIClient.getApi().getPopularMovies(new Callback<Movies>() {
            @Override
            public void success(Movies movies, Response response) {
                mMoviesGridAdapter = new MoviesGridAdapter(getContext(), movies.getResults());
                mMoviesGridAdapter.notifyDataSetChanged();
                mMoviesGridView.setAdapter(mMoviesGridAdapter);
                mMoviesGridView.setVisibility(View.VISIBLE);
                mMoviesProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(mView, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
                mMoviesProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchByRating() {
        mMoviesGridView.setVisibility(View.GONE);
        mMoviesProgressBar.setVisibility(View.VISIBLE);
        APIClient.getApi().getTopRatedMovies(new Callback<Movies>() {
            @Override
            public void success(Movies movies, Response response) {
                mMoviesGridAdapter = new MoviesGridAdapter(getContext(), movies.getResults());
                mMoviesGridAdapter.notifyDataSetChanged();
                mMoviesGridView.setAdapter(mMoviesGridAdapter);
                mMoviesGridView.setVisibility(View.VISIBLE);
                mMoviesProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(mView, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
                mMoviesProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchFromFavourites() {
        Cursor cursor = getActivity().getContentResolver().query(MoviesContract.BASE_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            mMoviesGridView.setVisibility(View.GONE);
            mMoviesProgressBar.setVisibility(View.VISIBLE);
            List<MoviesResults> results = new ArrayList<>();
            do {
                MoviesResults moviesResults = new MoviesResults();
                moviesResults.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE)));
                moviesResults.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
                moviesResults.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)));
                moviesResults.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATING)));
                results.add(moviesResults);
            } while (cursor.moveToNext());
            mMoviesGridAdapter = new MoviesGridAdapter(getContext(), results);
            mMoviesGridAdapter.notifyDataSetChanged();
            mMoviesGridView.setAdapter(mMoviesGridAdapter);
            mMoviesGridView.setVisibility(View.VISIBLE);
            mMoviesProgressBar.setVisibility(View.GONE);
            cursor.close();
        } else {
            Snackbar.make(mView, R.string.favourites_empty, Snackbar.LENGTH_SHORT).show();
        }
    }
}
