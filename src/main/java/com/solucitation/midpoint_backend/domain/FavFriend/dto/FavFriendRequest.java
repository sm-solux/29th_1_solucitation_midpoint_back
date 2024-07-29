package com.solucitation.midpoint_backend.domain.FavFriend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavFriendRequest {
    private String address;
    private String name;
    private Float latitude;
    private Float longitude;
}