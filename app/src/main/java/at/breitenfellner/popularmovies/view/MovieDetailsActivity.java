package at.breitenfellner.popularmovies.view;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import at.breitenfellner.popularmovies.R;
import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.model.ReviewList;
import at.breitenfellner.popularmovies.model.Trailer;
import at.breitenfellner.popularmovies.model.TrailerList;
import at.breitenfellner.popularmovies.viewmodel.MovieDetailsViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity displays a selected movie chosen by the user.
 * The information which movie was selected is hold in a ViewModel and will therefore
 * survive rotation of the screen.
 * There was an issue with retaining the scroll position after rotation.
 * This was solved with the answer here: https://stackoverflow.com/a/39154266/813725
 */
public class MovieDetailsActivity extends AppCompatActivity
        implements LifecycleRegistryOwner, TrailerAdapter.TrailerClickListener {
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private MovieDetailsViewModel viewModel;

    @BindView(R.id.movie_details)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.movie_details_appbar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.movie_details_scroll)
    View scrollView;
    @BindView(R.id.movie_details_release_date)
    TextView textviewReleaseDate;
    @BindView(R.id.movie_details_vote_average)
    TextView textviewVoteAverage;
    @BindView(R.id.movie_details_plot)
    TextView textviewPlot;
    @BindView(R.id.movie_details_poster)
    ImageView imageviewPoster;
    @BindView(R.id.movie_details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.movie_details_collapsing_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.movie_details_trailers)
    RecyclerView recyclerviewTrailers;
    @BindView(R.id.movie_details_reviews)
    RecyclerView recyclerviewReviews;
    MenuItem favoriteMenuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String movieId;
        super.onCreate(savedInstanceState);
        // load and initialize views
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        // set toolbar
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // activate recyclerview for trailers
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        recyclerviewTrailers.setLayoutManager(layout);
        // activate recyclerview for reviews
        layout = new LinearLayoutManager(this);
        recyclerviewReviews.setLayoutManager(layout);
        // get ViewModel
        viewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);
        // bind the ViewModel fields to the display
        final Resources res = getResources();
        viewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (movie != null) {
                    // title
                    setTitle(movie.title);
                    collapsingToolbarLayout.setTitle(movie.title);
                    imageviewPoster.setContentDescription(movie.title);
                    // plot
                    textviewPlot.setText(movie.overview);
                    // release data
                    textviewReleaseDate.setText(res.getString(R.string.release_date, movie.releaseDate));
                    // vote average
                    textviewVoteAverage.setText(res.getString(R.string.average_rating, movie.voteAverage));
                    // poster
                    Picasso.with(MovieDetailsActivity.this)
                            .load(viewModel.getPosterUrl())
                            .into(imageviewPoster);
                }
            }
        });
        viewModel.getTrailers().observe(this, new Observer<TrailerList>() {
            @Override
            public void onChanged(@Nullable TrailerList trailerList) {
                if (trailerList != null) {
                    recyclerviewTrailers.setAdapter(
                            new TrailerAdapter(trailerList, MovieDetailsActivity.this)
                    );
                }
            }
        });
        viewModel.getReviews().observe(this, new Observer<ReviewList>() {
            @Override
            public void onChanged(@Nullable ReviewList reviewList) {
                if (reviewList != null) {
                    recyclerviewReviews.setAdapter(new ReviewAdapter(reviewList));
                }
            }
        });
        // get intent data (if any)
        Intent data = getIntent();
        if (data.hasExtra(Movie.class.getName())) {
            // we got passed a movie!
            movieId = data.getStringExtra(Movie.class.getName());
            // set the data in the ViewModel
            viewModel.loadMovie(movieId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        favoriteMenuItem = menu.findItem(R.id.menu_favorite);
        viewModel.isFavorite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    favoriteMenuItem.setIcon(R.drawable.ic_favorite);
                } else {
                    favoriteMenuItem.setIcon(R.drawable.ic_make_favorite);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favorite) {
            viewModel.toggleFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        viewModel.showTrailer(trailer);
    }
}
