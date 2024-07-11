package com.solucitation.midpoint_backend.domain.community_board.repository;

import com.solucitation.midpoint_backend.domain.community_board.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    // 게시글의 좋아요 개수를 반환하는 코드
    @Query("SELECT COUNT(i) FROM Likes i WHERE i.post.id = :postId AND i.isLike = true")
    int countByPostIdAndIsLiked(Long postId);

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 여부를 확인하는 메서드
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM Likes l WHERE l.post.id = :postId AND l.member.id = :memberId AND l.isLike = true")
    boolean isMemberLikesPost(@Param("postId") Long postId, @Param("memberId") Long memberId);
}