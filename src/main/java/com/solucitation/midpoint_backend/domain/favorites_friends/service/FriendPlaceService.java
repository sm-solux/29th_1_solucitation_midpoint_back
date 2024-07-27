package com.solucitation.midpoint_backend.domain.favorites_friends.service;

import com.solucitation.midpoint_backend.domain.favorites_friends.dto.FriendPlaceRequestDto;
import com.solucitation.midpoint_backend.domain.favorites_friends.dto.FriendPlaceDetailDto;
import com.solucitation.midpoint_backend.domain.favorites_friends.dto.FriendPlaceResponseDto;
import com.solucitation.midpoint_backend.domain.favorites_friends.entity.FriendPlace;
import com.solucitation.midpoint_backend.domain.favorites_friends.repository.FriendPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendPlaceService {
    private final FriendPlaceRepository friendPlaceRepository;

    @Transactional
    public FriendPlaceResponseDto addOrUpdateFriendPlace(FriendPlaceRequestDto requestDto) {
        // 검색할 친구 이름과 장소 정보로 친구와 장소를 업데이트하거나 새로 생성
        Optional<FriendPlace> existingFriendPlace = friendPlaceRepository.findByFriendName(requestDto.getFriendName());

        FriendPlace friendPlace;
        if (existingFriendPlace.isPresent()) {
            // 이미 존재하는 친구와 장소 정보 업데이트
            friendPlace = existingFriendPlace.get();
            friendPlace.setPlaceTypes(requestDto.getPlaceInfo().getTypes());
            friendPlace.setAddress(requestDto.getPlaceInfo().getAddress());
            friendPlace.setLatitude(requestDto.getPlaceInfo().getLatitude());
            friendPlace.setLongitude(requestDto.getPlaceInfo().getLongitude());
            friendPlace.setName(requestDto.getPlaceInfo().getName());
            friendPlace.setPlaceID(requestDto.getPlaceInfo().getPlaceID());
        } else {
            // 새 친구와 장소 정보 추가
            friendPlace = new FriendPlace();
            friendPlace.setFriendName(requestDto.getFriendName());
            friendPlace.setPlaceTypes(requestDto.getPlaceInfo().getTypes());
            friendPlace.setAddress(requestDto.getPlaceInfo().getAddress());
            friendPlace.setLatitude(requestDto.getPlaceInfo().getLatitude());
            friendPlace.setLongitude(requestDto.getPlaceInfo().getLongitude());
            friendPlace.setName(requestDto.getPlaceInfo().getName());
            friendPlace.setPlaceID(requestDto.getPlaceInfo().getPlaceID());
        }

        friendPlaceRepository.save(friendPlace);

        return new FriendPlaceResponseDto(
                friendPlace.getId(),
                friendPlace.getFriendName(),
                friendPlace.getPlaceTypes(),
                friendPlace.getAddress(),
                friendPlace.getLatitude(),
                friendPlace.getLongitude(),
                friendPlace.getName(),
                friendPlace.getPlaceID()
        );
    }

    @Transactional(readOnly = true)
    public FriendPlaceDetailDto getFriendPlaceById(Long id) {
        FriendPlace friendPlace = friendPlaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("친구와 장소 정보를 찾을 수 없습니다."));

        return new FriendPlaceDetailDto(
                friendPlace.getId(),
                friendPlace.getFriendName(),
                friendPlace.getPlaceTypes(),
                friendPlace.getAddress(),
                friendPlace.getLatitude(),
                friendPlace.getLongitude(),
                friendPlace.getName(),
                friendPlace.getPlaceID()
        );
    }
}
