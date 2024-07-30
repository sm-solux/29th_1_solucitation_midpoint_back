package com.solucitation.midpoint_backend.domain.FavFriend.repository;

import com.solucitation.midpoint_backend.domain.FavFriend.entity.FavFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteFriendRepository extends JpaRepository<FavFriend, Long> {
}
