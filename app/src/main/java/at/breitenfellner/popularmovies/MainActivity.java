package at.breitenfellner.popularmovies;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.MovieList;
import at.breitenfellner.popularmovies.service.MovieService;
import at.breitenfellner.popularmovies.view.MovieAdapter;
import at.breitenfellner.popularmovies.viewmodel.MovieListViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieClickListener,
        LifecycleRegistryOwner {

    private final static int SORTORDER_POPULARITY = 0;
    final static int SORTORDER_RATING = 1;
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @BindView(R.id.sort_order)
    Spinner mViewSortOrder;
    @BindView(R.id.progress_bar)
    ProgressBar mViewPleaseWait;
    @BindView(R.id.movie_list)
    RecyclerView mViewMovieList;
    @BindView(R.id.error_retry)
    Button mButtonErrorRetry;
    @BindView(R.id.error_text)
    TextView mViewErrorText;
    @Nullable
    private MovieAdapter mAdapter;
    private MovieListViewModel viewModel;
    @State
    int mSortOrder;

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
        mViewSortOrder.setAdapter(adapter);
        // prepare recycler view
        // number of columns from a resource -> adjustable by size of screen
        int columns = getResources().getInteger(R.integer.columns);
        RecyclerView.LayoutManager layout = new GridLayoutManager(this, columns);
        mViewMovieList.setLayoutManager(layout);
        // activate "retry" button
        mButtonErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMovies(mSortOrder);
            }
        });
        // react on change of sort order
        mViewSortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSortOrder != position) {
                    mSortOrder = position;
                    // load data
                    fetchMovies(mSortOrder);
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
                mAdapter = new MovieAdapter(movieList, MainActivity.this);
                mViewMovieList.setAdapter(mAdapter);
                // show the status
                showStatus(movieList);
            }
        });
        // fetch movies
        fetchMovies(mSortOrder);
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
            mViewMovieList.setVisibility(View.GONE);
            mButtonErrorRetry.setVisibility(View.GONE);
            mViewErrorText.setVisibility(View.GONE);
            mViewPleaseWait.setVisibility(View.VISIBLE);
        } else if (movieList.isError) {
            // Error
            mViewMovieList.setVisibility(View.GONE);
            mButtonErrorRetry.setVisibility(View.VISIBLE);
            mViewErrorText.setVisibility(View.VISIBLE);
            mViewPleaseWait.setVisibility(View.GONE);
        } else {
            // Data
            mViewMovieList.setVisibility(View.VISIBLE);
            mButtonErrorRetry.setVisibility(View.GONE);
            mViewErrorText.setVisibility(View.GONE);
            mViewPleaseWait.setVisibility(View.GONE);
        }
    }

    private void fetchMovies(int sortOrder) {
        viewModel.loadMovies(sortOrder == SORTORDER_POPULARITY
                ? MovieService.SORT_POPULARITY
                : MovieService.SORT_RATING);
    }

    @Override
    public void onMovieClick(int itemIndex) {
        Movie theMovie = viewModel.getMovieByIndex(itemIndex);
        if (theMovie != null) {
            Intent detailsIntent = new Intent(this, MovieDetailsActivity.class);
            // set details needed
            detailsIntent.putExtra(Movie.class.getName(), theMovie.id);
            // Open new activity
            startActivity(detailsIntent);
        }
    }

    @NonNull
    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
