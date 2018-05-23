package com.android.gowtham.popularmovies.domain;

import com.android.gowtham.popularmovies.dto.MovieDto;

import java.io.Serializable;

public class Movie implements Serializable{
    private final MovieDto dto;

    public Movie(MovieDto dto) {
        this.dto = dto;
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
}
