package com.solucitation.midpoint_backend.domain.favorites;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites/locations")
public class FavoritesLocationController {

    private final FavoritesLocationService favoritesLocationService;

    public FavoritesLocationController(FavoritesLocationService favoritesLocationService) {
        this.favoritesLocationService = favoritesLocationService;
    }

    @PostMapping
    public ResponseEntity<FavoriteLocationResponseDto> addFavoriteLocation(
            @RequestParam String email,
            @RequestParam String category,
            @RequestParam String placeName) {

        FavoriteLocationResponseDto response = favoritesLocationService.addFavoriteLocation(email, category, placeName);

        return ResponseEntity.ok(response);
    }
}

