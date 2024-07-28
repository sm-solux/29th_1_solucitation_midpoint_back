package com.solucitation.midpoint_backend.domain.history.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostResponseDto;
import com.solucitation.midpoint_backend.domain.history.dto.SearchHistoryRequestDto;
import com.solucitation.midpoint_backend.domain.history.dto.SearchHistoryResponseDto;
import com.solucitation.midpoint_backend.domain.history.service.SearchHistoryService;
import com.solucitation.midpoint_backend.domain.member.dto.ValidationErrorResponse;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search-history")
public class SearchHistoryController {
    private final MemberService memberService;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final SearchHistoryService searchHistoryService;

    /**
     * 검색 결과에서 저장하고 싶은 장소를 리스트로 받아와서 저장합니다.
     * 
     * @param authentication 인증 정보
     * @param neighborhood 동 정보
     * @param historyDtoJson 장소 정보
     * @return 장소를 성공적으로 저장하면 201 CREATED를 반환합니다.
     *         로그인을 하지 않고 시도 시 401 UNAUTHORIZED를 반환합니다.
     *         사용자를 찾을 수 없는 경우 404 NOT_FOUND를 반환합니다.
     *         저장하려는 장소 정보에 문제가 있을 때 (필드가 공백이거나 null일 경우) 400 BAD_REQUEST를 반환합니다.
     *         기타 사유로 저장 실패 시 500 INTERNAL_SERVER_ERROR를 반환합니다.
     * @throws JsonProcessingException
     */
    @PostMapping("")
    public ResponseEntity<Object> saveHistory(Authentication authentication,
                                              @RequestPart("neighborhood") String neighborhood,
                                              @RequestPart("historyDto") String historyDtoJson) throws JsonProcessingException {
        try {
            List<SearchHistoryRequestDto> placeList = objectMapper.readValue(historyDtoJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SearchHistoryRequestDto.class));

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "해당 서비스를 이용하기 위해서는 로그인이 필요합니다."));
            }

            String memberEmail = authentication.getName();
            Member member = memberService.getMemberByEmail(memberEmail);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "USER_NOT_FOUND", "message", "사용자를 찾을 수 없습니다."));
            }

            for (SearchHistoryRequestDto place : placeList) { // 리스트 내의 모든 요소에 대한 검증 수행
                Set<ConstraintViolation<SearchHistoryRequestDto>> violations = validator.validate(place);
                if (!violations.isEmpty()) {
                    List<ValidationErrorResponse.FieldError> fieldErrors = violations.stream()
                            .map(violation -> new ValidationErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                            .collect(Collectors.toList());
                    ValidationErrorResponse errorResponse = new ValidationErrorResponse(fieldErrors);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            }
            searchHistoryService.save(neighborhood, memberEmail, placeList);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "장소를 저장하였습니다."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "USER_NOT_FOUND", "message", "사용자를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage(), "message", "장소 저장 중 오류가 발생하였습니다."));
        }
    }

    /**
     * 사용자가 지금까지 저장한 검색 정보를 전부 최신순부터 가져옵니다.
     *
     * @param authentication 인증 정보
     * @return 검색 기록 조회가 성공하면 200 OK와 결과 리스트를 반환합니다.
     *         로그인을 하지 않고 시도 시 401 UNAUTHORIZED를 반환합니다.
     *         사용자를 찾을 수 없는 경우 404 NOT_FOUND를 반환합니다.
     *         기타 사유로 저장 실패 시 500 INTERNAL_SERVER_ERROR를 반환합니다.
     */
    @GetMapping("")
    public ResponseEntity<Object> getHistory(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "해당 서비스를 이용하기 위해서는 로그인이 필요합니다."));
            }

            String memberEmail = authentication.getName();
            Member member = memberService.getMemberByEmail(memberEmail);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "USER_NOT_FOUND", "message", "사용자를 찾을 수 없습니다."));
            }

            List<SearchHistoryResponseDto> searchHistoryResponseDtos = searchHistoryService.getHistory(member);
            return ResponseEntity.ok(searchHistoryResponseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage(), "message", "검색 기록 조회 중 오류가 발생하였습니다."));
        }
    }

}
