package com.solucitation.midpoint_backend.domain.FavPlace.api;

import com.solucitation.midpoint_backend.domain.FavPlace.dto.FavoritePlaceRequest;
import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import com.solucitation.midpoint_backend.domain.FavPlace.service.FavPlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favs/places")
@RequiredArgsConstructor
public class FavPlaceController {

    private final FavPlaceService favPlaceService;

    @PostMapping("/save")
    public ResponseEntity<?> addFavoritePlace(@Valid @RequestBody FavoritePlaceRequest request) {
        try {
            FavPlace favPlace = favPlaceService.saveFavoritePlace(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "즐겨찾는 장소 저장에 성공했습니다.", favPlace.getFavPlaceId()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "저장에 실패했습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "서버 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        private Long favPlaceId;

        public ApiResponse(boolean success, String message, Long favPlaceId) {
            this.success = success;
            this.message = message;
            this.favPlaceId = favPlaceId;
        }

        public ApiResponse(boolean success, String message) {
            this(success, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getFavPlaceId() {
            return favPlaceId;
        }

        public void setFavPlaceId(Long favPlaceId) {
            this.favPlaceId = favPlaceId;
        }
    }
}