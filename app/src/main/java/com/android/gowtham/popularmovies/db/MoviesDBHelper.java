package com.android.gowtham.popularmovies.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.gowtham.popularmovies.domain.Movie;
import com.android.gowtham.popularmovies.dto.MovieDto;

import java.util.List;

public class MoviesDBHelper extends SQLiteOpenHelper {


    private SQLiteDatabase readableDatabase;

    public MoviesDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        readableDatabase = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesDBContract.TABLE_NAME + " ( "
                + MoviesDBContract._ID + " INTEGER PRIMARY KEY, "
                + MoviesDBContract.TITLE_COLUMN + " TEXT, "
                + MoviesDBContract.THUMBNAIL_URL_COLUMN + " TEXT, "
                + MoviesDBContract.SYNOPSIS_COLUMN + " TEXT, "
                + MoviesDBContract.RATING_COLUMN + " INTEGER, "
                + MoviesDBContract.DOR_COLUMN + " TEXT, "
                + MoviesDBContract.MOVIE_ID + " NUMBER"
                + ")";
        db.execSQL(CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesDBContract.TABLE_NAME);
        onCreate(db);
    }

    public void addMovies(List<Movie> movies) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("DELETE FROM " + MoviesDBContract.TABLE_NAME);

        insertValues(writableDatabase, movies);
    }

    public MovieDto getMovieDetails(int id) {
        if (readableDatabase == null) {
            readableDatabase = getReadableDatabase();
        }
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM " + MoviesDBContract.TABLE_NAME + " WHERE " + MoviesDBContract._ID + " = " + id, null);

        if (cursor.getCount() < 1) {
            return null;
        }
        cursor.moveToNext();
        long movieId = cursor.getLong(cursor.getColumnIndex(MoviesDBContract.MOVIE_ID));
        String title = cursor.getString(cursor.getColumnIndex(MoviesDBContract.TITLE_COLUMN));
        String rating = cursor.getString(cursor.getColumnIndex(MoviesDBContract.RATING_COLUMN));
        String synopsis = cursor.getString(cursor.getColumnIndex(MoviesDBContract.SYNOPSIS_COLUMN));
        String releaseDate = cursor.getString(cursor.getColumnIndex(MoviesDBContract.DOR_COLUMN));
        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MoviesDBContract.THUMBNAIL_URL_COLUMN));

        cursor.close();

        return new MovieDto(movieId, title, thumbnailUrl, synopsis, rating, releaseDate);
    }

    public Cursor getMovies() {
        return getMovies(MoviesDBContract._ID + " ASC");
    }

    private Cursor getMovies(String s) {
        if (readableDatabase == null) {
            readableDatabase = getReadableDatabase();
        }
        return readableDatabase.rawQuery("SELECT * FROM " + MoviesDBContract.TABLE_NAME + " ORDER BY " + s, null);
    }

    private void insertValues(SQLiteDatabase writableDatabase, List<Movie> movies) {
        Object[] PARAMS = new Object[7];
        int index = 0;
        String insert_query = "INSERT INTO " + MoviesDBContract.TABLE_NAME + "(" + MoviesDBContract._ID + ","
                + MoviesDBContract.MOVIE_ID + ","
                + MoviesDBContract.TITLE_COLUMN + ","
                + MoviesDBContract.THUMBNAIL_URL_COLUMN + ","
                + MoviesDBContract.SYNOPSIS_COLUMN + ","
                + MoviesDBContract.RATING_COLUMN + ","
                + MoviesDBContract.DOR_COLUMN + ") VALUES (?,?,?,?,?,?,?) ";

        for (Movie movie : movies) {
            PARAMS[0] = index++;
            PARAMS[1] = movie.getMovieId();
            PARAMS[2] = movie.getTitle();
            PARAMS[3] = movie.getImageUrl();
            PARAMS[4] = movie.getSynopsis();
            PARAMS[5] = movie.getVote();
            PARAMS[6] = movie.getReleaseDate();

            writableDatabase.execSQL(insert_query, PARAMS);
        }
    }


}
