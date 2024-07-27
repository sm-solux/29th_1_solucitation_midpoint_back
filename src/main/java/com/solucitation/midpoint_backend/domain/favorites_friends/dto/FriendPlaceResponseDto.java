package com.solucitation.midpoint_backend.domain.favorites_friends.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendPlaceResponseDto {
    private Long id;
    private String friendName;
    private String placeTypes;
    private String address;
    private Double latitude;
    private Double longitude;
    private String name;
    private String placeID;
}
