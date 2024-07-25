package com.solucitation.midpoint_backend.domain.favorites;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GooglePlacesService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.api.key}")
    private String apiKey;

    public GooglePlacesService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public LocationDetails getLocationDetails(String placeName) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&inputtype=textquery&fields=geometry&key=%s",
                placeName,
                apiKey
        );

        // API 호출
        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new RuntimeException("Google Places API 응답이 없습니다.");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidatesNode = rootNode.path("candidates");

            if (candidatesNode.isEmpty()) {
                throw new RuntimeException("해당 장소를 찾을 수 없습니다.");
            }

            JsonNode locationNode = candidatesNode.get(0).path("geometry").path("location");
            double latitude = locationNode.path("lat").asDouble();
            double longitude = locationNode.path("lng").asDouble();

            return new LocationDetails(latitude, longitude);
        } catch (Exception e) {
            throw new RuntimeException("구글 Places API 응답 처리 중 오류가 발생했습니다.", e);
        }
    }
}
