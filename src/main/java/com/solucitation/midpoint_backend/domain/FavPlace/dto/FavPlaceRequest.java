package com.solucitation.midpoint_backend.domain.FavPlace.dto;

import com.solucitation.midpoint_backend.domain.FavPlace.validation.ValidAddrType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavPlaceRequest {
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String addr;

    @NotNull(message = "Latitude is required")
    private Float latitude;

    @NotNull(message = "Longitude is required")
    private Float longitude;

    @NotNull(message = "Address type is required")
    @ValidAddrType
    private String addrType;
}