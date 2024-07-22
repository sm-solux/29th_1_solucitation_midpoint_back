package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReviewService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public ReviewService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getReviewsByPlaceId(String placeId) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=reviews&key=%s", placeId, apiKey);
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }
}

