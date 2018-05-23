package com.android.gowtham.popularmovies.db;

import android.provider.BaseColumns;

public final class MoviesDBContract implements BaseColumns{

    private MoviesDBContract() {

    }

    public static final String DB_NAME = "movies_db";
    public static final int VERSION = 5;

    static final String TABLE_NAME = "movies_table";

    static final String THUMBNAIL_URL_COLUMN = "thumbnail_url";
    static final String TITLE_COLUMN = "title";
    static final String SYNOPSIS_COLUMN = "synopsis";
    static final String RATING_COLUMN = "rating";
    static final String DOR_COLUMN = "release_date";

    static final String MOVIE_ID = "MOVIE_ID";
}
