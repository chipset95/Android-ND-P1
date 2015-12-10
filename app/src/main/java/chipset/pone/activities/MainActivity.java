package chipset.pone.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import chipset.pone.R;
import chipset.pone.adapters.MoviesGridAdapter;
import chipset.pone.models.Movies;
import chipset.pone.network.APIClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private GridView mMoviesGridView;
    private MoviesGridAdapter mMoviesGridAdapter;
    private ProgressBar mMoviesProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesGridView = (GridView) findViewById(R.id.movies_grid_view);
        mMoviesProgressBar = (ProgressBar) findViewById(R.id.movies_progress_bar);

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
                Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }
}

