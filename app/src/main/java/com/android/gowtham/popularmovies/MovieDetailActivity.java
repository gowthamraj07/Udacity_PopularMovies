package com.android.gowtham.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.gowtham.popularmovies.adapter.TrailerAdapter;
import com.android.gowtham.popularmovies.db.MoviesDBHelper;
import com.android.gowtham.popularmovies.domain.Movie;
import com.android.gowtham.popularmovies.dto.TrailerDto;
import com.android.gowtham.popularmovies.network.HttpTrailersAsyncTaskLoader;
import com.android.gowtham.popularmovies.providers.MovieProvider;
import com.android.gowtham.popularmovies.utils.MovieConstant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private long movieId;
    private RecyclerView rvTrailer;
    private Movie domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvRating = findViewById(R.id.tvRating);
        TextView tvReleaseDate = findViewById(R.id.tvReleaseDate);
        TextView tvSynopsis = findViewById(R.id.tvSynopsis);
        ImageView ivThumbnail = findViewById(R.id.ivThumbnail);
        ToggleButton tbtnFavorite = findViewById(R.id.tBtnFavorite);
        rvTrailer = findViewById(R.id.rvTrailers);

        Intent intent = getIntent();
        domain = (Movie) intent.getParcelableExtra(MovieConstant.MOVIE_DOMAIN);
        movieId = domain.getMovieId();
        String title = domain.getTitle();
        String rating = "" + domain.getVote() + " / 10";
        String releaseDate = domain.getReleaseDate();
        String synopsis = domain.getSynopsis();
        String thumbnailUrl = domain.getImageUrl();

        tvTitle.setText(title);
        tvRating.setText(rating);
        tvReleaseDate.setText(releaseDate);
        tvSynopsis.setText(synopsis);

        tbtnFavorite.setOnCheckedChangeListener(new FavoriteStateChangeListener());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvTrailer.setLayoutManager(layoutManager);

        MoviesDBHelper moviesDBHelper = new MoviesDBHelper(getApplicationContext());
        boolean favorite = moviesDBHelper.isFavorite(domain);

        tbtnFavorite.setChecked(favorite);
        MainDiscoveryActivity.sIsDataChanged = false;

        Picasso.with(this).load(getAbsolutePath(thumbnailUrl)).into(ivThumbnail);

        Log.i(MovieDetailActivity.class.getSimpleName(), domain.getTitle() + ":" + domain.getMovieId());

    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpTrailersAsyncTaskLoader trailerLoader = new HttpTrailersAsyncTaskLoader(this, movieId);
        trailerLoader.registerListener(1234, new TrailerListener(rvTrailer));
        trailerLoader.forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_show_review) {
            Intent intent = new Intent(this, MovieReviewActivity.class);
            intent.putExtra(MovieConstant.MOVIE_DOMAIN, getIntent().getParcelableExtra(MovieConstant.MOVIE_DOMAIN));
            startActivity(intent);
            return true;
        }

        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private class TrailerListener implements android.content.Loader.OnLoadCompleteListener<java.util.List<com.android.gowtham.popularmovies.dto.TrailerDto>> {
        private RecyclerView rvTrailer;

        TrailerListener(RecyclerView rvTrailer) {
            this.rvTrailer = rvTrailer;
        }

        @Override
        public void onLoadComplete(Loader<List<TrailerDto>> loader, List<TrailerDto> data) {
            if (data == null) {
                return;
            }

            rvTrailer.setAdapter(new TrailerAdapter(getApplicationContext(), data));
            rvTrailer.invalidate();
            Log.i(TrailerListener.class.getSimpleName(), "Response received");
        }
    }

    private class FavoriteStateChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            MainDiscoveryActivity.sIsDataChanged = true;

            if (isChecked) {
                ContentValues contentValue = domain.getContentValue();
                getContentResolver().insert(MovieProvider.FAVORITE_CONTENT_URI, contentValue);
            } else {
                getContentResolver().delete(MovieProvider.FAVORITE_CONTENT_URI.buildUpon().appendPath(""+ domain.getMovieId()).build(), null, null);
            }
        }
    }
}
