package at.breitenfellner.popularmovies.view;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.breitenfellner.popularmovies.R;
import at.breitenfellner.popularmovies.db.MovieContract;
import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.MovieList;
import at.breitenfellner.popularmovies.service.MovieService;
import at.breitenfellner.popularmovies.viewmodel.MovieListViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieClickListener,
        LifecycleRegistryOwner,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final static int SORTORDER_POPULARITY = 0;
    private final static int SORTORDER_RATING = 1;
    private final static int SORTORDER_FAVORITES = 2;
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    private final static int ID_MOVIELIST_LOADER = 123;
    private final static String[] MOVIELIST_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID
    };

    @BindView(R.id.sort_order)
    Spinner spinnerSortOrder;
    @BindView(R.id.progress_bar)
    ProgressBar viewPleaseWait;
    @BindView(R.id.movie_list)
    RecyclerView recyclerviewMovies;
    @BindView(R.id.error_retry)
    Button buttonErrorRetry;
    @BindView(R.id.error_text)
    TextView textviewError;
    private MovieListViewModel viewModel;
    @State
    int sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // show options for sort order
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_order,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortOrder.setAdapter(adapter);
        // prepare recycler view
        // number of columns from a resource -> adjustable by size of screen
        int columns = getResources().getInteger(R.integer.columns);
        RecyclerView.LayoutManager layout = new GridLayoutManager(this, columns);
        recyclerviewMovies.setLayoutManager(layout);
        // activate "retry" button
        buttonErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMovies(sortOrder);
            }
        });
        // react on change of sort order
        spinnerSortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sortOrder != position) {
                    sortOrder = position;
                    // load data
                    fetchMovies(sortOrder);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // show / hide fields for loading
        showStatus(null);
        // connect with ViewModel
        viewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        // bind view model to fields
        viewModel.getMovieList().observe(this, new Observer<MovieList>() {
            @Override
            public void onChanged(@Nullable MovieList movieList) {
                // show the movies
                MovieAdapter adapter = new MovieAdapter(movieList, MainActivity.this);
                recyclerviewMovies.setAdapter(adapter);
                // show the status
                showStatus(movieList);
            }
        });
        // fetch movies
        fetchMovies(sortOrder);
        // query favorite movies
        getSupportLoaderManager().initLoader(ID_MOVIELIST_LOADER, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Display the current status, i.e. show/hide the elements relevant.
     * Status could be:
     * <p><ul>
     * <li>New or loading - show progress but no results, retry or error</li>
     * <li>Error - show no progress or results, but retry and error</li>
     * <li>Data - show no progress or retry or error, but results</li>
     * </ul></p>
     *
     * @param movieList the MovieList for which the fields shall be adapted
     */
    private void showStatus(@Nullable MovieList movieList) {
        if (movieList == null) {
            // New or loading
            recyclerviewMovies.setVisibility(View.GONE);
            buttonErrorRetry.setVisibility(View.GONE);
            textviewError.setVisibility(View.GONE);
            viewPleaseWait.setVisibility(View.VISIBLE);
        } else if (movieList.isError) {
            // Error
            recyclerviewMovies.setVisibility(View.GONE);
            buttonErrorRetry.setVisibility(View.VISIBLE);
            textviewError.setVisibility(View.VISIBLE);
            viewPleaseWait.setVisibility(View.GONE);
        } else {
            // Data
            recyclerviewMovies.setVisibility(View.VISIBLE);
            buttonErrorRetry.setVisibility(View.GONE);
            textviewError.setVisibility(View.GONE);
            viewPleaseWait.setVisibility(View.GONE);
        }
    }

    private void fetchMovies(int sortOrder) {
        viewModel.loadMovies(sortOrder == SORTORDER_POPULARITY
                        ? MovieService.SORT_POPULARITY
                        : MovieService.SORT_RATING,
                sortOrder == SORTORDER_FAVORITES);
    }

    @Override
    public void onMovieClick(Movie theMovie) {
        viewModel.openMovieView(theMovie);
    }

    @NonNull
    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIELIST_LOADER:
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                return new CursorLoader(
                        this,
                        movieQueryUri,
                        MOVIELIST_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new UnsupportedOperationException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<String> favoriteIds = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            favoriteIds.add(data.getString(0));
            data.moveToNext();
        }
        viewModel.notifyFavorites(favoriteIds);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
