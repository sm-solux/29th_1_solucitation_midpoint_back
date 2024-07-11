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

//
//    // query 키워드가 들어간 Post 리스트를 반환합니다.
//    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(concat('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(concat('%', :keyword, '%')) ORDER BY p.createDate DESC")
//    List<Post> findAllPostByQuery(@Param("query") String query);
//
//    // 해시태그가 모두 포함된 Post 리스트를 반환합니다.
//    @Query("SELECT DISTINCT p FROM Post p JOIN p.postHashtags ph WHERE ph.id IN :purposes GROUP BY p HAVING COUNT(DISTINCT ph) = :count")
//    List<Post> findAllPostByPurpose(@Param("purposes") List<Long> purposes, @Param("count") int count);
//
//    // query와 해시태그를 모두 충족하는 Post 리스트를 반환합니다.
//    @Query("SELECT DISTINCT p FROM Post p " +
//            "JOIN p.postHashtags ph " +
//            "WHERE (LOWER(p.title) LIKE LOWER(concat('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(concat('%', :keyword, '%'))) " +
//            "AND ph.id IN :purposes " +
//            "GROUP BY p " +
//            "HAVING COUNT(DISTINCT ph) = :count " +
//            "ORDER BY p.createDate DESC")
//    List<Post> findAllPostByQueryAndPurpose(@Param("query") String query,
//                                            @Param("purposes") List<Long> purposes,
//                                            @Param("count") int count);

}
