package at.breitenfellner.popularmovies.service;

import at.breitenfellner.popularmovies.model.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by helmuth on 16/06/2017.
 */

public interface MovieService {
    @GET("3/movie/top_rated")
    Call<MovieList> topRated(@Query("api_key") String api_key);

    @GET("3/movie/popular")
    Call<MovieList> popular(@Query("api_key") String api_key);
}
