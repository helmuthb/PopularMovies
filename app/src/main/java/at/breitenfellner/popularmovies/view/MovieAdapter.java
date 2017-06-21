package at.breitenfellner.popularmovies.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import at.breitenfellner.popularmovies.R;
import at.breitenfellner.popularmovies.model.MovieList;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by helmuth on 17/06/2017.
 *
 * This is an Adapter class for a RecyclerView, allowing to display posters from movies.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private MovieList mMovieList;
    private MovieClickListener mClickListener;

    public MovieAdapter(MovieList movies, MovieClickListener clickListener)
    {
        mMovieList = movies;
        mClickListener = clickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.movie_listitem, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
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
        @BindView(R.id.movie_poster) ImageView moviePoster;
        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            String baseUrl = "http://image.tmdb.org/t/p/w185";
            Picasso.with(moviePoster.getContext())
                   .load(baseUrl + mMovieList.movies.get(listIndex).posterPath)
                   .into(moviePoster);
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
