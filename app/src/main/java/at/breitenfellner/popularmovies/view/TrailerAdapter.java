package at.breitenfellner.popularmovies.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.breitenfellner.popularmovies.R;
import at.breitenfellner.popularmovies.model.Trailer;
import at.breitenfellner.popularmovies.model.TrailerList;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to show the trailers of a movie in a RecyclerView
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private final TrailerClickListener clickListener;
    private final List<Trailer> trailerList;

    public TrailerAdapter(TrailerList trailerList, TrailerClickListener clickListener) {
        this.clickListener = clickListener;
        List<Trailer> trailers = new ArrayList<>();
        if (trailerList != null && trailerList.trailers != null) {
            for (Trailer t : trailerList.trailers) {
                if (t.site.equals(Trailer.youtube)) {
                    trailers.add(t);
                }
            }
        }
        this.trailerList = trailers;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.trailer_listitem, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (trailerList == null) {
            return 0;
        }
        return trailerList.size();
    }

    /**
     * Class implementing the ViewHolder pattern for trailers.
     */
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Nullable
        @BindView(R.id.trailer_text)
        TextView trailerText;

        Trailer theTrailer;

        TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onTrailerClick(theTrailer);
        }

        void bind(int listIndex) {
            theTrailer = trailerList.get(listIndex);
            trailerText.setText(theTrailer.name);
        }
    }

    public interface TrailerClickListener {
        void onTrailerClick(Trailer trailer);
    }
}
