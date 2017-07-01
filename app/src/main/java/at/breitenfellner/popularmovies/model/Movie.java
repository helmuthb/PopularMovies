package at.breitenfellner.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

/**
 * This is the representation of a movie.
 * It is annotated using {@code Json} annotation, which allows it to be
 * fetched from the TMDB API.
 */

public class Movie {
    public String id;

    @NonNull
    public Boolean isError = false;

    public String title;

    public String overview;

    @Json(name = "vote_average")
    public double voteAverage;

    public double popularity;

    @Json(name = "release_date")
    public String releaseDate;

    @Json(name = "poster_path")
    public String posterPath;

    /**
     * Create a movie from its values
     *
     * @param id          identified of the movie (as on TheMovieDB)
     * @param title       title of the movie
     * @param overview    summary overview of the movie
     * @param voteAverage average vote for the movie
     * @param popularity  average popularity of the movie
     * @param releaseDate date of release
     * @param posterPath  path to the poster image
     */
    public Movie(String id, String title, String overview, double voteAverage,
                 double popularity, String releaseDate, String posterPath) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.isError = false;
    }

    /**
     * Constructor for an empty movie
     */
    @Ignore
    public Movie() {
        isError = false;
    }
}
