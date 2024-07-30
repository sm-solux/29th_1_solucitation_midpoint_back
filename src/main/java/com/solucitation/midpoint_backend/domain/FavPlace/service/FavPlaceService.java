package com.solucitation.midpoint_backend.domain.FavPlace.service;

import com.solucitation.midpoint_backend.domain.FavPlace.api.FavPlaceResponse;
import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import com.solucitation.midpoint_backend.domain.FavPlace.repository.FavPlaceRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavPlaceService {
    private final FavPlaceRepository favPlaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FavPlace saveFavoritePlace(String addr, Float latitude, Float longitude, FavPlace.AddrType addrType, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        favPlaceRepository.findByAddrTypeAndMemberId(addrType, member.getId())
                .ifPresent(favPlace -> {
                    throw new RuntimeException(addrType + " 장소는 이미 존재합니다.");
                });

        FavPlace favPlace = FavPlace.builder()
                .member(member)
                .addr(addr)
                .latitude(latitude)
                .longitude(longitude)
                .addrType(addrType)
                .build();

        return favPlaceRepository.save(favPlace);
    }

    @Transactional
    public void deleteFavoritePlace(Long favPlaceId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavPlace favPlace = favPlaceRepository.findById(favPlaceId)
                .orElseThrow(() -> new IllegalArgumentException("장소가 존재하지 않습니다."));

        if (!favPlace.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("권한이 없습니다.");
        }

        favPlaceRepository.delete(favPlace);
    }

    @Transactional(readOnly = true)
    public FavPlace getFavoritePlaceDetails(Long favPlaceId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        return favPlaceRepository.findById(favPlaceId)
                .orElseThrow(() -> new IllegalArgumentException("장소가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<FavPlaceResponse> getAllFavoritePlaces(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        List<FavPlace> favPlaces = favPlaceRepository.findAllByMemberId(member.getId());

        Map<FavPlace.AddrType, String> addrTypeMap = new HashMap<>();
        addrTypeMap.put(FavPlace.AddrType.HOME, null);
        addrTypeMap.put(FavPlace.AddrType.WORK, null);

        for (FavPlace favPlace : favPlaces) {
            addrTypeMap.put(favPlace.getAddrType(), favPlace.getAddr());
        }

        return addrTypeMap.entrySet().stream()
                .map(entry -> new FavPlaceResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}