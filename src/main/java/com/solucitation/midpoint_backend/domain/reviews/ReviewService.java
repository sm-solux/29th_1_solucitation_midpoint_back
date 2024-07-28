package com.solucitation.midpoint_backend.domain.reviews;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ReviewService(@Qualifier("reviewsRestTemplate") RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> getReviewUrl(String placeId) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s", placeId, apiKey);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode result = jsonResponse.path("result");

            String placeUrl = result.path("url").asText(null);

            if (placeUrl == null || placeUrl.isEmpty()) {
                logger.error("No URL found for placeId: {}", placeId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid placeId: " + placeId);
            }

            return Map.of("url", placeUrl);
        } catch (Exception e) {
            logger.error("Error retrieving place details for placeId: {}", placeId, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid placeId: " + placeId);
        }
    }
}
