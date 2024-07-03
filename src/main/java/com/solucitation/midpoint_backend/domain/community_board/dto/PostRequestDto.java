package com.solucitation.midpoint_backend.domain.community_board.dto;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private Long post_id;
    private Member member;

    private String title;
    private String content;

    private LocalDateTime update_date;
    private LocalDateTime create_date;

    private List<Long> postHashtag;
    private List<String> images;

    public void validateImages() {
        if (images == null || images.isEmpty() || images.size() > 3) {
            throw new IllegalArgumentException("이미지는 최소 1개 이상, 최대 3개까지 가능합니다.");
        }
    }

    public void validatePostHashtags() {
        if (postHashtag == null || postHashtag.size() != 2 || postHashtag.get(0).equals(postHashtag.get(1))) {
            throw new IllegalArgumentException("서로 다른 두 개의 해시태그를 선택해야 합니다.");
        }
    }
}