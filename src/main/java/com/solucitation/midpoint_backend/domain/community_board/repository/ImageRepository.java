package com.solucitation.midpoint_backend.domain.community_board.repository;

import com.solucitation.midpoint_backend.domain.community_board.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByMemberIdAndPostIsNull(Long memberId); // 회원의 프로필 이미지를 찾는 경우

    @Query("SELECT COUNT(i) FROM Image i WHERE i.imageUrl = :imageUrl AND i.post.id = :postId")
    Long countByImageUrlAndPostId(@Param("imageUrl") String imageUrl, @Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.imageUrl = :imageUrl AND i.post.id = :postId")
    void deleteImageByImageUrlAndPostId(@Param("imageUrl") String imageUrl, @Param("postId") Long postId);
}

