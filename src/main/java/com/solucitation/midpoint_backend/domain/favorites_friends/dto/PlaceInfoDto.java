package com.solucitation.midpoint_backend.domain.favorites_friends.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInfoDto {
    private String types;
    private String address;
    private Double latitude;
    private Double longitude;
    private String name;
    private String placeID;
}
