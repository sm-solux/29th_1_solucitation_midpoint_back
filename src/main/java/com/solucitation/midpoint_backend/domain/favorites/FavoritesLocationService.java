package com.solucitation.midpoint_backend.domain.favorites;

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
    public FavoriteLocationResponseDto addFavoriteLocation(String email, String category, String placeName) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocationDetails locationDetails = googlePlacesService.getLocationDetails(placeName);

        String favorite = String.format("%s (%s): %.6f, %.6f", category, placeName, locationDetails.getLatitude(), locationDetails.getLongitude());
        member.getFavorites().add(favorite);
        memberRepository.save(member);

        return new FavoriteLocationResponseDto(email, category, placeName, locationDetails.getLatitude(), locationDetails.getLongitude());
    }
}
