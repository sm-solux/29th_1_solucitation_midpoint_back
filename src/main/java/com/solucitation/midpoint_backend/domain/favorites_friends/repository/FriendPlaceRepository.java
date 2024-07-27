package com.solucitation.midpoint_backend.domain.favorites_friends.repository;

import com.solucitation.midpoint_backend.domain.favorites_friends.entity.FriendPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendPlaceRepository extends JpaRepository<FriendPlace, Long> {
    Optional<FriendPlace> findByFriendName(String friendName);
}
