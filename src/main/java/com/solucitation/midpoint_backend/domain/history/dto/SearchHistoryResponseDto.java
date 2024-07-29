package com.solucitation.midpoint_backend.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryResponseDto {
    private String neighborhood;
    private LocalDateTime serachTime;
    private List<PlaceDto> places = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceDto {
        private String placeId;        // 장소의 고유 ID
        private String placeName;      // 장소의 이름
        private String placeAddress; // 장소의 주소
        private String imageUrl; // 장소 이미지
    }
}
