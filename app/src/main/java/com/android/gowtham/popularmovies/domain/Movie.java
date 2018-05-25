package com.android.gowtham.popularmovies.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.gowtham.popularmovies.db.MoviesDBContract;
import com.android.gowtham.popularmovies.dto.MovieDto;

public class Movie implements Parcelable{
    private final MovieDto dto;

    public Movie(MovieDto dto) {
        this.dto = dto;
    }

    public Movie(Cursor cursor) {

        long movieId = cursor.getLong(cursor.getColumnIndex(MoviesDBContract.MOVIE_ID));
        String title = cursor.getString(cursor.getColumnIndex(MoviesDBContract.TITLE_COLUMN));
        String rating = cursor.getString(cursor.getColumnIndex(MoviesDBContract.RATING_COLUMN));
        String synopsis = cursor.getString(cursor.getColumnIndex(MoviesDBContract.SYNOPSIS_COLUMN));
        String releaseDate = cursor.getString(cursor.getColumnIndex(MoviesDBContract.DOR_COLUMN));
        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MoviesDBContract.THUMBNAIL_URL_COLUMN));

        dto = new MovieDto(movieId, title, thumbnailUrl, synopsis, rating, releaseDate);
    }

    public Movie(ContentValues values) {
        long movieId = values.getAsLong(MoviesDBContract.MOVIE_ID);
        String title = values.getAsString(MoviesDBContract.TITLE_COLUMN);
        String posterPath = values.getAsString(MoviesDBContract.THUMBNAIL_URL_COLUMN);
        String overView = values.getAsString(MoviesDBContract.SYNOPSIS_COLUMN);
        String voteAverage = values.getAsString(MoviesDBContract.RATING_COLUMN);
        String releaseDate = values.getAsString(MoviesDBContract.DOR_COLUMN);

        dto = new MovieDto(movieId, title, posterPath, overView, voteAverage, releaseDate);
    }

    public Movie(Parcel source) {
        long movieId = source.readLong();
        String title = source.readString();
        String posterPath = source.readString();
        String overView = source.readString();
        String voteAverage = ""+ source.readFloat();
        String releaseDate = source.readString();

        dto = new MovieDto(movieId, title, posterPath, overView, voteAverage, releaseDate);
    }

    public long getMovieId() {
        return dto.getMovieId();
    }

    public String getTitle() {
        return dto.getTitle();
    }

    public String getImageUrl() {
        return dto.getPosterPath();
    }

    public String getSynopsis() {
        return dto.getOverview();
    }

    public Float getVote() {
        return Float.valueOf(dto.getVoteAverage());
    }

    public String getReleaseDate() {
        return dto.getReleaseDate();
    }

    public MovieDto getDto() {
        return dto;
    }

    public ContentValues getContentValue() {
        ContentValues contentValue = new ContentValues();

        contentValue.put(MoviesDBContract.MOVIE_ID, getMovieId());
        contentValue.put(MoviesDBContract.TITLE_COLUMN, getTitle());
        contentValue.put(MoviesDBContract.THUMBNAIL_URL_COLUMN, getImageUrl());
        contentValue.put(MoviesDBContract.SYNOPSIS_COLUMN, getSynopsis());
        contentValue.put(MoviesDBContract.RATING_COLUMN, getVote());
        contentValue.put(MoviesDBContract.DOR_COLUMN, getReleaseDate());

        return contentValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getMovieId());
        dest.writeString(getTitle());
        dest.writeString(getImageUrl());
        dest.writeString(getSynopsis());
        dest.writeFloat(getVote());
        dest.writeString(getReleaseDate());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
