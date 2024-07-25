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
                "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&inputtype=textquery&fields=geometry,name&key=%s",
                placeName,
                apiKey
        );

        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new RuntimeException("Google Places API 응답이 없습니다.");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            String status = rootNode.path("status").asText();

            if ("ZERO_RESULTS".equals(status)) {
                throw new RuntimeException("존재하지 않는 장소입니다. 정확한 장소를 입력해주세요.");
            }

            JsonNode candidatesNode = rootNode.path("candidates");
            if (candidatesNode.isEmpty()) {
                throw new RuntimeException("존재하지 않는 장소입니다. 정확한 장소를 입력해주세요.");
            }

            JsonNode candidateNode = candidatesNode.get(0);
            JsonNode locationNode = candidateNode.path("geometry").path("location");
            double latitude = locationNode.path("lat").asDouble();
            double longitude = locationNode.path("lng").asDouble();
            String googlePlaceName = candidateNode.path("name").asText();

            return new LocationDetails(latitude, longitude, googlePlaceName);
        } catch (Exception e) {
            throw new RuntimeException("구글 Places API 응답 처리 중 오류가 발생했습니다.", e);
        }
    }
}
