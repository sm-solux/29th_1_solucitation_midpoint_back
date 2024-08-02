package com.solucitation.midpoint_backend.domain.places;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MapService {

    private static final Logger logger = LoggerFactory.getLogger(MapService.class);

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> CATEGORY_TYPE_MAP = new HashMap<>();

    static {
        CATEGORY_TYPE_MAP.put("restaurant", "american_restaurant|barbecue_restaurant|brazilian_restaurant|breakfast_restaurant|chinese_restaurant|fast_food_restaurant|french_restaurant|greek_restaurant|hamburger_restaurant|indian_restaurant|indonesian_restaurant|italian_restaurant|japanese_restaurant|korean_restaurant|lebanese_restaurant|mediterranean_restaurant|mexican_restaurant|middle_eastern_restaurant|pizza_restaurant|ramen_restaurant|restaurant|sandwich_shop|seafood_restaurant|spanish_restaurant|steak_house|sushi_restaurant|thai_restaurant|turkish_restaurant|vegan_restaurant|vegetarian_restaurant|vietnamese_restaurant");
        CATEGORY_TYPE_MAP.put("cafe", "bakery|cafe|cafes|coffee_shop|coffee_shops|brunch_restaurant|ice_cream_shop");
        CATEGORY_TYPE_MAP.put("walk", "park|hiking_area|national_park");
        CATEGORY_TYPE_MAP.put("hiking", "park|hiking_area|national_park");
        CATEGORY_TYPE_MAP.put("study", "library|bookstore");
        CATEGORY_TYPE_MAP.put("culture", "clothing_stores|department_stores|department_store|discount_store|furniture_store|gift_shop|grocery_store|jewelry_stores|liquor_store|shoe_stores|shopping_mall|sporting_goods_store|store|art_gallery|performing_arts_theater|movie_theater|museum|historical_landmark|tourist_attraction|spa");
        CATEGORY_TYPE_MAP.put("hot-place", "night_club|bar");
        CATEGORY_TYPE_MAP.put("social", "amusement_center|amusement_park|aquarium|bowling_alley|cultural_center|dog_park|event_venue|zoo|athletic_field|fitness_center|gym|sports_club|sports_complex|stadium|swimming_pools");
    }

    public MapService(@Qualifier("placesRestTemplate") RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> findPlaces(double latitude, double longitude, int radius, String category) {
        String placeTypes = CATEGORY_TYPE_MAP.get(category);
        if (placeTypes == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%d&type=%s&key=%s",
                latitude, longitude, radius, placeTypes, apiKey
        );

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode resultsNode = rootNode.path("results");

            List<Map<String, Object>> places = new ArrayList<>();
            for (JsonNode resultNode : resultsNode) {
                Map<String, Object> place = new HashMap<>();
                place.put("name", resultNode.path("name").asText());
                place.put("address", resultNode.path("vicinity").asText());
                place.put("latitude", resultNode.path("geometry").path("location").path("lat").asDouble());
                place.put("longitude", resultNode.path("geometry").path("location").path("lng").asDouble());
                place.put("types", resultNode.path("types").toString());
                place.put("placeID", resultNode.path("place_id").asText());

                JsonNode photosNode = resultNode.path("photos");
                if (photosNode.isArray() && photosNode.size() > 0) {
                    place.put("photo", photosNode.get(0).path("photo_reference").asText());
                } else {
                    place.put("photo", null);
                }

                places.add(place);
            }

            return places;
        } catch (Exception e) {
            logger.error("An error occurred while fetching places: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while fetching places", e);
        }
    }

    public static boolean isValidCategory(String category) {
        return CATEGORY_TYPE_MAP.containsKey(category);
    }
}
