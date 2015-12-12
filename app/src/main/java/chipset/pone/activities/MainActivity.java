package chipset.pone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import chipset.pone.R;
import chipset.pone.adapters.MoviesGridAdapter;
import chipset.pone.models.Movies;
import chipset.pone.network.APIClient;
import chipset.pone.resources.Constants;
import chipset.potato.Potato;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private GridView mMoviesGridView;
    private MoviesGridAdapter mMoviesGridAdapter;
    private ProgressBar mMoviesProgressBar;
    private int sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesGridView = (GridView) findViewById(R.id.movies_grid_view);
        mMoviesProgressBar = (ProgressBar) findViewById(R.id.movies_progress_bar);
        mMoviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, MovieDetailActivity.class).putExtra(Constants.EXTRA_MOVIE_ID, id));
            }
        });
        initialize();
    }

    @Override
    protected void onResume() {
        sort = Potato.potate().Preferences().getSharedPreferenceInteger(getApplicationContext(), Constants.PREF_SORT_ORDER);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Potato.potate().Preferences().putSharedPreference(getApplicationContext(), Constants.PREF_SORT_ORDER, sort);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_popularity && sort != 0) {
            fetchByPopularity();
            sort = 0;
        } else if (item.getItemId() == R.id.action_sort_rating && sort != 1) {
            fetchByRating();
            sort = 1;
        }
        Potato.potate().Preferences().putSharedPreference(getApplicationContext(), Constants.PREF_SORT_ORDER, sort);
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        sort = Potato.potate().Preferences().getSharedPreferenceInteger(getApplicationContext(), Constants.PREF_SORT_ORDER);
        if (sort == 0) {
            fetchByPopularity();
            sort = 0;
        } else {
            fetchByRating();
            sort = 1;
        }
        Potato.potate().Preferences().putSharedPreference(getApplicationContext(), Constants.PREF_SORT_ORDER, sort);
    }

    private void fetchByPopularity() {
        mMoviesGridView.setVisibility(View.GONE);
        mMoviesProgressBar.setVisibility(View.VISIBLE);
        APIClient.getApi().getPopularMovies(new Callback<Movies>() {
            @Override
            public void success(Movies movies, Response response) {
                mMoviesGridAdapter = new MoviesGridAdapter(getApplicationContext(), movies.getResults());
                mMoviesGridAdapter.notifyDataSetChanged();
                mMoviesGridView.setAdapter(mMoviesGridAdapter);
                mMoviesGridView.setVisibility(View.VISIBLE);
                mMoviesProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(findViewById(android.R.id.content), R.string.connection_error, Snackbar.LENGTH_SHORT).show();
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
                mMoviesGridAdapter = new MoviesGridAdapter(getApplicationContext(), movies.getResults());
                mMoviesGridAdapter.notifyDataSetChanged();
                mMoviesGridView.setAdapter(mMoviesGridAdapter);
                mMoviesGridView.setVisibility(View.VISIBLE);
                mMoviesProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(findViewById(android.R.id.content), R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
                mMoviesProgressBar.setVisibility(View.GONE);
            }
        });
    }
}

