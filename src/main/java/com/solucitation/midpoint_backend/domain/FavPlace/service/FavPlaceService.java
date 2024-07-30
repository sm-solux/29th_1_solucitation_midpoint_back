package com.solucitation.midpoint_backend.domain.FavPlace.service;

import com.solucitation.midpoint_backend.domain.FavPlace.dto.FavoritePlaceRequest;
import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import com.solucitation.midpoint_backend.domain.FavPlace.repository.FavPlaceRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavPlaceService {

    private final FavPlaceRepository favPlaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FavPlace saveFavoritePlace(FavoritePlaceRequest request) {
        Member member = getAuthenticatedMember();

        FavPlace favPlace = FavPlace.builder()
                .addr(request.getAddr())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .member(member)
                .addrType(convertAddrType(request.getAddrType()))
                .build();

        return favPlaceRepository.save(favPlace);
    }

    private Member getAuthenticatedMember() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
        return memberRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("Member not found"));
    }

    private FavPlace.AddrType convertAddrType(FavoritePlaceRequest.AddrType requestAddrType) {
        // Address type conversion
        return switch (requestAddrType) {
            case HOME -> FavPlace.AddrType.HOME;
            case WORK -> FavPlace.AddrType.WORK;
        };
    }
}