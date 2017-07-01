package at.breitenfellner.popularmovies.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import at.breitenfellner.popularmovies.R;
import at.breitenfellner.popularmovies.model.MovieList;
import at.breitenfellner.popularmovies.model.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * This is an Adapter class for a RecyclerView, allowing to display posters from movies.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final MovieList mMovieList;
    private final MovieClickListener mClickListener;

    public MovieAdapter(MovieList movies, MovieClickListener clickListener)
    {
        mMovieList = movies;
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.movie_listitem, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null || mMovieList.movies == null) {
            return 0;
        }
        return mMovieList.movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Nullable
        @BindView(R.id.movie_poster) ImageView moviePoster;
        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            Movie theMovie = mMovieList.movies.get(listIndex);
            Context ctx = moviePoster.getContext();
            String baseUrl = ctx.getResources().getString(R.string.tmdb_image_baseurl_small);
            Picasso.with(ctx)
                   .load(baseUrl + theMovie.posterPath)
                   .into(moviePoster);
            // set content description for accessibility
            moviePoster.setContentDescription(theMovie.title);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onMovieClick(getAdapterPosition());
        }
    }

    public interface MovieClickListener {
        void onMovieClick(int itemIndex);
    }
}
