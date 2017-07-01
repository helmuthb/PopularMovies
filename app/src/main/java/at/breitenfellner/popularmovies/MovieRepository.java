package at.breitenfellner.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.MovieList;
import at.breitenfellner.popularmovies.model.ReviewList;
import at.breitenfellner.popularmovies.model.TrailerList;
import at.breitenfellner.popularmovies.service.MovieService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * This class is the interface between any application logic and UI on the one side and
 * the data (through API, or the database) on the other side.
 */

public class MovieRepository {
    private final MovieService movieService;
    @NonNull
    private final HashMap<String, MutableLiveData<Movie>> movieCache;
    @NonNull
    private final HashMap<String, MutableLiveData<MovieList>> movieListCache;
    @NonNull
    private final HashMap<String, MutableLiveData<TrailerList>> trailerListCache;
    @NonNull
    private final HashMap<String, MutableLiveData<ReviewList>> reviewListCache;
    @Nullable
    static private MovieRepository theRepository = null;

    private MovieRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        movieService = retrofit.create(MovieService.class);
        movieCache = new HashMap<>();
        movieListCache = new HashMap<>();
        trailerListCache = new HashMap<>();
        reviewListCache = new HashMap<>();
    }

    @NonNull
    public static MovieRepository getInstance() {
        if (theRepository == null) {
            theRepository = new MovieRepository();
        }
        return theRepository;
    }

    private void loadMovie(String id, @NonNull final MutableLiveData<Movie> movieLiveData) {
        // load movie details
        Call<Movie> movieCall = movieService.getMovie(id, BuildConfig.THEMOVIEDB_KEY);
        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                // set the value of the LiveData
                movieLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                // create a movie with error status
                Movie movie = new Movie();
                movie.isError = true;
                // and set the value
                movieLiveData.setValue(movie);
            }
        });
    }

    public LiveData<Movie> getMovie(final String id) {
        // get from cache
        MutableLiveData<Movie> theMovie = movieCache.get(id);
        if (theMovie == null) {
            // not in cache - create an object & load it in background
            MutableLiveData<Movie> newMovie = new MutableLiveData<>();
            // load movie details
            loadMovie(id, newMovie);
            // put into cache for the future
            movieCache.put(id, newMovie);
            theMovie = newMovie;
        }
        else if (theMovie.getValue() != null && theMovie.getValue().isError) {
            // load did not work - let's retry in the background
            loadMovie(id, theMovie);
        }
        return theMovie;
    }

    private void loadMovieList(String sortOrder, @NonNull final MutableLiveData<MovieList> movieListLiveData) {
        // load movie list
        Call<MovieList> movieListCall = movieService
                .getMovies(sortOrder, BuildConfig.THEMOVIEDB_KEY);
        movieListCall.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {
                // set the value of the LiveData
                movieListLiveData.setValue(response.body());
                // and pre-cache all the movies retrieved
                //noinspection ConstantConditions
                for (Movie movie : movieListLiveData.getValue().movies) {
                    if (!movieCache.containsKey(movie.id)) {
                        MutableLiveData<Movie> movieLiveData = new MutableLiveData<>();
                        movieLiveData.setValue(movie);
                        movieCache.put(movie.id, movieLiveData);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable t) {
                // Create a movie list with error
                MovieList movieList = new MovieList();
                movieList.movies = null;
                movieList.isError = true;
                // and set it into the live data
                movieListLiveData.setValue(movieList);
            }
        });
    }

    public LiveData<MovieList> getMovies(String sortOrder) {
        // get from cache
        MutableLiveData<MovieList> theMovieList = movieListCache.get(sortOrder);
        if (theMovieList == null) {
            // not in cache - create an object & load it in background
            final MutableLiveData<MovieList> newMovieList = new MutableLiveData<>();
            // load in background
            loadMovieList(sortOrder, newMovieList);
            // put into the cache for the future
            movieListCache.put(sortOrder, newMovieList);
            theMovieList = newMovieList;
        }
        else if (theMovieList.getValue() != null && theMovieList.getValue().isError) {
            // load did not work - let's retry
            loadMovieList(sortOrder, theMovieList);
        }
        return theMovieList;
    }

    private void loadTrailerList(String movieId, @NonNull final MutableLiveData<TrailerList> trailerList) {
        // load trailer list
        Call<TrailerList> trailerListCall = movieService.getTrailers(movieId, BuildConfig.THEMOVIEDB_KEY);
        trailerListCall.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(@NonNull Call<TrailerList> call, @NonNull Response<TrailerList> response) {
                // set the value of the LiveData
                trailerList.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TrailerList> call, @NonNull Throwable t) {
                // Create a movie list with error
                TrailerList error = new TrailerList();
                error.trailers = null;
                error.isError = true;
                // and set it into the live data
                trailerList.setValue(error);
            }
        });
    }

    public LiveData<TrailerList> getTrailers(String movieId) {
        // get from cache
        MutableLiveData<TrailerList> theTrailerList = trailerListCache.get(movieId);
        if (theTrailerList == null) {
            // not in cache - create an object & load it in background
            final MutableLiveData<TrailerList> newTrailerList = new MutableLiveData<>();
            // load in background
            loadTrailerList(movieId, newTrailerList);
            // put into cache for the future
            trailerListCache.put(movieId, newTrailerList);
            theTrailerList = newTrailerList;
        }
        else if (theTrailerList.getValue() != null && theTrailerList.getValue().isError) {
            // load did not work - let's retry
            loadTrailerList(movieId, theTrailerList);
        }
        return theTrailerList;
    }

    private void loadReviewList(String movieId, @NonNull final MutableLiveData<ReviewList> reviewList) {
        // load trailer list
        Call<ReviewList> reviewListCall = movieService.getReviews(movieId, BuildConfig.THEMOVIEDB_KEY);
        reviewListCall.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(@NonNull Call<ReviewList> call, @NonNull Response<ReviewList> response) {
                // set the value of the LiveData
                reviewList.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ReviewList> call, @NonNull Throwable t) {
                // Create a movie list with error
                ReviewList error = new ReviewList();
                error.reviews = null;
                error.isError = true;
                // and set it into the live data
                reviewList.setValue(error);
            }
        });
    }

    public LiveData<ReviewList> getReviews(String movieId) {
        // get from cache
        MutableLiveData<ReviewList> theReviewList = reviewListCache.get(movieId);
        if (theReviewList == null) {
            // not in cache - create an object & load it in background
            final MutableLiveData<ReviewList> newReviewList = new MutableLiveData<>();
            // load in background
            loadReviewList(movieId, newReviewList);
            // put into cache for the future
            reviewListCache.put(movieId, newReviewList);
            theReviewList = newReviewList;
        }
        else if (theReviewList.getValue() != null && theReviewList.getValue().isError) {
            // load did not work - let's retry
            loadReviewList(movieId, theReviewList);
        }
        return theReviewList;
    }
}
