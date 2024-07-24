package com.solucitation.midpoint_backend.domain.reviews;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public ReviewService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PlaceDetailsResponse getReviewsByPlaceId(String placeId) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=reviews,photos&key=%s&language=ko", placeId, apiKey);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        PlaceDetailsResponse placeDetailsResponse = new PlaceDetailsResponse();

        if (response != null && response.get("result") != null) {
            Map<String, Object> result = (Map<String, Object>) response.get("result");
            
            List<Map<String, Object>> reviews = (List<Map<String, Object>>) result.get("reviews");
            List<ReviewResponse> reviewResponses = reviews.stream()
                    .map(this::mapToReviewResponse)
                    .collect(Collectors.toList());
            placeDetailsResponse.setReviews(reviewResponses);

            List<String> photoUrls = getPhotoUrls(result);
            placeDetailsResponse.setPhotoUrls(photoUrls);
        }

        return placeDetailsResponse;
    }

    private List<String> getPhotoUrls(Map<String, Object> result) {
        if (result.get("photos") != null) {
            List<Map<String, Object>> photos = (List<Map<String, Object>>) result.get("photos");
            return photos.stream()
                    .map(photo -> {
                        String photoReference = (String) photo.get("photo_reference");
                        return String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s", photoReference, apiKey);
                    })
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private ReviewResponse mapToReviewResponse(Map<String, Object> review) {
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setAuthorName((String) review.get("author_name"));
        reviewResponse.setProfilePhotoUrl((String) review.get("profile_photo_url"));
        reviewResponse.setText((String) review.get("text"));
        reviewResponse.setRating(((Number) review.get("rating")).intValue());
        reviewResponse.setRelativeTimeDescription((String) review.get("relative_time_description"));
        return reviewResponse;
    }
}
