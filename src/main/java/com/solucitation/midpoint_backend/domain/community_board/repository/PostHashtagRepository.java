package com.solucitation.midpoint_backend.domain.community_board.repository;

import com.solucitation.midpoint_backend.domain.community_board.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
