package com.solucitation.midpoint_backend.domain.favorites.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteLocationResponseDto {
    private final String email;
    private final String category;
    private final String searchName;
    private final double latitude;
    private final double longitude;
    private final String googlePlaceName;
    private final String googlePlaceAddress;
}
