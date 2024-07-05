package com.solucitation.midpoint_backend.domain.community_board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String firstImageUrl;
    private String title;
    private List<Long> hashtags;
    private int like;
}