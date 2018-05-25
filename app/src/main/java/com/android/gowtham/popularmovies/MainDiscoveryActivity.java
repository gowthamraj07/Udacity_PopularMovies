package com.android.gowtham.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.gowtham.popularmovies.db.MoviesListAdapter;
import com.android.gowtham.popularmovies.domain.Movie;
import com.android.gowtham.popularmovies.dto.MovieDto;
import com.android.gowtham.popularmovies.network.HttpMoviesAsyncTaskLoader;
import com.android.gowtham.popularmovies.providers.MovieProvider;
import com.android.gowtham.popularmovies.utils.MovieConstant;

import java.util.List;

public class MainDiscoveryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ID = 1234;
    public static final String ITEM_ID = "ITEM_ID";
    public static boolean sIsDataChanged = false;
    private static int itemId;

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    private RecyclerView movieThumbnails;
    private TextView tvUnableToFetchData;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery);

        if (MovieConstant.API_KEY_VALUE.length() == 0) {
            throw new RuntimeException(getString(R.string.TMDb_api_missing_message));
        }

        movieThumbnails = findViewById(R.id.lvTitlesHolder);
        tvUnableToFetchData = findViewById(R.id.tv_no_internet_message_holder);

        itemId = R.id.action_sort_by_popularity;
        showMovies(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(itemId != R.id.action_show_favorites) {
            sIsDataChanged = false;
        }

        if (sIsDataChanged) {
            showMoviesBasedOnSelection(itemId);
            sIsDataChanged = false;
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
        return showMoviesBasedOnSelection(itemId) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ITEM_ID, itemId);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, movieThumbnails.getLayoutManager().onSaveInstanceState());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onRestoreInstanceState(this.savedInstanceState);

        if (savedInstanceState == null) {
            return;
        }

        itemId = savedInstanceState.getInt(ITEM_ID);
        restoreGridPosition();
    }

    private void showMovies(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            itemId = savedInstanceState.getInt(ITEM_ID);
        }

        showMoviesBasedOnSelection(itemId);
    }

    private boolean showMoviesBasedOnSelection(int itemId) {
        MainDiscoveryActivity.itemId = itemId;

        switch (MainDiscoveryActivity.itemId) {
            case R.id.action_sort_by_popularity:
                sortByPopularity();
                return true;
            case R.id.action_sort_by_rating:
                sortByRating();
                return true;
            case R.id.action_show_favorites:
                showFavorites();
                return true;
            default:
                sortByPopularity();
                return true;

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int id = Integer.parseInt((String) v.getTag());

        Uri movieUri = itemId == R.id.action_show_favorites? MovieProvider.FAVORITE_CONTENT_URI : MovieProvider.MOVIE_CONTENT_URI;
        movieUri = movieUri.buildUpon().appendPath("" + id).build();
        Movie movieDetails = getMovieFrom(movieUri);
        if (movieDetails == null) return;

        startDetailsActivity(movieDetails);
    }

    @Nullable
    private Movie getMovieFrom(Uri movieUri) {
        Cursor movieCursor = getContentResolver().query(movieUri, null, null, null, null);

        if (movieCursor == null || movieCursor.getCount() == 0) {
            return null;
        }

        movieCursor.moveToFirst();
        Movie movieDetails = new Movie(movieCursor);
        movieCursor.close();
        return movieDetails;
    }

    private void startDetailsActivity(Movie movieDetails) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieConstant.MOVIE_DOMAIN, movieDetails);
        startActivity(intent);
    }

    private class MovieDownloadListener implements Loader.OnLoadCompleteListener<java.util.List<com.android.gowtham.popularmovies.dto.MovieDto>> {
        @Override
        public void onLoadComplete(Loader<List<MovieDto>> loader, List<MovieDto> data) {
            if (data == null) {
                tvUnableToFetchData.setVisibility(View.VISIBLE);
                return;
            }
            tvUnableToFetchData.setVisibility(View.GONE);

            ContentValues[] contentValues = new ContentValues[data.size()];
            int index = 0;
            for (MovieDto dto : data) {
                contentValues[index++] = new Movie(dto).getContentValue();
            }

            getContentResolver().bulkInsert(MovieProvider.MOVIE_CONTENT_URI, contentValues);
            Cursor query = getContentResolver().query(MovieProvider.MOVIE_CONTENT_URI, null, null, null, null);
            loadMovieToGridView(query);
            restoreGridPosition();
        }
    }

    private void restoreGridPosition() {
        if(savedInstanceState == null) {
            return;
        }

        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        movieThumbnails.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
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
        loadFavoriteMovieToGridView();
        restoreGridPosition();
    }

    private void loadFavoriteMovieToGridView() {

        Cursor query = getContentResolver().query(MovieProvider.FAVORITE_CONTENT_URI, null, null, null, null);
        loadMovieToGridView(query);
    }

    private void loadMovieToGridView(Cursor cursor) {
        movieThumbnails.setHasFixedSize(true);
        movieThumbnails.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));

        MoviesListAdapter adapter = new MoviesListAdapter(this, cursor, this);
        movieThumbnails.setAdapter(adapter);
        movieThumbnails.invalidate();
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) {
            return 2;
        }

        return nColumns;
    }
}
