package com.solucitation.midpoint_backend.domain.community_board.service;

import com.solucitation.midpoint_backend.domain.community_board.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;

    @Transactional(readOnly = true)
    public int countLikesByPostId(Long postId) {
        return likesRepository.countByPostIdAndIsLiked(postId);
    }
}
