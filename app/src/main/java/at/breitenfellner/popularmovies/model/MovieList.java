package at.breitenfellner.popularmovies.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * A MovieList is an object which represents a list of movies from
 * TheMovieDB.
 */

public class MovieList {
    @Nullable
    @Json(name="results")
    public List<Movie> movies;

    @NonNull
    public Boolean isError = false;
}
