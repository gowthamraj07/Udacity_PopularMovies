package com.android.gowtham.popularmovies;

import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.android.gowtham.popularmovies.adapter.ReviewAdapter;
import com.android.gowtham.popularmovies.domain.Movie;
import com.android.gowtham.popularmovies.dto.ReviewDto;
import com.android.gowtham.popularmovies.network.HttpReviewsAsyncTaskLoader;
import com.android.gowtham.popularmovies.utils.MovieConstant;

import java.util.List;

public class MovieReviewActivity extends AppCompatActivity {

    private long movieId;
    private RecyclerView rvReviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review);

        Intent intent = getIntent();
        Movie dto = (Movie) intent.getParcelableExtra(MovieConstant.MOVIE_DOMAIN);
        movieId = dto.getMovieId();

        rvReviewList = findViewById(R.id.rvReviewList);

    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpReviewsAsyncTaskLoader reviewLoader = new HttpReviewsAsyncTaskLoader(this, movieId);
        reviewLoader.registerListener(1234, new ReviewListener(rvReviewList));
        reviewLoader.forceLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ReviewListener implements Loader.OnLoadCompleteListener<List<ReviewDto>> {
        private RecyclerView rvReviewList;

        ReviewListener(RecyclerView rvReviewList) {
            this.rvReviewList = rvReviewList;
        }

        @Override
        public void onLoadComplete(Loader<List<ReviewDto>> loader, List<ReviewDto> data) {
            rvReviewList.setAdapter(new ReviewAdapter(MovieReviewActivity.this, data));
        }
    }
}
