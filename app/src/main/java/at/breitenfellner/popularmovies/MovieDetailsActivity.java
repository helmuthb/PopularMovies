package at.breitenfellner.popularmovies;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import at.breitenfellner.popularmovies.model.Movie;
import at.breitenfellner.popularmovies.viewmodel.MovieDetailsViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity displays a selected movie chosen by the user.
 * The information which movie was selected is hold in a ViewModel and will therefore
 * survive rotation of the screen.
 */
public class MovieDetailsActivity extends AppCompatActivity
        implements LifecycleRegistryOwner {
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private MovieDetailsViewModel viewModel;

    @BindView(R.id.movie_details_release_date)
    TextView mViewReleaseDate;
    @BindView(R.id.movie_details_vote_average)
    TextView mViewVoteAverage;
    @BindView(R.id.movie_details_plot)
    TextView mViewPlot;
    @BindView(R.id.movie_details_poster)
    ImageView mViewPoster;
    @BindView(R.id.movie_details_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.movie_details_collapsing_layout)
    CollapsingToolbarLayout mCollapsingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String movieId;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        // set toolbar
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    mCollapsingLayout.setTitle(movie.title);
                    mViewPoster.setContentDescription(movie.title);
                    // plot
                    mViewPlot.setText(movie.overview);
                    // release data
                    mViewReleaseDate.setText(res.getString(R.string.release_date, movie.releaseDate));
                    // vote average
                    mViewVoteAverage.setText(res.getString(R.string.average_rating, movie.voteAverage));
                    // poster
                    Picasso.with(MovieDetailsActivity.this)
                            .load(viewModel.getPosterUrl())
                            .into(mViewPoster);
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

    @NonNull
    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
