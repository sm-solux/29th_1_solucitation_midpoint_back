package com.solucitation.midpoint_backend.domain.reviews;

import java.util.List;

public class PlaceDetailsResponse {
    private List<ReviewResponse> reviews;
    private List<String> photoUrls;

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }
}