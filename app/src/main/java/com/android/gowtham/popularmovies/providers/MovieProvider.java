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
import com.android.gowtham.popularmovies.dto.MovieDto;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    MoviesDBHelper dbHelper;

    public static final String AUTHORITY = "com.android.gowtham.popularmovies";
    public static final String BASE_PATH = MoviesDBContract.FAVORITE_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + MovieProvider.AUTHORITY+"/"+MovieProvider.BASE_PATH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "", 1);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", 2);
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
                String lastPathSegment = uri.getLastPathSegment();
                return dbHelper.getFavoriteMovie(lastPathSegment);
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
        long movieId = values.getAsLong(MoviesDBContract.MOVIE_ID);
        String title = values.getAsString(MoviesDBContract.TITLE_COLUMN);
        String posterPath = values.getAsString(MoviesDBContract.THUMBNAIL_URL_COLUMN);
        String overView = values.getAsString(MoviesDBContract.SYNOPSIS_COLUMN);
        String voteAverage = values.getAsString(MoviesDBContract.RATING_COLUMN);
        String releaseDate = values.getAsString(MoviesDBContract.DOR_COLUMN);

        Movie movie = new Movie(new MovieDto(movieId, title, posterPath, overView, voteAverage, releaseDate));

        dbHelper.addFavoriteMovie(movie);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        try {
            long movieId = Long.valueOf(uri.getLastPathSegment());

            Movie movie = new Movie(new MovieDto(movieId, null, null, null, null, null));

            dbHelper.removeFavoriteMovie(movie);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
