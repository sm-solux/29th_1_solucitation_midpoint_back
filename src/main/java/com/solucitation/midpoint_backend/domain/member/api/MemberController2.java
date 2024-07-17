package com.solucitation.midpoint_backend.domain.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solucitation.midpoint_backend.domain.member.dto.*;
import com.solucitation.midpoint_backend.domain.member.exception.PasswordMismatchException;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController2 {
    private final MemberService memberService;
    private final Validator validator;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    /**
     * 회원 프로필 정보를 가져옵니다.
     *
     * @param authentication 인증 정보
     * @return 회원의 이름을 포함한 응답
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getMemberInfo(Authentication authentication) {
        String email = authentication.getName();
        MemberProfileResponseDto memberProfile = memberService.getMemberProfile(email);
        return ResponseEntity.ok(memberProfile);
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestPart(value = "profileUpdateRequestDto", required = false) String profileUpdateRequestDtoJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws JsonProcessingException {
        if (profileUpdateRequestDtoJson == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "profile_update_empty_dto", "message", "프로필 수정 요청 dto가 비었습니다."));
        }
        ProfileUpdateRequestDto profileUpdateRequestDto = objectMapper.readValue(profileUpdateRequestDtoJson, ProfileUpdateRequestDto.class);
        String email = authentication.getName();
        return validateAndUpdate(email, profileUpdateRequestDto, profileImage);
    }

    private ResponseEntity<?> validateAndUpdate(String email, @Valid ProfileUpdateRequestDto profileUpdateRequestDto, MultipartFile profileImage) {
        memberService.updateMember(email, profileUpdateRequestDto, profileImage);
        return ResponseEntity.ok(Map.of("message", "프로필 수정이 성공적으로 완료되었습니다."));
    }


    /**
     * 비밀번호를 확인합니다.
     *
     * @param passwordVerifyRequestDto 비밀번호 확인 요청 DTO
     * @param authentication           인증 정보
     * @return 비밀번호가 일치하면 새로운 액세스 토큰을 반환, 일치하지 않으면 오류 메시지 반환
     */
    @PostMapping("/verify-pw")
    public ResponseEntity<?> verifyPassword(@RequestBody @Valid PasswordVerifyRequestDto passwordVerifyRequestDto, Authentication authentication) {
        String email = authentication.getName();
        try {
            memberService.verifyPassword(email, passwordVerifyRequestDto);
            String tokenForDelete = jwtTokenProvider.createShortLivedTokenWithPurpose(authentication, "delete");
            String tokenForResetPassword = jwtTokenProvider.createShortLivedTokenWithPurpose(authentication, "reset-password");

            return ResponseEntity.ok(Map.of(
                    "tokenForResetPassword", Map.of(
                            "grantType", "Bearer",
                            "X-Reset-Password-Token", tokenForResetPassword
                    ),
                    "tokenForDelete", Map.of(
                            "grantType", "Bearer",
                            "X-Delete-Token", tokenForDelete
                    )
            ));
        } catch (PasswordMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "password_mismatch", "message", e.getMessage()));
        }
    }

}
