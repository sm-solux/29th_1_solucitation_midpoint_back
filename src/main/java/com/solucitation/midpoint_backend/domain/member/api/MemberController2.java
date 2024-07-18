package com.solucitation.midpoint_backend.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solucitation.midpoint_backend.domain.file.service.S3Service;
import com.solucitation.midpoint_backend.domain.member.dto.*;
import com.solucitation.midpoint_backend.domain.member.exception.PasswordMismatchException;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController2 {
    private final MemberService memberService;
    private final S3Service s3Service;
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
    ) throws IOException {
        if (profileUpdateRequestDtoJson == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "profile_update_empty_dto", "message", "프로필 수정 요청 dto가 비었습니다."));
        }
        ProfileUpdateRequestDto profileUpdateRequestDto = objectMapper.readValue(profileUpdateRequestDtoJson, ProfileUpdateRequestDto.class);
        String email = authentication.getName();
        return validateAndUpdate(email, profileUpdateRequestDto, profileImage);
    }

    private ResponseEntity<?> validateAndUpdate(String email, @Valid ProfileUpdateRequestDto profileUpdateRequestDto, MultipartFile profileImage) throws IOException {
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

    /**
     * 회원 탈퇴를 처리합니다.
     *
     * @param deleteToken    비밀번호 확인했고, 탈퇴하겠다는 인증 토큰
     * @param authentication 인증 객체
     * @return 회원 탈퇴 성공 메시지 또는 오류 메시지
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestHeader("X-Delete-Token") String deleteToken, Authentication authentication) {
        ResponseEntity<?> validationResponse = validateTokenAndEmail(deleteToken, "delete", authentication);
        if (validationResponse != null) { // 에러 응답을 가진 경우
            return validationResponse; // 에러 응답을 리턴
        }
        String tokenEmail = jwtTokenProvider.extractEmailFromToken(jwtTokenProvider.resolveToken(deleteToken));
        String profileImgUrl = memberService.deleteMember(tokenEmail); // 프로필 이미지와 멤버 엔티티 삭제
        log.info("profileImgUrl는 by test? " + profileImgUrl);
        s3Service.delete(profileImgUrl); // 프로필 이미지 S3에서 삭제
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 성공적으로 완료되었습니다."));
    }

    /**
     * 비밀번호를 재설정합니다.
     *
     * @param resetToken         비밀번호 확인했고, 비밀번호 재설정하겠다는 인증 토큰
     * @param resetPwRequestDto 비밀번호 재설정 요청 DTO
     * @param authentication    인증 객체
     * @return 비밀번호 재설정 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/reset-pw")
    public ResponseEntity<?> resetPassword(@RequestHeader("X-Reset-Password-Token") String resetToken, @RequestBody @Valid ResetPwRequestDto resetPwRequestDto, Authentication authentication) {
        ResponseEntity<?> validationResponse = validateTokenAndEmail(resetToken, "reset-password", authentication);
        if (validationResponse != null) {
            return validationResponse;
        }
        if (!resetPwRequestDto.getNewPassword().equals(resetPwRequestDto.getNewPasswordConfirm())) {
            return ResponseEntity.badRequest().body(Map.of("error", "password_mismatch", "message", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다."));
        }
        String tokenEmail = jwtTokenProvider.extractEmailFromToken(jwtTokenProvider.resolveToken(resetToken));
        memberService.resetPassword(tokenEmail, resetPwRequestDto.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정이 성공적으로 완료되었습니다."));
    }

    private ResponseEntity<?> validateTokenAndEmail(String BFtoken, String action, Authentication authentication) {
        String token = jwtTokenProvider.resolveToken(BFtoken);
        if (!jwtTokenProvider.validateTokenByPwConfirm(token, action)) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized", "message", "권한이 없습니다. action: " + action));
        }
        String tokenEmail = jwtTokenProvider.extractEmailFromToken(token); // 토큰으로부터 이메일 추출
        String authEmail = authentication.getName(); // 인증 객체로부터 이메일 추출

        if (!tokenEmail.equals(authEmail)) { // 토큰으로부터 추출한 이메일과 인증 객체로부터 추출한 이메일이 동일한지 비교
            return ResponseEntity.status(403).body(Map.of("error", "forbidden", "message", "토큰의 이메일과 인증된 이메일이 일치하지 않습니다."));
        }
        return null;
    }
}
