package com.solucitation.midpoint_backend.domain.history.repository;

import com.solucitation.midpoint_backend.domain.history.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
}