package com.solucitation.midpoint_backend.domain.FavFriend.service;

import com.solucitation.midpoint_backend.domain.FavFriend.entity.FavFriend;
import com.solucitation.midpoint_backend.domain.FavFriend.repository.FavoriteFriendRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavFriendService {
    private final FavoriteFriendRepository favoriteFriendRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FavFriend saveFavoriteFriend(String address, String name, Float latitude, Float longitude) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 중복 확인 로직
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
    public FavFriend getFavoriteFriendByName(String name) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return favoriteFriendRepository.findByNameAndMemberId(name, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 친구입니다."));
    }

    @Transactional
    public void deleteFavoriteFriendByName(String name) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        FavFriend favFriend = favoriteFriendRepository.findByNameAndMemberId(name, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 친구입니다."));

        favoriteFriendRepository.delete(favFriend);
    }

    @Transactional(readOnly = true)
    public List<FavFriend> getFavoriteFriends() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return favoriteFriendRepository.findByMemberId(member.getId());
    }
}