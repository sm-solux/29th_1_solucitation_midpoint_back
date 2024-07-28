package com.solucitation.midpoint_backend.domain.favorites_friends.repository;

import com.solucitation.midpoint_backend.domain.favorites_friends.entity.FavoriteFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteFriendRepository extends JpaRepository<FavoriteFriend, Long> {
}