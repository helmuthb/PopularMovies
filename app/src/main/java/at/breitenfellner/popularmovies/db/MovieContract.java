package at.breitenfellner.popularmovies.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * MovieContract, following the Udacity code for a Contract class
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "at.breitenfellner.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
    }
}
