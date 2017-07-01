package at.breitenfellner.popularmovies.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

/**
 * This is the Data Access Object definition for the table of Movies.
 */

@Dao
public interface MovieDao {

    /**
     * Get the number of movies in the table
     *
     * @return number of all movies in the table
     */
    @Query("select count(*) from " + Movie.TABLE_NAME)
    int count();

    /**
     * Get all movies in the table
     *
     * @return cursor with all movies in the table
     */
    @Query("SELECT * from " + Movie.TABLE_NAME)
    Cursor selectAll();

    /**
     * Insert a single movie into the database
     *
     * @param movie movie object to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Movie movie);

    /**
     * Insert an array of movies into the table
     *
     * @param movies movies to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Movie[] movies);

    /**
     * Get a movie specified by its ID value
     *
     * @param id identifier of the movie (as on TheMovieDB)
     * @return the corresponding movie
     */
    @Query("select * from " + Movie.TABLE_NAME + " where " + Movie.COLUMN_ID + "=:id")
    Cursor selectById(String id);

    /**
     * Delete a specific movie
     *
     * @param id identifier of the movie to be deleted
     * @return number of rows affected ({@code 0} or {@code 1})
     */
    @Query("delete from " + Movie.TABLE_NAME + " where " + Movie.COLUMN_ID + "=:id")
    int deleteById(String id);

}
