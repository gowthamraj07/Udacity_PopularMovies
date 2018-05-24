package com.android.gowtham.popularmovies.dto;

public class TrailerDto {
    private final String trailerId;
    private final String key;

    public TrailerDto(String trailerId, String key) {
        this.trailerId = trailerId;
        this.key = key;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public String getKey() {
        return key;
    }
}
