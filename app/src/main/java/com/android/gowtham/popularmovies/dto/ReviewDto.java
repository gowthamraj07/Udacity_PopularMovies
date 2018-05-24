package com.android.gowtham.popularmovies.dto;

public class ReviewDto {
    private final String author;
    private final String reviewContent;

    public ReviewDto(String author, String reviewContent) {
        this.author = author;
        this.reviewContent = reviewContent;
    }

    public String getAuthor() {
        return author;
    }

    public String getReviewContent() {
        return reviewContent;
    }
}
