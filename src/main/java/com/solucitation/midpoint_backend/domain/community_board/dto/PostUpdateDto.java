package com.solucitation.midpoint_backend.domain.community_board.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateDto {
    @Size(max=100, message="제목은 최대 100자까지 가능합니다.")
    private String title;

    private String content;

    private List<Long> postHashtag;

    public void validate() {
        if (postHashtag != null)  {
            if (postHashtag.size() != 2 || postHashtag.get(0).equals(postHashtag.get(1))) {
                throw new IllegalArgumentException("서로 다른 두 개의 해시태그를 선택해야 합니다.");
            }
        }

        if (title != null) {
            String trimmedTitle = title.trim();
            if (trimmedTitle.isEmpty()) {
                throw new IllegalArgumentException("제목은 비워둘 수 없습니다.");
            }
            if (trimmedTitle.length() > 100) {
                throw new IllegalArgumentException("제목은 최대 100자까지 가능합니다.");
            }
        }

        if (content != null) {
            String trimmedContent = content.trim();
            if (trimmedContent.isEmpty()) {
                throw new IllegalArgumentException("본문은 비워둘 수 없습니다.");
            }
        }
    }
}