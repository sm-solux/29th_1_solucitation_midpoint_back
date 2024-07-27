package com.solucitation.midpoint_backend.domain.favorites_friends.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendPlaceRequestDto {
    private String friendName;  // 사용자 정의 친구 이름
    private PlaceInfoDto placeInfo;
}
