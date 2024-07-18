package com.solucitation.midpoint_backend.domain.places;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaceController {

    private final MapService mapService;

    public PlaceController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/api/places")
    public String getPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "20000") int radius
    ) {
        return mapService.findPlaces(latitude, longitude, radius);
    }
}










