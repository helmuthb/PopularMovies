package at.breitenfellner.popularmovies.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.MovieDao;

/**
 * This is the database object, representing the SQLite file stored on Android.
 */

@Database(entities = {Movie.class}, version = 1)
public abstract class FavoritesDb extends RoomDatabase {
    /**
     * Get movie DAO
     *
     * @return the MovieDAO implementation
     */
    public abstract MovieDao movie();
}
