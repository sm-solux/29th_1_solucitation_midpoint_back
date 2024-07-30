package com.solucitation.midpoint_backend.domain.FavFriend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavFriendRequest {

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Latitude is required")
    private Float latitude;

    @NotNull(message = "Longitude is required")
    private Float longitude;
}
