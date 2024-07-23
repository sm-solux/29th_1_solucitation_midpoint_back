package com.solucitation.midpoint_backend.domain.favorites.repository;

import com.solucitation.midpoint_backend.domain.favorites.entity.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {
}
