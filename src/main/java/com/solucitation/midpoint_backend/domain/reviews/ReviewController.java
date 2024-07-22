package com.solucitation.midpoint_backend.domain.reviews;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/reviews")
    public List<ReviewResponse> getReviews(@RequestParam String placeId) {
        return reviewService.getReviewsByPlaceId(placeId);
    }
}



