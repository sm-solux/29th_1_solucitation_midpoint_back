package com.solucitation.midpoint_backend.domain.FavFriend.api;

import com.solucitation.midpoint_backend.domain.FavFriend.dto.FavFriendRequest;
import com.solucitation.midpoint_backend.domain.FavFriend.entity.FavFriend;
import com.solucitation.midpoint_backend.domain.FavFriend.service.FavFriendService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favs/friends")
@RequiredArgsConstructor
public class FavFriendController {
    private final FavFriendService favFriendService;

    @PostMapping("/save")
    public ResponseEntity<?> saveFavFriend(Authentication authentication, @Valid @RequestBody FavFriendRequest favFriendRequest, BindingResult result) {
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
            FavFriend savedFriend = favFriendService.saveFavoriteFriend(
                    favFriendRequest.getAddress(),
                    favFriendRequest.getName(),
                    favFriendRequest.getLatitude(),
                    favFriendRequest.getLongitude(),
                    email
            );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "즐겨찾는 친구 저장에 성공했습니다.", savedFriend.getFavFriendId()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "이미 존재하는 친구입니다."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "서버 오류가 발생했습니다. " + e.getMessage()));
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getFavFriendDetails(Authentication authentication, @RequestParam Long favFriendId) {
        String email = authentication.getName();
        try {
            FavFriend favFriend = favFriendService.getFavoriteFriendByFavFriendId(favFriendId, email);
            return ResponseEntity.ok(new FavFriendResponse(favFriend.getFavFriendId(), favFriend.getName(), favFriend.getAddress()));
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
    public ResponseEntity<?> deleteFavFriend(Authentication authentication, @RequestParam Long favFriendId) {
        String email = authentication.getName();
        try {
            favFriendService.deleteFavoriteFriendByName(favFriendId, email);
            return ResponseEntity.ok(new ApiResponse(true, "즐겨찾는 친구 삭제에 성공했습니다."));
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

    @GetMapping("/list")
    public ResponseEntity<?> getFavFriendsList(Authentication authentication) {
        String email = authentication.getName();
        try {
            List<FavFriend> favFriends = favFriendService.getFavoriteFriends(email);
            return ResponseEntity.ok(favFriends.stream()
                    .map(favFriend -> new FavFriendResponse(favFriend.getFavFriendId(), favFriend.getName(), favFriend.getAddress()))
                    .collect(Collectors.toList()));
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

    @Getter
    @Setter
    public static class FavFriendResponse {
        private Long favFriendId;
        private String name;
        private String address;

        public FavFriendResponse(Long favFriendId, String name, String address) {
            this.favFriendId = favFriendId;
            this.name = name;
            this.address = address;
        }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        private Long favFriendId;

        public ApiResponse(boolean success, String message, Long favFriendId) {
            this.success = success;
            this.message = message;
            this.favFriendId = favFriendId;
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

        public Long getFavFriendId() {
            return favFriendId;
        }

        public void setFavFriendId(Long favFriendId) {
            this.favFriendId = favFriendId;
        }
    }
}