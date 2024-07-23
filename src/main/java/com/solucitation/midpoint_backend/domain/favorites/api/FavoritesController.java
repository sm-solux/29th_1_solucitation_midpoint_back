package com.solucitation.midpoint_backend.domain.favorites.api;

import com.solucitation.midpoint_backend.domain.favorites.service.FavoritesService;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PostMapping("/places")
    public ResponseEntity<?> addFavoritePlace(@RequestParam Long userId,
                                              @RequestParam String placeName,
                                              @RequestParam String placeLocation) {
        try {
            favoritesService.addFavoritePlace(userId, placeName, placeLocation);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/friends")
    public ResponseEntity<?> addFavoriteFriend(@RequestParam Long userId,
                                               @RequestParam Long friendId) {
        try {
            favoritesService.addFavoriteFriend(userId, friendId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}



