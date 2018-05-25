package com.android.gowtham.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.gowtham.popularmovies.R;
import com.android.gowtham.popularmovies.dto.ReviewDto;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context applicationContext;
    private List<ReviewDto> reviewList;

    public ReviewAdapter(Context applicationContext, List<ReviewDto> reviewList) {
        this.applicationContext = applicationContext;
        this.reviewList = reviewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(applicationContext).inflate(R.layout.review_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindReview(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private final TextView tvAuthor;
        private final TextView tvReviewComment;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
        }

        void bindReview(ReviewDto reviewDto) {
            itemView.setTag(reviewDto);
            tvAuthor.setText(reviewDto.getAuthor());
            tvReviewComment.setText(reviewDto.getReviewContent());
        }
    }
}
