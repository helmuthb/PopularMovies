package at.breitenfellner.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

/**
 * This is the representation of a movie.
 * It is annotated using {@code Room} annotation which allows it to be stored and fetched
 * from database.
 * In addition, it is annotated using {@code Json} annotation, which allows it to be
 * fetched from the TMDB API.
 */

@Entity(tableName = Movie.TABLE_NAME)
public class Movie {
    public static final String TABLE_NAME = "movie";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_POSTER_PATH = "poster_path";

    @PrimaryKey
    @ColumnInfo(index = true, name = COLUMN_ID)
    public String id;

    @NonNull
    @Ignore
    public Boolean favorite = false;

    @NonNull
    @Ignore
    public Boolean isError = false;

    @ColumnInfo(name = COLUMN_TITLE)
    public String title;

    @ColumnInfo(name = COLUMN_OVERVIEW)
    public String overview;

    @Json(name = "vote_average")
    @ColumnInfo(name = COLUMN_VOTE_AVERAGE)
    public double voteAverage;

    @ColumnInfo(name = COLUMN_POPULARITY)
    public double popularity;

    @Json(name = "release_date")
    @ColumnInfo(name = COLUMN_RELEASE_DATE)
    public String releaseDate;

    @Json(name = "poster_path")
    @ColumnInfo(name = COLUMN_POSTER_PATH)
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

    /**
     * Create a Movie object from a ContentValues object
     *
     * @param vals ContentValues object, containing the needed elements
     * @return a Movie object with the same values
     */
    @NonNull
    public static Movie fromContentValues(@NonNull ContentValues vals) {
        Movie theMovie = new Movie();
        if (vals.containsKey(COLUMN_ID)) {
            theMovie.id = vals.getAsString(COLUMN_ID);
        }
        if (vals.containsKey(COLUMN_TITLE)) {
            theMovie.title = vals.getAsString(COLUMN_TITLE);
        }
        if (vals.containsKey(COLUMN_RELEASE_DATE)) {
            theMovie.releaseDate = vals.getAsString(COLUMN_RELEASE_DATE);
        }
        if (vals.containsKey(COLUMN_VOTE_AVERAGE)) {
            theMovie.voteAverage = vals.getAsDouble(COLUMN_VOTE_AVERAGE);
        }
        if (vals.containsKey(COLUMN_POPULARITY)) {
            theMovie.popularity = vals.getAsDouble(COLUMN_POPULARITY);
        }
        if (vals.containsKey(COLUMN_POSTER_PATH)) {
            theMovie.posterPath = vals.getAsString(COLUMN_POSTER_PATH);
        }
        if (vals.containsKey(COLUMN_OVERVIEW)) {
            theMovie.overview = vals.getAsString(COLUMN_OVERVIEW);
        }
        return theMovie;
    }
}
