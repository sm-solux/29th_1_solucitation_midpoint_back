package com.solucitation.midpoint_backend.domain.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solucitation.midpoint_backend.domain.member.dto.*;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.exception.PasswordMismatchException;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestPart(value = "profileUpdateRequestDto") String profileUpdateRequestDtoJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws JsonProcessingException {
        ProfileUpdateRequestDto profileUpdateRequestDto = objectMapper.readValue(profileUpdateRequestDtoJson, ProfileUpdateRequestDto.class);
        String email = authentication.getName();
        return validateAndUpdate(email, profileUpdateRequestDto, profileImage);
    }

    private ResponseEntity<?> validateAndUpdate(String email, @Valid ProfileUpdateRequestDto profileUpdateRequestDto, MultipartFile profileImage) {
        memberService.updateMember(email, profileUpdateRequestDto, profileImage);
        return ResponseEntity.ok(Map.of("message", "프로필 수정이 성공적으로 완료되었습니다."));
    }


    /**
     * 비밀번호를 재설정합니다.
     *
     * @param token 인증 토큰
     * @param resetPwRequestDto 비밀번호 재설정 요청 DTO
     * @return 비밀번호 재설정 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/reset-pw")
    public ResponseEntity<?> resetPassword(@RequestHeader("Authorization") String token, @RequestBody @Valid ResetPwRequestDto resetPwRequestDto) {
        Set<ConstraintViolation<ResetPwRequestDto>> violations = validator.validate(resetPwRequestDto);
        if (!violations.isEmpty()) {
            List<ValidationErrorResponse.FieldError> fieldErrors = violations.stream()
                    .map(violation -> new ValidationErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.toList());
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(fieldErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        String email = jwtTokenProvider.extractEmailFromToken(token);
        if (email == null || !email.equals(resetPwRequestDto.getEmail())) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized", "message", "비밀번호를 재설정할 수 있는 권한이 없습니다."));
        }
        if (!resetPwRequestDto.getNewPassword().equals(resetPwRequestDto.getNewPasswordConfirm())) {
            return ResponseEntity.badRequest().body(Map.of("error", "password_mismatch", "message", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다."));
        }
        memberService.resetPassword(resetPwRequestDto.getEmail(), resetPwRequestDto.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정이 성공적으로 완료되었습니다."));
    }

    /**
     * 비밀번호를 확인합니다.
     *
     * @param passwordVerifyRequestDto 비밀번호 확인 요청 DTO
     * @param authentication 인증 정보
     * @return 비밀번호가 일치하면 새로운 액세스 토큰을 반환, 일치하지 않으면 오류 메시지 반환
     */
    @PostMapping("/verify-pw")
    public ResponseEntity<?> verifyPassword(@RequestBody @Valid PasswordVerifyRequestDto passwordVerifyRequestDto, Authentication authentication) {
        Set<ConstraintViolation<PasswordVerifyRequestDto>> violations = validator.validate(passwordVerifyRequestDto);
        if (!violations.isEmpty()) {
            List<ValidationErrorResponse.FieldError> fieldErrors = violations.stream()
                    .map(violation -> new ValidationErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.toList());
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(fieldErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        String email = authentication.getName();
        try {
            memberService.verifyPassword(email, passwordVerifyRequestDto);
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            AccessTokenResponseDto tokenResponse = AccessTokenResponseDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .build();
            return ResponseEntity.ok(tokenResponse);
        } catch (PasswordMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "password_mismatch", "message", e.getMessage()));
        }
    }
}
