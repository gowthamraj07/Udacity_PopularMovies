package com.android.gowtham.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.gowtham.popularmovies.dto.MovieDto;
import com.android.gowtham.popularmovies.utils.MovieConstant;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvRating = findViewById(R.id.tvRating);
        TextView tvReleaseDate = findViewById(R.id.tvReleaseDate);
        TextView tvSynopsis = findViewById(R.id.tvSynopsis);
        ImageView ivThumbnail = findViewById(R.id.ivThumbnail);

        Intent intent = getIntent();
        MovieDto dto = (MovieDto) intent.getSerializableExtra("MOVIE_DTO");
        String title = dto.getTitle();
        String rating = dto.getVoteAverage()+" / 10";
        String releaseDate = dto.getReleaseDate();
        String synopsis = dto.getOverview();
        String thumbnailUrl = dto.getPosterPath();

        tvTitle.setText(title);
        tvRating.setText(rating);
        tvReleaseDate.setText(releaseDate);
        tvSynopsis.setText(synopsis);
        Picasso.with(this).load(getAbsolutePath(thumbnailUrl)).into(ivThumbnail);

    }

    private String getAbsolutePath(String thumbnailUrl) {
        Uri build = new Uri.Builder()
                .scheme("http")
                .path(MovieConstant.BASE_URL)
                .appendPath("w500")
                .appendPath(thumbnailUrl.substring(1))
                .build();
        String path = build.toString();
        Log.i(getClass().getName(), path);
        return path;
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
}
