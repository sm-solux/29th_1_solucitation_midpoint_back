package com.solucitation.midpoint_backend.domain.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    @NotNull @NotBlank
    private String placeName;

    @NotNull @NotBlank
    private String placeAddress;
}
