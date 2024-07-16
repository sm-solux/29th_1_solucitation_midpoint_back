package com.solucitation.midpoint_backend.domain.community_board.repository;

import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    //Post에 연결된 images 엔티티를 가져옵니다.
    @Query("SELECT p FROM Post p JOIN FETCH p.images WHERE p.id = :postId")
    Post findPostWithImagesById(@Param("postId") Long postId);

    // Post에 연결된 postHashtags 엔티티를 가져옵니다.
    @Query("SELECT p FROM Post p JOIN FETCH p.postHashtags WHERE p.id = :postId")
    Post findPostWithPostHashtagsById(@Param("postId") Long postId);

    // Post 엔티티와 각 게시글에 연결된 images와 postHashtags를 함께 가져옵니다.
    @Query("SELECT p FROM Post p ORDER BY p.createDate desc ")
    List<Post> findAllPostWithImagesAndPostHashtags();

    // 해시태그 리스트 원소 중 하나 이상을 포함하는 게시글을 게시일 내림차순으로 모두 가져옵니다.
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN p.postHashtags ph " +
            "JOIN ph.hashtag h " +
            "WHERE h.id IN :purpose " +
            "ORDER BY p.createDate DESC")
    List<Post> findAllPostByPurpose(@Param("purpose") List<Long> purpose);
}
