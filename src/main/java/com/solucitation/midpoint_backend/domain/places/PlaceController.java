package com.solucitation.midpoint_backend.domain.places;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PlaceController {

    private final MapService mapService;

    public PlaceController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/api/places")
    public List<Map<String, Object>> getPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String category,
            @RequestParam(defaultValue = "1000") int radius // 기본값은 1km로 설정
    ) {
        if (radius != 1000 && radius != 2000 && radius != 3000) {
            return List.of(Map.of("error", "Invalid radius: " + radius + ". Allowed values are 1000, 2000, 3000 meters."));
        }

        try {
            return mapService.findPlaces(latitude, longitude, radius, category);
        } catch (IllegalArgumentException e) {
            return List.of(Map.of("error", "Invalid category: " + category));
        } catch (Exception e) {
            return List.of(Map.of("error", "An error occurred while fetching places: " + e.getMessage()));
        }
    }
}