package at.breitenfellner.popularmovies.model;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by helmuth on 16/06/2017.
 */

public class MovieList implements Serializable {
    @Json(name="results")
    public List<Movie> movies;
}
