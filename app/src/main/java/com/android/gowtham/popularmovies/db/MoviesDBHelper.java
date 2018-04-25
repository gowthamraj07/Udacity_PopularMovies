package com.android.gowtham.popularmovies.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.gowtham.popularmovies.domain.Movie;
import com.android.gowtham.popularmovies.dto.MovieDto;

import java.util.List;

public class MoviesDBHelper extends SQLiteOpenHelper {


    static final String _ID = "_id";
    static final String THUMBNAIL_URL = "thumbnail_url";

    private static final String TABLE_NAME = "movies_table";
    private static final String TITLE = "title";
    private static final String SYNOPSIS = "synopsis";
    private static final String RATING = "rating";
    private static final String DOR = "release_date";

    private SQLiteDatabase readableDatabase;

    public MoviesDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        readableDatabase = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
                + _ID + " INTEGER PRIMARY KEY, "
                + TITLE + " TEXT, "
                + THUMBNAIL_URL + " TEXT, "
                + SYNOPSIS + " TEXT, "
                + RATING + " INTEGER, "
                + DOR + " TEXT"
                + ")";
        db.execSQL(CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addMovies(List<Movie> movies) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("DELETE FROM " + TABLE_NAME);

        insertValues(writableDatabase, movies);
    }

    public Cursor getMoviesSortByRating() {
        return getMovies(RATING + " DESC");
    }

    public Cursor getMoviesSortByPopularity() {
        return getMovies(_ID + " ASC");
    }

    public MovieDto getMovieDetails(int id) {
        if (readableDatabase == null) {
            readableDatabase = getReadableDatabase();
        }
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + _ID + " = " + id, null);

        if (cursor.getCount() < 1) {
            return null;
        }
        cursor.moveToNext();
        int _id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(_ID)));
        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        String rating = cursor.getString(cursor.getColumnIndex(RATING));
        String synopsis = cursor.getString(cursor.getColumnIndex(SYNOPSIS));
        String releaseDate = cursor.getString(cursor.getColumnIndex(DOR));
        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(THUMBNAIL_URL));

        cursor.close();

        return new MovieDto(title, thumbnailUrl, synopsis, rating, releaseDate, _id);
    }

    private Cursor getMovies(String s) {
        if (readableDatabase == null) {
            readableDatabase = getReadableDatabase();
        }
        return readableDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + s, null);
    }

    private void insertValues(SQLiteDatabase writableDatabase, List<Movie> movies) {
        Object[] PARAMS = new Object[6];
        int index = 0;
        String insert_query = "INSERT INTO " + TABLE_NAME + "(" + _ID + ","
                + TITLE + ","
                + THUMBNAIL_URL + ","
                + SYNOPSIS + ","
                + RATING + ","
                + DOR + ") VALUES (?,?,?,?,?,?) ";

        for (Movie movie : movies) {
            PARAMS[0] = index++;
            PARAMS[1] = movie.getTitle();
            PARAMS[2] = movie.getImageUrl();
            PARAMS[3] = movie.getSynopsis();
            PARAMS[4] = movie.getVote();
            PARAMS[5] = movie.getReleseDate();

            writableDatabase.execSQL(insert_query, PARAMS);
        }
    }
}
