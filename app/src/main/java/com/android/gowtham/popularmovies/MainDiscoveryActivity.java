package com.android.gowtham.popularmovies;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.android.gowtham.popularmovies.db.MoviesDBHelper;
import com.android.gowtham.popularmovies.db.MoviesListAdapter;
import com.android.gowtham.popularmovies.domain.Movie;
import com.android.gowtham.popularmovies.dto.MovieDto;
import com.android.gowtham.popularmovies.network.HttpMoviesAsyncTaskLoader;
import com.android.gowtham.popularmovies.utils.MovieConstant;

import java.util.ArrayList;
import java.util.List;

public class MainDiscoveryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ID = 1234;
    public static boolean sIsDataChanged = false;

    private MoviesDBHelper dbHelper;
    private GridView movieThumbnails;
    private TextView tvUnableToFetchData;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery);

        if(MovieConstant.API_KEY_VALUE.length() == 0) {
            throw new RuntimeException("Please add TMDb Api key to the variable \"MovieConstant.API_KEY_VALUE\" and run the project again");
        }

        dbHelper = new MoviesDBHelper(getApplicationContext());

        movieThumbnails = findViewById(R.id.lvTitlesHolder);
        tvUnableToFetchData = findViewById(R.id.tv_no_internet_message_holder);

        sortByPopularity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sIsDataChanged) {
            showMoviesBasedOnSelection(itemId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (showMoviesBasedOnSelection(itemId)) return true;
        return super.onOptionsItemSelected(item);
    }

    private boolean showMoviesBasedOnSelection(int itemId) {
        this.itemId = itemId;
        switch (this.itemId) {
            case R.id.action_sort_by_popularity:
                sortByPopularity();
                return true;
            case R.id.action_sort_by_rating:
                sortByRating();
                return true;
            case R.id.action_show_favorites:
                showFavorites();
                return true;

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getTag() == null) {
            return;
        }
        int id = Integer.parseInt((String) v.getTag());
        MovieDto movieDetails = dbHelper.getMovieDetails(id);

        if(movieDetails == null) {
            return;
        }

        startDetailsActivity(new Movie(movieDetails));
    }

    private void startDetailsActivity(Movie movieDetails) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieConstant.MOVIE_DOMAIN, movieDetails);
        startActivity(intent);
    }

    private class MovieDownloadListener implements Loader.OnLoadCompleteListener<java.util.List<com.android.gowtham.popularmovies.dto.MovieDto>> {
        @Override
        public void onLoadComplete(Loader<List<MovieDto>> loader, List<MovieDto> data) {
            if(data == null) {
                tvUnableToFetchData.setVisibility(View.VISIBLE);
                return;
            }
            tvUnableToFetchData.setVisibility(View.GONE);
            List<Movie> movies = new ArrayList<>();
            for(MovieDto dto : data) {
                movies.add(new Movie(dto));
            }
            dbHelper.addMovies(movies);
            loadMovieToGridView(dbHelper.getMovies());
        }
    }

    private void sortByRating() {
        HttpMoviesAsyncTaskLoader httpMoviesAsyncTaskLoader = new HttpMoviesAsyncTaskLoader(this, MovieConstant.SORT_BY_RATING);
        httpMoviesAsyncTaskLoader.registerListener(ID, new MovieDownloadListener());
        httpMoviesAsyncTaskLoader.forceLoad();
    }

    private void sortByPopularity() {
        HttpMoviesAsyncTaskLoader httpMoviesAsyncTaskLoader = new HttpMoviesAsyncTaskLoader(this, MovieConstant.SORT_BY_POPULARITY);
        httpMoviesAsyncTaskLoader.registerListener(ID, new MovieDownloadListener());
        httpMoviesAsyncTaskLoader.forceLoad();
    }

    private void showFavorites() {
        tvUnableToFetchData.setVisibility(View.GONE);
        loadMovieToGridView(dbHelper.getFavoriteMovies());
    }

    private void loadMovieToGridView(Cursor moviesSortByRating) {
        MoviesListAdapter adapter = new MoviesListAdapter(this, moviesSortByRating, this);
        movieThumbnails.setAdapter(adapter);
        movieThumbnails.invalidate();
    }
}
