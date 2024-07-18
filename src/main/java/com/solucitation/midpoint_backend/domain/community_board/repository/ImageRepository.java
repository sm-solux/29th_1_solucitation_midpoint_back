package com.solucitation.midpoint_backend.domain.community_board.repository;

import com.solucitation.midpoint_backend.domain.community_board.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByMemberIdAndPostIsNull(Long memberId); // 회원의 프로필 이미지를 찾는 경우
}

