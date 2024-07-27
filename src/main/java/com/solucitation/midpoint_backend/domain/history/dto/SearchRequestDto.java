package com.solucitation.midpoint_backend.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.IllformedLocaleException;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    private List<String> urlList;

    public void validateSearchRequest() {
        if (urlList == null || urlList.isEmpty()) {
            throw new IllformedLocaleException("저장할 장소를 선택하지 않으셨습니다.");
        }

        for (String s : urlList) {
            if (s == null || s.isEmpty()) {
                throw new IllformedLocaleException("저장할 장소에 대한 정보가 존재하지 않습니다.");
            }
        }
    }
}
