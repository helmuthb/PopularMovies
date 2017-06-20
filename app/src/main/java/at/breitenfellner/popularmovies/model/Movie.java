package at.breitenfellner.popularmovies.model;

import com.squareup.moshi.Json;

import java.io.Serializable;

/**
 * Created by helmuth on 16/06/2017.
 */

public class Movie implements Serializable {
    public Double vote_average;
    public Double popularity;
    public String title;
    public String release_date;
    public String poster_path;
    public String overview;
}
