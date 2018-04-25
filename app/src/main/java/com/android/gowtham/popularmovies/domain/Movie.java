package com.android.gowtham.popularmovies.domain;

import com.android.gowtham.popularmovies.dto.MovieDto;

public class Movie {
    private MovieDto dto;

    public Movie(MovieDto dto) {
        this.dto = dto;
    }

    public String getId() {
        return String.valueOf(dto.getId());
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

    public String getReleseDate() {
        return dto.getReleaseDate();
    }
}
