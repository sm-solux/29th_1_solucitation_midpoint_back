package com.solucitation.midpoint_backend.domain.places;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class MapService {

    private static final Logger logger = LoggerFactory.getLogger(MapService.class);

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private static final Map<String, String> CATEGORY_TYPE_MAP = new HashMap<>();

    static {
        // 맛집
        CATEGORY_TYPE_MAP.put("맛집", "american_restaurant|barbecue_restaurant|brazilian_restaurant|breakfast_restaurant|chinese_restaurant|fast_food_restaurant|french_restaurant|greek_restaurant|hamburger_restaurant|indian_restaurant|indonesian_restaurant|italian_restaurant|japanese_restaurant|korean_restaurant|lebanese_restaurant|meal_delivery|meal_takeaway|mediterranean_restaurant|mexican_restaurant|middle_eastern_restaurant|pizza_restaurant|ramen_restaurant|restaurant|sandwich_shop|seafood_restaurant|spanish_restaurant|steak_house|sushi_restaurant|thai_restaurant|turkish_restaurant|vegan_restaurant|vegetarian_restaurant|vietnamese_restaurant");
        // 카페
        CATEGORY_TYPE_MAP.put("카페", "bakery|cafe|coffee_shop|brunch_restaurant|ice_cream_shop");
        // 산책
        CATEGORY_TYPE_MAP.put("산책", "park|hiking_area|national_park");
        // 등산
        CATEGORY_TYPE_MAP.put("등산", "park|hiking_area|national_park");
        // 공부
        CATEGORY_TYPE_MAP.put("공부", "library|book_store");
        // 문화생활
        CATEGORY_TYPE_MAP.put("문화생활", "auto_parts_store|bicycle_store|book_store|cell_phone_store|clothing_store|convenience_store|department_store|discount_store|electronics_store|furniture_store|gift_shop|grocery_store|hardware_store|home_goods_store|home_improvement_store|jewelry_store|liquor_store|market|pet_store|shoe_store|shopping_mall|sporting_goods_store|store|supermarket|wholesaler|art_gallery|performing_arts_theater|movie_rental|movie_theater|museum|historical_landmark|tourist_attraction|spa");
        // 핫플
        CATEGORY_TYPE_MAP.put("핫플", "casino|liquor_store|night_club|bar");
        // 친목
        CATEGORY_TYPE_MAP.put("친목", "amusement_center|amusement_park|aquarium|banquet_hall|bowling_alley|community_center|convention_center|cultural_center|dog_park|event_venue|zoo");
    }

    public MapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String findPlaces(double latitude, double longitude, int radius, String category) {
        String placeTypes = CATEGORY_TYPE_MAP.get(category);
        if (placeTypes == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%d&type=%s&key=%s",
                latitude, longitude, radius, placeTypes, apiKey
        );
        
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid category provided: {}", category, e);
            return "Invalid category: " + category;
        } catch (Exception e) {
            logger.error("An error occurred while fetching places", e);
            return "An error occurred while fetching places: " + e.getMessage();
        }
    }
}