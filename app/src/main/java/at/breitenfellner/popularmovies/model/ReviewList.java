package at.breitenfellner.popularmovies.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * A ReviewList represents all reviews for a given movie
 */

public class ReviewList {
    @Json(name="results")
    public List<Review> reviews;
}
