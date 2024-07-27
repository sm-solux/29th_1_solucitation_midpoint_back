package com.solucitation.midpoint_backend.domain.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryRequestDto {
    @NotNull(message = "장소ID가 누락되었습니다.")
    @NotBlank(message = "장소ID가 올바르지 않습니다. 공백으로만 구성된 장소ID는 불가능합니다.")
    private String placeId;

    @NotNull(message = "장소명이 누락되었습니다.")
    @NotBlank(message = "장소명이 올바르지 않습니다. 공백으로만 구성된 장소명은 불가능합니다.")
    private String placeName;

    @NotNull(message = "주소가 누락되었습니다.")
    @NotBlank(message = "주소가 올바르지 않습니다. 공백으로만 구성된 주소는 불가능합니다.")
    private String placeAddress;
}
