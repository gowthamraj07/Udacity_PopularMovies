package com.android.gowtham.popularmovies.db;

import android.provider.BaseColumns;

public final class MoviesDBContract implements BaseColumns{

    private MoviesDBContract() {

    }

    public static final String DB_NAME = "movies_db";
    public static final int VERSION = 6;

    public static final String TABLE_NAME = "movies_table";
    public static final String FAVORITE_TABLE_NAME = "favorite_movies_table";

    public static final String THUMBNAIL_URL_COLUMN = "thumbnail_url";
    public static final String TITLE_COLUMN = "title";
    public static final String SYNOPSIS_COLUMN = "synopsis";
    public static final String RATING_COLUMN = "rating";
    public static final String DOR_COLUMN = "release_date";

    public static final String MOVIE_ID = "MOVIE_ID";
}
