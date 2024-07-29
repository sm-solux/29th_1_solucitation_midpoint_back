package com.solucitation.midpoint_backend.domain.FavFriend.api;

import com.solucitation.midpoint_backend.domain.FavFriend.dto.FavFriendRequest;
import com.solucitation.midpoint_backend.domain.FavFriend.entity.FavFriend;
import com.solucitation.midpoint_backend.domain.FavFriend.service.FavFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fav/friends")
@RequiredArgsConstructor
public class FavFriendController {
    private final FavFriendService favFriendService;

    @PostMapping("/save")
    public ResponseEntity<Long> saveFavFriend(@RequestBody FavFriendRequest favFriendRequest) {
        FavFriend savedFriend = favFriendService.saveFavoriteFriend(
                favFriendRequest.getAddress(),
                favFriendRequest.getName(),
                favFriendRequest.getLatitude(),
                favFriendRequest.getLongitude()
        );
        return new ResponseEntity<>(savedFriend.getFriendshipId(), HttpStatus.CREATED); // 201 Created와 저장된 ID 반환
    }

}
