package com.solucitation.midpoint_backend.domain.favorites.service;

import com.solucitation.midpoint_backend.domain.favorites.entity.LocationDetails;
import com.solucitation.midpoint_backend.domain.favorites.dto.FavoriteLocationResponseDto;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoritesLocationService {

    private final MemberRepository memberRepository;
    private final GooglePlacesService googlePlacesService;

    public FavoritesLocationService(MemberRepository memberRepository, GooglePlacesService googlePlacesService) {
        this.memberRepository = memberRepository;
        this.googlePlacesService = googlePlacesService;
    }

    @Transactional
    public FavoriteLocationResponseDto addFavoriteLocation(String email, String category, String searchName) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocationDetails locationDetails = googlePlacesService.getLocationDetails(searchName);

        String favorite = String.format("%s (%s): %.6f, %.6f", category, locationDetails.getGooglePlaceName(), locationDetails.getLatitude(), locationDetails.getLongitude());
        member.getFavorites().add(favorite);
        memberRepository.save(member);

        return new FavoriteLocationResponseDto(
                email,
                category,
                searchName, // 사용자 입력한 장소 이름
                locationDetails.getLatitude(),
                locationDetails.getLongitude(),
                locationDetails.getGooglePlaceName() // 구글에서 가져온 장소 이름
        );
    }
}
