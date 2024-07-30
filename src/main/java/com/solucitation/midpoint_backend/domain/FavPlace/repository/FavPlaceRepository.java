package com.solucitation.midpoint_backend.domain.FavPlace.repository;

import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavPlaceRepository extends JpaRepository<FavPlace, Long> {
    Optional<FavPlace> findByAddrAndMemberId(String addr, Long memberId);
}