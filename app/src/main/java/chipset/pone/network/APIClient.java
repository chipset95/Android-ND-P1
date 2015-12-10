package chipset.pone.network;

import chipset.pone.models.Movies;
import chipset.pone.resources.Constants;
import chipset.pone.resources.Keys;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;

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
                    .setEndpoint(Constants.BASE_URL)
                    .build();
            movieDBInterface = adapter.create(MovieDBInterface.class);
        }
        return movieDBInterface;
    }

    public interface MovieDBInterface {
        @GET("/movie/popular?api_key=" + Keys.TMDB_APIKEY)
        void getPopularMovies(Callback<Movies> moviesCallback);
    }
}
