package at.breitenfellner.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import at.breitenfellner.popularmovies.MovieRepository;
import at.breitenfellner.popularmovies.R;
import at.breitenfellner.popularmovies.model.Movie;

/**
 * This is the view model for the details view, providing the data for a specific movie.
 * Using the Android Architecture class ViewModel ensures that from background tasks
 * no updates will be attempted once an activity is no longer in view on the device.
 */

public class MovieDetailsViewModel extends AndroidViewModel {

    @NonNull
    private final MediatorLiveData<Movie> movie;
    @Nullable
    private LiveData<Movie> movieSource;
    @Nullable
    private final MovieRepository repository;

    /**
     * Create a MovieDetailsViewModel object
     *
     * @param application the application which can be used to provide an Android Context
     */
    public MovieDetailsViewModel(Application application) {
        super(application);
        movie = new MediatorLiveData<>();
        movieSource = null;
        repository = MovieRepository.getInstance();
    }

    /**
     * Get the Movie LiveData being provided by the repository.
     *
     * @return the Movie LiveData which will be updated once data is available.
     */
    @NonNull
    public LiveData<Movie> getMovie() {
        return movie;
    }

    /**
     * Get the poster Url for the movie - if known.
     *
     * @return the HTTP Url for the movie poster
     */
    @Nullable
    public String getPosterUrl() {
        if (movie.getValue() != null && !movie.getValue().isError) {
            String baseUrl = getApplication().getString(R.string.tmdb_image_baseurl_large);
            return baseUrl + movie.getValue().posterPath;
        } else {
            return null;
        }
    }

    /**
     * Load a Movie into this MovieDetailsViewModel
     *
     * @param movieId String identifier of the movie
     */
    public void loadMovie(String movieId) {
        LiveData<Movie> movieLiveData = repository.getMovie(movieId);
        if (movieSource != null) {
            movie.removeSource(movieSource);
        }
        movieSource = movieLiveData;
        movie.addSource(movieLiveData, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie m) {
                movie.setValue(m);
            }
        });
    }

}
