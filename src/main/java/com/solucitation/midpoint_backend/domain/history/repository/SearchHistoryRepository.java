package com.solucitation.midpoint_backend.domain.history.repository;

import com.solucitation.midpoint_backend.domain.history.entity.SearchHistory;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByMemberOrderBySearchDateDesc(Member member);
}