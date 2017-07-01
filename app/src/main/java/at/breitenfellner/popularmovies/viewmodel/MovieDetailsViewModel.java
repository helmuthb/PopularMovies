package at.breitenfellner.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import at.breitenfellner.popularmovies.MovieRepository;
import at.breitenfellner.popularmovies.R;
import at.breitenfellner.popularmovies.db.MovieContract;
import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.ReviewList;
import at.breitenfellner.popularmovies.model.Trailer;
import at.breitenfellner.popularmovies.model.TrailerList;

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
    @NonNull
    private final MediatorLiveData<TrailerList> trailers;
    @Nullable
    private LiveData<TrailerList> trailerSource;
    @NonNull
    private final MediatorLiveData<ReviewList> reviews;
    @Nullable
    private LiveData<ReviewList> reviewSource;
    @NonNull
    private final MutableLiveData<Boolean> favoriteFlag;
    @NonNull
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
        trailers = new MediatorLiveData<>();
        trailerSource = null;
        reviews = new MediatorLiveData<>();
        reviewSource = null;
        favoriteFlag = new MutableLiveData<>();
        favoriteFlag.setValue(Boolean.FALSE);
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
     * Get the TrailerList LiveData being provided by the repository.
     *
     * @return the TrailerList LiveData which will be updated once data is available.
     */
    @NonNull
    public LiveData<TrailerList> getTrailers() {
        return trailers;
    }

    /**
     * Get the ReviewList LiveData being provided by the repository.
     *
     * @return the ReviewList LiveData which will be updated once data is available.
     */
    @NonNull
    public LiveData<ReviewList> getReviews() {
        return reviews;
    }

    @NonNull
    public LiveData<Boolean> isFavorite() {
        return favoriteFlag;
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
    public void loadMovie(final String movieId) {
        // movie ...
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
        // ... and trailers ...
        LiveData<TrailerList> trailerListLiveData = repository.getTrailers(movieId);
        if (trailerSource != null) {
            trailers.removeSource(trailerSource);
        }
        trailerSource = trailerListLiveData;
        trailers.addSource(trailerListLiveData, new Observer<TrailerList>() {
            @Override
            public void onChanged(@Nullable TrailerList t) {
                trailers.setValue(t);
            }
        });
        // ... and reviews!
        LiveData<ReviewList> reviewListLiveData = repository.getReviews(movieId);
        if (reviewSource != null) {
            reviews.removeSource(reviewSource);
        }
        reviewSource = reviewListLiveData;
        reviews.addSource(reviewListLiveData, new Observer<ReviewList>() {
            @Override
            public void onChanged(@Nullable ReviewList r) {
                reviews.setValue(r);
            }
        });
        // and now check if it is a favorite
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getApplication().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry.COLUMN_ID},
                        MovieContract.MovieEntry.COLUMN_ID + " = ?",
                        new String[]{movieId},
                        null);
                favoriteFlag.setValue(cursor.getCount() > 0);
            }
        }).run();

    }

    /**
     * Show the Trailer using an Intent, either on the browser or the Youtube application
     *
     * @param trailer Trailer object to display
     */
    public void showTrailer(Trailer trailer) {
        if (trailer != null) {
            Uri uriApp = Uri.parse("vnd.youtube:" + trailer.key);
            Uri uriWeb = Uri.parse("http://www.youtube.com/watch?v=" + trailer.key);
            try {
                getApplication().startActivity(new Intent(Intent.ACTION_VIEW, uriApp));
            } catch (ActivityNotFoundException e) {
                getApplication().startActivity(new Intent(Intent.ACTION_VIEW, uriWeb));
            }
        }
    }

    public void toggleFavorite() {
        if (movie.getValue() != null) {
            final boolean isFav = !favoriteFlag.getValue();
            final String movieId = movie.getValue().id;
            final String movieTitle = movie.getValue().title;
            favoriteFlag.setValue(isFav);
            // go in other thread into DB
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isFav) {
                        // add into DB
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieEntry.COLUMN_ID, movieId);
                        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
                        getApplication().getContentResolver().insert(
                                MovieContract.MovieEntry.CONTENT_URI,
                                values
                        );
                    } else {
                        // delete from DB
                        getApplication().getContentResolver().delete(
                                MovieContract.MovieEntry.CONTENT_URI,
                                MovieContract.MovieEntry.COLUMN_ID + " = ?",
                                new String[]{movieId}
                        );
                    }
                    // and inform everyone we have changed something
                    getApplication().getContentResolver().notifyChange(
                            MovieContract.MovieEntry.CONTENT_URI,
                            null
                    );
                }
            }).run();
        }
    }
}
