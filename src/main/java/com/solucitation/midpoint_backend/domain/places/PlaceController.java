package com.solucitation.midpoint_backend.domain.places;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaceController {

    private final com.solucitation.midpoint_backend.domain.places.MapService mapService;

    public PlaceController(com.solucitation.midpoint_backend.domain.places.MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/api/places")
    public String getPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String category,
            @RequestParam(defaultValue = "20000") int radius
    ) {
        try {
            return mapService.findPlaces(latitude, longitude, radius, category);
        } catch (IllegalArgumentException e) {
            return "Invalid category: " + category;
        } catch (Exception e) {
            return "An error occurred while fetching places: " + e.getMessage();
        }
    }
}










