package com.solucitation.midpoint_backend.domain.FavFriend.service;

import com.solucitation.midpoint_backend.domain.FavFriend.entity.FavFriend;
import com.solucitation.midpoint_backend.domain.FavFriend.repository.FavoriteFriendRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavFriendService {
    private final FavoriteFriendRepository favoriteFriendRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FavFriend saveFavoriteFriend(String address, String name, Float latitude, Float longitude, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        favoriteFriendRepository.findByNameAndMemberId(name, member.getId())
                .ifPresent(favFriend -> {
                    throw new RuntimeException("이미 존재하는 친구입니다.");
                });

        FavFriend favFriend = FavFriend.builder()
                .member(member)
                .address(address)
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        return favoriteFriendRepository.save(favFriend);
    }

    @Transactional(readOnly = true)
    public FavFriend getFavoriteFriendByFavFriendId(Long favFriendId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        return favoriteFriendRepository.findByFavFriendIdAndMemberId(favFriendId, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 친구입니다."));
    }

    @Transactional
    public void deleteFavoriteFriendByName(Long favFriendId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavFriend favFriend = favoriteFriendRepository.findByFavFriendIdAndMemberId(favFriendId, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 친구입니다."));

        favoriteFriendRepository.delete(favFriend);
    }

    @Transactional(readOnly = true)
    public List<FavFriend> getFavoriteFriends(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        return favoriteFriendRepository.findByMemberId(member.getId());
    }

    @Transactional
    public FavFriend updateFavoriteFriend(Long favFriendId, String name, String address, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavFriend favFriend = favoriteFriendRepository.findByFavFriendIdAndMemberId(favFriendId, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 친구입니다."));

        if (address != null && !address.equals(favFriend.getAddress())) {
            favFriend.setAddress(address);
        } else if (address != null) {
            throw new IllegalArgumentException("새 주소가 기존 주소와 동일합니다.");
        }

        if (name != null && !name.equals(favFriend.getName())) {
            favoriteFriendRepository.findByNameAndMemberId(name, member.getId())
                    .ifPresent(existingFriend -> {
                        throw new RuntimeException("이미 존재하는 친구 이름입니다.");
                    });
            favFriend.setName(name);
        } else if (name != null) {
            throw new IllegalArgumentException("새 이름이 기존 이름과 동일합니다.");
        }

        return favoriteFriendRepository.save(favFriend);
    }
}