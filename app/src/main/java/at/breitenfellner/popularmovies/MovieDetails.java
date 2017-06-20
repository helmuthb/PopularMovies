package at.breitenfellner.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import at.breitenfellner.popularmovies.model.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;

public class MovieDetails extends AppCompatActivity {

    @BindView(R.id.movie_details_release_date) TextView mReleaseDate;
    @BindView(R.id.movie_details_vote_average) TextView mVoteAverage;
    @BindView(R.id.movie_details_plot) TextView mPlot;
    @BindView(R.id.movie_details_poster) ImageView mPoster;
    @State Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        // get intent data (if any)
        Intent data = getIntent();
        if (data.hasExtra(Movie.class.getName())) {
            // we got passed a movie!
            mMovie = (Movie)data.getSerializableExtra(Movie.class.getName());
            // show the data ...
            setTitle(mMovie.title);
            mReleaseDate.setText("Release date: " + mMovie.release_date);
            mPlot.setText(mMovie.overview);
            mVoteAverage.setText("Average rating: " + mMovie.vote_average);
            String baseUrl = "http://image.tmdb.org/t/p/w780";
            Picasso.with(this)
                    .load(baseUrl + mMovie.poster_path)
                    .into(mPoster);
        }
    }
}
