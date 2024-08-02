package com.solucitation.midpoint_backend.domain.FavPlace.service;

import com.solucitation.midpoint_backend.domain.FavPlace.dto.FavPlaceResponse;
import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import com.solucitation.midpoint_backend.domain.FavPlace.repository.FavPlaceRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                    throw new IllegalArgumentException(addrType + " 장소는 이미 존재합니다.");
                });

        FavPlace favPlace = FavPlace.builder()
                .addr(addr)
                .latitude(latitude)
                .longitude(longitude)
                .addrType(addrType)
                .build();

        member.addFavPlace(favPlace);  // 양방향 연관 관계 설정
        return favPlaceRepository.save(favPlace);
    }

    @Transactional
    public void deleteFavoritePlace(FavPlace.AddrType addrType, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavPlace favPlace = favPlaceRepository.findByAddrTypeAndMemberId(addrType, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        if (!favPlace.getMember().equals(member)) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }
        member.removeFavPlace(favPlace);  // 양방향 연관 관계 해제
        favPlaceRepository.delete(favPlace);
    }

    @Transactional(readOnly = true)
    public FavPlace getFavoritePlaceDetails(FavPlace.AddrType addrType, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavPlace favPlace = favPlaceRepository.findByAddrTypeAndMemberId(addrType, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        if (!favPlace.getMember().equals(member)) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        return favPlace;
    }

    @Transactional(readOnly = true)
    public List<FavPlaceResponse> getAllFavoritePlaces(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        List<FavPlace> favPlaces = favPlaceRepository.findAllByMemberId(member.getId());

        Map<FavPlace.AddrType, FavPlaceResponse> responseMap = new HashMap<>();

        responseMap.put(FavPlace.AddrType.HOME, new FavPlaceResponse(null, null, FavPlace.AddrType.HOME.name()));
        responseMap.put(FavPlace.AddrType.WORK, new FavPlaceResponse(null, null, FavPlace.AddrType.WORK.name()));

        for (FavPlace favPlace : favPlaces) {
            FavPlace.AddrType addrType = favPlace.getAddrType();
            if (responseMap.containsKey(addrType)) {
                responseMap.put(addrType, new FavPlaceResponse(
                        favPlace.getFavPlaceId(),
                        favPlace.getAddr(),
                        addrType.name()
                ));
            }
        }

        List<FavPlaceResponse> responses = responseMap.values().stream()
                .collect(Collectors.toList());

        return responses;
    }

    @Transactional
    public FavPlace updateFavoritePlace(FavPlace.AddrType addrType, String addr, Float latitude, Float longitude, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavPlace favPlace = favPlaceRepository.findByAddrTypeAndMemberId(addrType, member.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        if (!favPlace.getMember().equals(member)) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        if (addr == null || addr.trim().isEmpty() || addr.length() > 255) {
            throw new IllegalArgumentException("주소는 최소 1글자 이상 최대 255글자 이하로 입력해야 합니다.");
        }

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("위도와 경도는 필수 입력 값입니다.");
        }

        favPlace.setAddr(addr);
        favPlace.setLatitude(latitude);
        favPlace.setLongitude(longitude);

        return favPlaceRepository.save(favPlace);
    }
}