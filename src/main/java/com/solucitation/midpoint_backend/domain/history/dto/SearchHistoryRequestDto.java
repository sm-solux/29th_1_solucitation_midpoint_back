package com.solucitation.midpoint_backend.domain.history.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryRequestDto {
    @NotBlank(message = "장소ID가 누락되었습니다.")
    private String placeId;

    @NotBlank(message = "장소명이 누락되었습니다.")
    private String placeName;

    @NotBlank(message = "주소가 누락되었습니다.")
    private String placeAddress;

    private String imageUrl;
}
