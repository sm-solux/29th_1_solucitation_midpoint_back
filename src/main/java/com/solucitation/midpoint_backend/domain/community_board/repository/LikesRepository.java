package com.solucitation.midpoint_backend.domain.community_board.repository;

import com.solucitation.midpoint_backend.domain.community_board.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    // 게시글의 좋아요 개수를 반환
    @Query("SELECT COUNT(i) FROM Likes i WHERE i.post.id = :postId AND i.isLike = true")
    int countByPostIdAndIsLiked(Long postId);

    // 해당 멈베가 해당 게시글에 대해 좋아요를 눌렀는지를 반환
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM Likes l WHERE l.post.id = :postId AND l.member.email= :email AND l.isLike = true")
    boolean isMemberLikesPostByEmail(@Param("postId") Long postId, @Param("email") String email);

    @Transactional // 좋아요 취소
    @Modifying
    @Query("DELETE FROM Likes l WHERE l.member.email = :memberEmail AND l.post.id = :postId")
    void deleteByMemberEmailAndPostId(String memberEmail, Long postId);

    void deleteByPostId(Long postId);
}

