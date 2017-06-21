package at.breitenfellner.popularmovies.service;

import at.breitenfellner.popularmovies.model.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by helmuth on 16/06/2017.
 *
 * This class defines two functions, one for getting movies sorted by rating,
 * one for getting them sorted by popularity.
 *
 * Another approach would be to use one function with a parameter
 */

public interface MovieService {
    // path constants to be used in the functoin getMovies
    String SORT_RATING = "top_rated";
    String SORT_POPULARITY = "popular";

    @GET("3/movie/{sort_by}")
    Call<MovieList> getMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);
}
