package com.jayfella.website.http.request.review;

import com.jayfella.website.http.request.SimplePageRequest;

public class ReviewRequest extends SimplePageRequest {

    private String reviewContent;
    private int rating;

    public String getReviewContent() { return reviewContent; }
    public void setReviewContent(String reviewContent) { this.reviewContent = reviewContent; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

}
