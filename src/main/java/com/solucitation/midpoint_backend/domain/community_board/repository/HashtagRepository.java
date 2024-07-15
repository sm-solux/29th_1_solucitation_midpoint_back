package com.solucitation.midpoint_backend.domain.community_board.repository;

import com.solucitation.midpoint_backend.domain.community_board.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}