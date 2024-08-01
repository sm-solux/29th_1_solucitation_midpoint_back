package com.solucitation.midpoint_backend.domain.FavPlace.repository;

import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavPlaceRepository extends JpaRepository<FavPlace, Long> {
    Optional<FavPlace> findByAddrTypeAndMemberId(FavPlace.AddrType addrType, Long memberId);
    List<FavPlace> findAllByMemberId(Long memberId);
}
