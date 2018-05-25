package com.android.gowtham.popularmovies.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.gowtham.popularmovies.db.MoviesDBContract;
import com.android.gowtham.popularmovies.db.MoviesDBHelper;
import com.android.gowtham.popularmovies.domain.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    MoviesDBHelper dbHelper;

    private static final String AUTHORITY = "com.android.gowtham.popularmovies";
    private static final String FAVORITE_BASE_PATH = MoviesDBContract.FAVORITE_TABLE_NAME;
    private static final String MOVIE_BASE_PATH = MoviesDBContract.TABLE_NAME;
    private static final String ERROR_BASE_PATH = "Error";

    public static final Uri FAVORITE_CONTENT_URI = Uri.parse("content://" + MovieProvider.AUTHORITY + "/" + MovieProvider.FAVORITE_BASE_PATH);
    public static final Uri MOVIE_CONTENT_URI = Uri.parse("content://" + MovieProvider.AUTHORITY + "/" + MovieProvider.MOVIE_BASE_PATH);
    public static final Uri ERROR_URI = Uri.parse("content://" + MovieProvider.AUTHORITY + "/" + MovieProvider.ERROR_BASE_PATH);

    static {
        uriMatcher.addURI(AUTHORITY, FAVORITE_BASE_PATH + "", 1);
        uriMatcher.addURI(AUTHORITY, FAVORITE_BASE_PATH + "/#", 2);
        uriMatcher.addURI(AUTHORITY, MOVIE_BASE_PATH + "", 3);
        uriMatcher.addURI(AUTHORITY, MOVIE_BASE_PATH + "/#", 4);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case 1:
                return dbHelper.getFavoriteMovies();
            case 2:
                return dbHelper.getFavoriteMovie(uri.getLastPathSegment());
            case 3:
                return dbHelper.getMovies();
            case 4:
                String lastPathSegment = uri.getLastPathSegment();
                return dbHelper.getMovie(lastPathSegment);
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        if (values == null) {
            return ERROR_URI;
        }

        switch (uriMatcher.match(uri)) {
            case 1:
                dbHelper.addFavoriteMovie(new Movie(values));
                break;
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        try {
            long movieId = Long.valueOf(uri.getLastPathSegment());
            dbHelper.removeFavoriteMovie(movieId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (uriMatcher.match(uri)) {
            case 3:
                List<Movie> movies = new ArrayList<>();
                for(ContentValues value : values) {
                    movies.add(new Movie(value));
                }

                dbHelper.addMovies(movies);
                break;
        }
        return super.bulkInsert(uri, values);
    }
}
