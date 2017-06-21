package at.breitenfellner.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import at.breitenfellner.popularmovies.model.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;

public class MovieDetails extends AppCompatActivity {

    @BindView(R.id.movie_details_release_date) TextView mViewReleaseDate;
    @BindView(R.id.movie_details_vote_average) TextView mViewVoteAverage;
    @BindView(R.id.movie_details_plot) TextView mViewPlot;
    @BindView(R.id.movie_details_poster) ImageView mViewPoster;
    @BindView(R.id.movie_details_toolbar) Toolbar mToolbar;
    @BindView(R.id.movie_details_collapsing_layout) CollapsingToolbarLayout mCollapsingLayout;
    @State Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        // set toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get intent data (if any)
        Intent data = getIntent();
        if (data.hasExtra(Movie.class.getName())) {
            // we got passed a movie!
            mMovie = (Movie)data.getSerializableExtra(Movie.class.getName());
            // show the data ...
            setTitle(mMovie.title);
            Resources res = getResources();
            mViewReleaseDate.setText(res.getString(R.string.release_date, mMovie.releaseDate));
            mViewVoteAverage.setText(res.getString(R.string.average_rating, mMovie.voteAverage));
            mViewPlot.setText(mMovie.overview);
            String baseUrl = "http://image.tmdb.org/t/p/w780";
            Picasso.with(this)
                    .load(baseUrl + mMovie.posterPath)
                    .into(mViewPoster);
            mViewPoster.setContentDescription(mMovie.title);
            mCollapsingLayout.setTitle(mMovie.title);
        }
    }
}
