package com.solucitation.midpoint_backend.domain.favorites_friends.api;

import com.solucitation.midpoint_backend.domain.favorites_friends.dto.FriendPlaceRequestDto;
import com.solucitation.midpoint_backend.domain.favorites_friends.dto.FriendPlaceResponseDto;
import com.solucitation.midpoint_backend.domain.favorites_friends.dto.FriendPlaceDetailDto;
import com.solucitation.midpoint_backend.domain.favorites_friends.service.FriendPlaceService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites_friends")
public class FriendPlaceController {

    private final FriendPlaceService friendPlaceService;
    private final Validator validator;

    /**
     * 친구와 장소를 추가하거나 업데이트합니다.
     *
     * @param requestDto 친구와 장소 정보가 담긴 요청 데이터
     * @return 성공 시 201 CREATED와 함께 추가된 친구와 장소 정보를 반환합니다.
     *         요청 데이터 검증 실패 시 400 BAD REQUEST를 반환합니다.
     *         기타 오류 발생 시 500 INTERNAL SERVER ERROR를 반환합니다.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addOrUpdateFriendPlace(@RequestBody FriendPlaceRequestDto requestDto) {
        try {
            Set<ConstraintViolation<FriendPlaceRequestDto>> violations = validator.validate(requestDto);
            if (!violations.isEmpty()) {
                List<String> errors = violations.stream()
                        .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            FriendPlaceResponseDto responseDto = friendPlaceService.addOrUpdateFriendPlace(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("친구 추가/업데이트 중 오류가 발생하였습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 친구와 장소의 상세 정보를 조회합니다.
     *
     * @param id 친구와 장소의 ID
     * @return 성공 시 200 OK와 함께 친구와 장소의 상세 정보를 반환합니다.
     *         해당 ID의 친구와 장소가 존재하지 않을 경우 404 NOT FOUND를 반환합니다.
     *         기타 오류 발생 시 500 INTERNAL SERVER ERROR를 반환합니다.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getFriendPlaceDetails(@PathVariable Long id) {
        try {
            FriendPlaceDetailDto detailDto = friendPlaceService.getFriendPlaceById(id);
            return ResponseEntity.ok(detailDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상세 정보 조회 중 오류가 발생하였습니다: " + e.getMessage());
        }
    }
}