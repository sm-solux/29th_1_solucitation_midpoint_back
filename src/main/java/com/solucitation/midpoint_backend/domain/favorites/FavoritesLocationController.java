package com.solucitation.midpoint_backend.domain.favorites;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites/places")
public class FavoritesLocationController {

    private final FavoritesLocationService favoritesLocationService;

    public FavoritesLocationController(FavoritesLocationService favoritesLocationService) {
        this.favoritesLocationService = favoritesLocationService;
    }

    @PostMapping
    public ResponseEntity<?> addFavoriteLocation(
            @RequestParam String email,
            @RequestParam String category,
            @RequestParam String placeName) {

        if (!"집".equals(category) && !"직장/학교".equals(category)) {
            return ResponseEntity.badRequest().body("유효하지 않은 카테고리입니다.");
        }

        try {
            FavoriteLocationResponseDto response = favoritesLocationService.addFavoriteLocation(email, category, placeName);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
