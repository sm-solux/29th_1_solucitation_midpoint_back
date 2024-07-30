package com.solucitation.midpoint_backend.domain.FavFriend.repository;

import com.solucitation.midpoint_backend.domain.FavFriend.entity.FavFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteFriendRepository extends JpaRepository<FavFriend, Long> {
    Optional<FavFriend> findByNameAndMemberId(String name, Long memberId);
    List<FavFriend> findByMemberId(Long memberId);
    Optional<FavFriend> findByFavFriendIdAndMemberId(Long favFriendId, Long member_id);
}