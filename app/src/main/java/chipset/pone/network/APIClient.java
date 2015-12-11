package chipset.pone.network;

import chipset.pone.models.Movie;
import chipset.pone.models.Movies;
import chipset.pone.resources.Constants;
import chipset.pone.resources.Keys;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Developer: chipset
 * Package : chipset.pone.network
 * Project : Popular Movies
 * Date : 10/12/15
 */
public class APIClient {
    public static MovieDBInterface movieDBInterface = null;

    public static MovieDBInterface getApi() {


        if (movieDBInterface == null) {
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.URL_BASE)
                    .build();
            movieDBInterface = adapter.create(MovieDBInterface.class);
        }
        return movieDBInterface;
    }

    public interface MovieDBInterface {
        @GET("/discover/movie?sort_by=popularity.desc&api_key=" + Keys.TMDB_APIKEY)
        void getPopularMovies(Callback<Movies> moviesCallback);

        @GET("/discover/movie?sort_by=vote_average.desc&api_key=" + Keys.TMDB_APIKEY)
        void getTopRatedMovies(Callback<Movies> moviesCallback);

        @GET("/movie/{id}?api_key=" + Keys.TMDB_APIKEY)
        void getMovieFromId(@Path("id") String id, Callback<Movie> movieCallback);
    }
}
