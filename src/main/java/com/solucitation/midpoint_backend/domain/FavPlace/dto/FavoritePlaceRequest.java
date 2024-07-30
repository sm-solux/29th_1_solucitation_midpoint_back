package com.solucitation.midpoint_backend.domain.FavPlace.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoritePlaceRequest {

    @NotNull(message = "Address type is required")
    private AddrType addrType;

    @NotNull(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String addr;

    @NotNull(message = "Latitude is required")
    private Float latitude;

    @NotNull(message = "Longitude is required")
    private Float longitude;

    public enum AddrType {
        HOME, WORK
    }
}