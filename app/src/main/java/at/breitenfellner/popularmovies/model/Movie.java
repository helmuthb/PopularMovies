package at.breitenfellner.popularmovies.model;

import com.squareup.moshi.Json;

import java.io.Serializable;

/**
 * Created by helmuth on 16/06/2017.
 *
 * This class is the interface between the JSON API and the Android code.
 * Every field with a name matching a field as specified in the JSON
 * data will automatically be matched by Moshi.
 *
 * Some names have been changed to follow the normal "CamelCase"
 * convention for variable names, and annotations have been added
 * with the corresponding JSON attribute name.
 *
 * To allow the object to be passed as Intent extra data it declares to be Serializable.
 * Another option would be to implement Parcelable, which could also be simplified with
 * using appropriate extensions. However, it is (currently) only one object at a time which is
 * marshalled to show the details view - the performance overhead of Serializable can easily
 * be ignored in this case.
 */

public class Movie implements Serializable {

    @Json(name="vote_average")
    public Double voteAverage;

    public Double popularity;

    public String title;

    @Json(name="release_date")
    public String releaseDate;

    @Json(name="poster_path")
    public String posterPath;

    public String overview;
}
