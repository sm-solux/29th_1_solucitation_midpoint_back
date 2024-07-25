package com.solucitation.midpoint_backend.domain.favorites;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteLocationResponseDto {
    private final String email;
    private final String category;
    private final String placeName;
    private final double latitude;
    private final double longitude;
}
