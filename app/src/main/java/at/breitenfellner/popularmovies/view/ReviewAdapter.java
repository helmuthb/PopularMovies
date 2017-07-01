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
import at.breitenfellner.popularmovies.model.Review;
import at.breitenfellner.popularmovies.model.ReviewList;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to show the reviews of a movie in a RecyclerView
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final List<Review> reviewList;

    public ReviewAdapter(ReviewList reviewList) {
        if (reviewList != null && reviewList.reviews != null) {
            this.reviewList = reviewList.reviews;
        }
        else {
            this.reviewList = new ArrayList<Review>();
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.review_listitem, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (reviewList == null) {
            return 0;
        }
        return reviewList.size();
    }

    /**
     * Class implementing the ViewHolder pattern for reviews.
     */
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.review_text)
        TextView reviewText;

        Review theReview;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int listIndex) {
            theReview = reviewList.get(listIndex);
            reviewText.setText(theReview.content);
        }
    }
}
