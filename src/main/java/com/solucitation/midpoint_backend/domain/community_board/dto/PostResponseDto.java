package com.solucitation.midpoint_backend.domain.community_board.dto;

import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import com.solucitation.midpoint_backend.domain.community_board.entity.PostHashtag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String firstImageUrl;
    private String title;
    private List<Long> hashtags;
    private Boolean likes;

    public PostResponseDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.likes = false;

        if (!post.getImages().isEmpty()) {
            this.firstImageUrl = post.getImages().get(0).getImageUrl();
        }

        this.hashtags = post.getPostHashtags().stream()
                .map(PostHashtag::getId)
                .collect(Collectors.toList());
    }
}