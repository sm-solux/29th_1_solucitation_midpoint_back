package com.solucitation.midpoint_backend.domain.places;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MapService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public MapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String findPlaces(double latitude, double longitude, int radius) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%d&key=%s",
                latitude, longitude, radius, apiKey
        );

        return restTemplate.getForObject(url, String.class);
    }
}


