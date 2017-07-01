package at.breitenfellner.popularmovies.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * A TrailerList represents all trailers of a movie.
 */

public class TrailerList {
    @Json(name="results")
    public List<Trailer> trailers;

    public boolean isError = false;
}
