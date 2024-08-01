package com.solucitation.midpoint_backend.domain.FavPlace.service;

import com.solucitation.midpoint_backend.domain.FavPlace.dto.FavPlaceResponse;
import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import com.solucitation.midpoint_backend.domain.FavPlace.repository.FavPlaceRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        if (!favPlace.getMember().equals(member)) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        favPlaceRepository.delete(favPlace);
    }

    @Transactional(readOnly = true)
    public FavPlace getFavoritePlaceDetails(Long favPlaceId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavPlace favPlace = favPlaceRepository.findById(favPlaceId)
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

        FavPlaceResponse homeResponse = new FavPlaceResponse("HOME");
        FavPlaceResponse workResponse = new FavPlaceResponse("WORK");

        List<FavPlace> favPlaces = favPlaceRepository.findAllByMemberId(member.getId());

        for (FavPlace favPlace : favPlaces) {
            if (favPlace.getAddrType() == FavPlace.AddrType.HOME) {
                homeResponse = new FavPlaceResponse(
                        favPlace.getFavPlaceId(),
                        favPlace.getAddr(),
                        favPlace.getAddrType().name()
                );
            } else if (favPlace.getAddrType() == FavPlace.AddrType.WORK) {
                workResponse = new FavPlaceResponse(
                        favPlace.getFavPlaceId(),
                        favPlace.getAddr(),
                        favPlace.getAddrType().name()
                );
            }
        }

        return List.of(homeResponse, workResponse);
    }

    @Transactional
    public FavPlace updateFavoritePlace(Long favPlaceId, String addr, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        FavPlace favPlace = favPlaceRepository.findById(favPlaceId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        if (!favPlace.getMember().equals(member)) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        if (addr == null || addr.trim().isEmpty()|| addr.length() >255) {
            throw new IllegalArgumentException("주소는 최소 1글자 이상 최대 255글자 이하로 입력해야 합니다.");
        }

        favPlace.setAddr(addr);
        return favPlaceRepository.save(favPlace);
    }
}