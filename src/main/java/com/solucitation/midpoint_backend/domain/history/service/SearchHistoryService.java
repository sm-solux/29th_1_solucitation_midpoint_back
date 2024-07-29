package com.solucitation.midpoint_backend.domain.history.service;

import com.solucitation.midpoint_backend.domain.history.dto.SearchHistoryRequestDto;
import com.solucitation.midpoint_backend.domain.history.dto.SearchHistoryResponseDto;
import com.solucitation.midpoint_backend.domain.history.entity.PlaceInfo;
import com.solucitation.midpoint_backend.domain.history.entity.SearchHistory;
import com.solucitation.midpoint_backend.domain.history.repository.SearchHistoryRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SearchHistoryService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final String defaultImageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, "ap-northeast-2", "place_default_image.png");
    private final SearchHistoryRepository searchHistoryRepository;
    private final MemberService memberService;

    @Transactional
    public void save(String neighborhood, String memberEmail, List<SearchHistoryRequestDto> placeDtos) {
        Member member = memberService.getMemberByEmail(memberEmail);

        if (neighborhood == null) {
           throw new IllegalArgumentException(String.valueOf(Map.of("error", "EMPTY_FIELD", "message", "동 정보가 누락되었습니다.")));
        }

        SearchHistory searchHistory = SearchHistory.builder()
                .member(member)
                .neighborhood(neighborhood)
                .build();

        for (SearchHistoryRequestDto dto : placeDtos) {
            String imageUrl = dto.getImageUrl();
            if (imageUrl == null || imageUrl.trim().isEmpty()) { // 이미지 url이 없거나 공백
                imageUrl = defaultImageUrl;
            }
            PlaceInfo placeInfo = PlaceInfo.builder()
                    .name(dto.getPlaceName())
                    .placeId(dto.getPlaceId())
                    .address(dto.getPlaceAddress())
                    .imageUrl(imageUrl)
                    .searchHistory(searchHistory) // searchHistory 필드 설정
                    .build();
            searchHistory.getPlaceList().add(placeInfo);
        }
        searchHistoryRepository.save(searchHistory);
    }

    @Transactional(readOnly = true)
    public List<SearchHistoryResponseDto> getHistory(Member member) {
        List<SearchHistory> histories = searchHistoryRepository.findByMemberOrderBySearchDateDesc(member);

        return histories.stream()
                .map(history -> new SearchHistoryResponseDto(
                        history.getNeighborhood(),
                        history.getSearchDate(),
                        history.getPlaceList().stream()
                                .map(placeInfo -> new SearchHistoryResponseDto.PlaceDto(
                                        placeInfo.getPlaceId(),
                                        placeInfo.getName(),
                                        placeInfo.getAddress(),
                                        placeInfo.getImageUrl()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
