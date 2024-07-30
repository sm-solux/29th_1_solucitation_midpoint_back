package com.solucitation.midpoint_backend.domain.FavPlace.api;

import com.solucitation.midpoint_backend.domain.FavPlace.dto.FavPlaceRequest;
import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import com.solucitation.midpoint_backend.domain.FavPlace.service.FavPlaceService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favs/places")
@RequiredArgsConstructor
public class FavPlaceController {
    private final FavPlaceService favPlaceService;

    @PostMapping("/save")
    public ResponseEntity<?> saveFavPlace(Authentication authentication, @Valid @RequestBody FavPlaceRequest favPlaceRequest, BindingResult result) {
        String email = authentication.getName();
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "입력 값이 잘못되었습니다: " + errorMessage));
        }

        try {
            FavPlace savedPlace = favPlaceService.saveFavoritePlace(
                    favPlaceRequest.getAddr(),
                    favPlaceRequest.getLatitude(),
                    favPlaceRequest.getLongitude(),
                    FavPlace.AddrType.valueOf(favPlaceRequest.getAddrType().toUpperCase()),
                    email
            );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "즐겨찾는 장소 저장에 성공했습니다.", savedPlace.getFavPlaceId()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "서버 오류가 발생했습니다. " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFavPlace(Authentication authentication, @RequestParam Long favPlaceId) {
        String email = authentication.getName();
        try {
            favPlaceService.deleteFavoritePlace(favPlaceId, email);
            return ResponseEntity.ok(new ApiResponse(true, "즐겨찾는 장소 삭제에 성공했습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "존재하지 않는 장소입니다."));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "권한이 없습니다."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "서버 오류가 발생했습니다. "));
        }
    }

    @Getter
    @Setter
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
    }
}
