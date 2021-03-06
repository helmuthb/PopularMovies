package at.breitenfellner.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import at.breitenfellner.popularmovies.MovieRepository;
import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.MovieList;
import at.breitenfellner.popularmovies.view.MovieDetailsActivity;

/**
 * This is the view model for a list of movies, where one can choose between
 * sorting by rating and sorting by popularity.
 */

public class MovieListViewModel extends AndroidViewModel {
    @NonNull
    private final MediatorLiveData<MovieList> movieList;
    @Nullable
    private LiveData<MovieList> movieListSource;
    private List<String> favoriteIds;
    boolean showFavorites;
    @Nullable
    private final MovieRepository repository;

    /**
     * Create a MovieListViewModel object
     *
     * @param application the Android application which can be used to provide an Android Context
     */
    public MovieListViewModel(Application application) {
        super(application);
        movieList = new MediatorLiveData<>();
        movieListSource = null;
        showFavorites = false;
        repository = MovieRepository.getInstance();
    }

    /**
     * Provide the MovieList LiveData which will be updated when needed
     *
     * @return a MovieList LiveData containing the requested list of movies
     */
    @NonNull
    public LiveData<MovieList> getMovieList() {
        return movieList;
    }

    /**
     * Load the requested list of movies from cache or database
     *
     * @param sortOrder requested sortorder
     */
    public void loadMovies(String sortOrder, boolean showFavorites) {
        this.showFavorites = showFavorites;
        LiveData<MovieList> movies;
        if (!showFavorites) {
            movies = repository.getMovies(sortOrder);
        }
        else {
            movies = repository.getMovies(favoriteIds);
        }
        if (movieListSource != null) {
            movieList.removeSource(movieListSource);
        }
        movieListSource = movies;
        movieList.addSource(movies, new Observer<MovieList>() {
            @Override
            public void onChanged(@Nullable MovieList ml) {
                movieList.setValue(ml);
            }
        });
    }

    /**
     * Get a movie by index from the current movie list
     *
     * @param index position (0-based) of the requested item
     * @return the Movie object on the provided position, or {@code null} if not found
     */
    @Nullable
    public Movie getMovieByIndex(int index) {
        if (movieList.getValue() != null && !movieList.getValue().isError) {
            return movieList.getValue().movies.get(index);
        }
        return null;
    }

    /**
     * Show the Movie details activity
     * @param m Movie object to display
     */
    public void openMovieView(Movie m) {
        if (m != null) {
            Intent detailsIntent = new Intent(getApplication(), MovieDetailsActivity.class);
            // set details needed
            detailsIntent.putExtra(Movie.class.getName(), m.id);
            getApplication().startActivity(detailsIntent);
        }
    }

    /**
     * Inform the ViewModel about the list of currently selected favorites.
     * @param favoriteIds list of current favorites
     */
    public void notifyFavorites(List<String> favoriteIds) {
        this.favoriteIds = favoriteIds;
        if (showFavorites) {
            loadMovies(null, true);
        }
    }
}
