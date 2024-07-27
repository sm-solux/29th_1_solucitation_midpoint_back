package com.solucitation.midpoint_backend.domain.places;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String category,
            @RequestParam(defaultValue = "1000") int radius // 기본값은 1km로 설정
    ) {
        // 반경이 유효하지 않으면 400 Bad Request 응답
        if (radius != 1000 && radius != 2000 && radius != 3000) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid radius: " + radius));
        }

        // 카테고리가 유효하지 않으면 400 Bad Request 응답
        if (!MapService.isValidCategory(category)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid category: " + category));
        }

        try {
            List<Map<String, Object>> places = mapService.findPlaces(latitude, longitude, radius, category);
            return ResponseEntity.ok(places);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while fetching places: " + e.getMessage()));
        }
    }
}