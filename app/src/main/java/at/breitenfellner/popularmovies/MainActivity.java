package at.breitenfellner.popularmovies;

import android.content.Intent;
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

import com.ryanharter.auto.value.moshi.MoshiAdapterFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.MovieList;
import at.breitenfellner.popularmovies.service.MovieService;
import at.breitenfellner.popularmovies.view.MovieAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    final static int SORTORDER_POPULARITY = 0;
    final static int SORTORDER_RATING = 1;

    @BindView(R.id.sort_order) Spinner mViewSortOrder;
    @BindView(R.id.progress_bar) ProgressBar mViewPleaseWait;
    @BindView(R.id.movie_list) RecyclerView mViewMovieList;
    @BindView(R.id.error_retry) Button mButtonErrorRetry;
    @BindView(R.id.error_text) TextView mViewErrorText;
    MovieService movieService = null;
    MovieAdapter mAdapter;
    @State MovieList mMovieList;
    @State int mSortOrder;

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
        // connect with API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        movieService = retrofit.create(MovieService.class);
        // prepare recycler view
        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 4);
        mViewMovieList.setLayoutManager(layout);
        // fetch and show movies
        fetchMoviesAndShow(mSortOrder);
        mButtonErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMoviesAndShow(mSortOrder);
            }
        });
        // react on change of sort order
        mViewSortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSortOrder != position) {
                    mSortOrder = position;
                    // reset data - not valid anymore
                    mMovieList = null;
                    // load data & show it
                    fetchMoviesAndShow(mSortOrder);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fetchMoviesAndShow(int sortOrder) {
        fetchMoviesObservable(sortOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<MovieList>() {
                    @Override
                    public void onNext(@NonNull MovieList aMovieList) {
                        mMovieList = aMovieList;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (mMovieList != null) {
                            // ignore - we have old valid data
                            return;
                        }
                        mViewMovieList.setVisibility(View.GONE);
                        mViewPleaseWait.setVisibility(View.GONE);
                        if (e.getClass() == IllegalArgumentException.class) {
                            // nothing selected? clear error
                            mButtonErrorRetry.setVisibility(View.GONE);
                            mViewErrorText.setVisibility(View.GONE);
                        }
                        else {
                            // network error? show options to reload
                            mButtonErrorRetry.setVisibility(View.VISIBLE);
                            mViewErrorText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onComplete() {
                        mViewPleaseWait.setVisibility(View.GONE);
                        mViewMovieList.setVisibility(View.VISIBLE);
                        mButtonErrorRetry.setVisibility(View.GONE);
                        mViewErrorText.setVisibility(View.GONE);
                        showMovies();
                    }
                });
    }

    private void showMovies() {
        if (mMovieList != null) {
            mAdapter = new MovieAdapter(mMovieList, this);
            mViewMovieList.setAdapter(mAdapter);
            mViewMovieList.setVisibility(View.VISIBLE);
            mViewPleaseWait.setVisibility(View.GONE);
            mButtonErrorRetry.setVisibility(View.GONE);
            mViewErrorText.setVisibility(View.GONE);
        }
    }

    private MovieList fetchMovies(int sortOrder) throws IOException {
        Call<MovieList> moviesRequest;
        if (sortOrder == SORTORDER_POPULARITY) {
            moviesRequest = movieService.getMovies(
                    MovieService.SORT_POPULARITY,
                    BuildConfig.THEMOVIEDB_KEY);
        }
        else if (sortOrder == SORTORDER_RATING) {
            moviesRequest = movieService.getMovies(
                    MovieService.SORT_RATING,
                    BuildConfig.THEMOVIEDB_KEY);
        }
        else {
            // The string here is never raised to the UI and needs not to be put into a resource
            throw new IllegalArgumentException("Sortorder must be 'By Popularity' or 'By Rating'");
        }
        return moviesRequest.execute().body();
    }


    private Observable<MovieList> fetchMoviesObservable(final int sortOrder) {
        return Observable.defer(new Callable<ObservableSource<? extends MovieList>>() {
            @Override
            public ObservableSource<? extends MovieList> call() throws Exception {
                return Observable.just(fetchMovies(sortOrder));
            }
        });
    }

    @Override
    public void onMovieClick(int itemIndex) {
        Intent detailsIntent = new Intent(this, MovieDetails.class);
        Movie theMovie = mMovieList.movies.get(itemIndex);
        // set details needed
        detailsIntent.putExtra(Movie.class.getName(), theMovie);
        // Open new activity
        startActivity(detailsIntent);
    }
}
