package at.breitenfellner.popularmovies.service;

import android.support.annotation.NonNull;

import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.ReviewList;
import at.breitenfellner.popularmovies.model.TrailerList;
import at.breitenfellner.popularmovies.model.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by helmuth on 16/06/2017.
 *
 * This class defines three functions, one for getting movies sorted by rating or popularity,
 * one for getting their trailer videos, and one for getting their reviews.
 *
 */

public interface MovieService {
    // path constants to be used in the function getMovies
    String SORT_RATING = "top_rated";
    String SORT_POPULARITY = "popular";

    /**
     * Get movies sorted either by popularity or by rating
     * @param sortBy one of either SORT_RATING or SORT_POPULARITY
     * @param apiKey API key to access TheMovieDB
     * @return a MovieList structure with the first 20 movies by sort order
     */
    @NonNull
    @GET("3/movie/{sort_by}")
    Call<MovieList> getMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    /**
     * Get a single movie by its ID value from TheMovieDB
     * @param id Identifier for the movie
     * @param apiKey API key to access TheMovieDB
     * @return a Movie structure for the specified movie
     */
    @NonNull
    @GET("3/movie/{id}")
    Call<Movie> getMovie(@Path("id") String id, @Query("api_key") String apiKey);

    /**
     * Get the trailers and snippets available for a movie
     * @param movieId id of the movie for which the trailers are looked for
     * @param apiKey API key to access TheMovieDB
     * @return a TrailerList structure with the trailers and snippets available
     */
    @NonNull
    @GET("3/movie/{movie}/videos")
    Call<TrailerList> getTrailers(@Path("movie") String movieId, @Query("api_key") String apiKey);

    /**
     * Get the review for a movie
     * @param movieId id of the movie for which the reviews are looked for
     * @param apiKey API key to access TheMovieDB
     * @return a
     */
    @NonNull
    @GET("3/movie/{movie}/reviews")
    Call<ReviewList> getReviews(@Path("movie") String movieId, @Query("api_key") String apiKey);
}
