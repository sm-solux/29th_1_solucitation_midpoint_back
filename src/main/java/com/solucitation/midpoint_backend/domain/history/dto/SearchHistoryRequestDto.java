package com.solucitation.midpoint_backend.domain.history.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryRequestDto {
    @NotBlank(message = "장소ID가 올바르지 않습니다.")
    private String placeId;

    @NotBlank(message = "장소명이 올바르지 않습니다.")
    private String placeName;

    @NotBlank(message = "주소가 올바르지 않습니다.")
    private String placeAddress;

    private String imageUrl;
}
