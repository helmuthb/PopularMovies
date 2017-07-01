package at.breitenfellner.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import at.breitenfellner.popularmovies.model.Movie;

/**
 * Implementation of a content provider based on the Favorites database
 */

public class FavoritesContentProvider extends ContentProvider {
    public static final String AUTHORITY = "at.breitenfellner.popularmovies";
    public static final Uri URI_MOVIE = Uri.parse(
            "content://" + AUTHORITY + "/" + Movie.TABLE_NAME);
    private static final int CODE_MOVIE_DIR = 1;
    private static final int CODE_MOVIE_ITEM = 2;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        MATCHER.addURI(AUTHORITY, Movie.TABLE_NAME, CODE_MOVIE_DIR);
        MATCHER.addURI(AUTHORITY, Movie.TABLE_NAME + "/*", CODE_MOVIE_ITEM);
    }

    /**
     * onCreate is called when the content provider is created.
     * No preparations are needed in our case.
     *
     * @return {@code true} to indicate that the content provider was created successfully
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * Perform a query to return data to the caller.
     *
     * @param uri           the URI of the resource
     * @param projection    which columns to return
     * @param selection     which rows to return - structure of the restriction
     * @param selectionArgs which rows to return - values of the restriction
     * @param sortOrder     by which column to sort the return values
     * @return a cursor with the results of the requested query
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
