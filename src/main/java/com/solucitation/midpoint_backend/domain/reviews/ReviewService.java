package com.solucitation.midpoint_backend.domain.reviews;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReviewService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getReviewUrl(String placeId) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s", placeId, apiKey);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode result = jsonResponse.path("result");

            String websiteUrl = result.path("website").asText(null);
            String placeUrl = result.path("url").asText(null);

            return (websiteUrl != null && !websiteUrl.isEmpty()) ? websiteUrl : placeUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving place details";
        }
    }
}
