package com.solucitation.midpoint_backend.domain.favorites.repository;

import com.solucitation.midpoint_backend.domain.favorites.entity.FavoriteFriend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteFriendRepository extends JpaRepository<FavoriteFriend, Long> {
}
