package com.solucitation.midpoint_backend.domain.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solucitation.midpoint_backend.domain.member.dto.LoginRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.SignupRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.TokenResponseDto;
import com.solucitation.midpoint_backend.domain.member.dto.ValidationErrorResponse;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {
    private final MemberService memberService;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 새로운 회원을 등록합니다.
     *
     * @param signupRequestDtoJson 회원가입 요청 DTO
     * @return 성공 시 200 OK와 성공 메시지를 반환, 실패 시 400 Bad Request와 오류 메시지를 반환
     */
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signUpMember(
            @RequestPart(value = "signupRequestDto") String signupRequestDtoJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws JsonProcessingException {
        SignupRequestDto signupRequestDto = objectMapper.readValue(signupRequestDtoJson, SignupRequestDto.class);
        log.info("signupRequestDto = " + signupRequestDto);

        // Validate the DTO manually
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);
        if (!violations.isEmpty()) {
            List<ValidationErrorResponse.FieldError> fieldErrors = violations.stream()
                    .map(violation -> new ValidationErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.toList());
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(fieldErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return validateAndSignUp(signupRequestDto, profileImage);
    }

    private ResponseEntity<?> validateAndSignUp(@Valid SignupRequestDto signupRequestDto, MultipartFile profileImage) {
        memberService.signUpMember(signupRequestDto, profileImage);
        return ResponseEntity.ok(Map.of("message", "회원가입에 성공하였습니다!"));
    }

    /**
     * 회원 로그인 처리
     *
     * @param loginRequestDto 로그인 요청 DTO
     * @return 성공 시 JWT 액세스 토큰과 리프레시 토큰을 반환, 실패 시 400 Bad Request와 오류 메시지를 반환
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            TokenResponseDto tokenResponse = memberService.loginMember(loginRequestDto);
            return ResponseEntity.ok(tokenResponse);
        } catch (InvalidCredentialsException e) {
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                    List.of(new ValidationErrorResponse.FieldError("login", e.getMessage()))
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Refresh Token을 이용하여 새로운 Access Token을 발급합니다.
     *
     * @param refreshTokenHeader Authorization 헤더에 포함된 Refresh Token
     * @return 새로 발급된 Access Token과 Refresh Token을 포함한 응답
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        try {
            String refreshToken = jwtTokenProvider.resolveToken(refreshTokenHeader);

            // 블랙리스트에 등록된 토큰인지 확인
            if (jwtTokenProvider.isInBlacklist(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "invalid_token",
                        "message", "블랙리스트에 등록된 토큰입니다. 다시 로그인해 주세요."
                ));
            }

            TokenResponseDto tokenResponse = memberService.refreshAccessToken(refreshToken); // Refresh Token을 검증한 뒤 새로운 Access Token과 Refresh Token을 발급
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) { // 만료되거나 유효하지 않은 Refresh Token일 경우 401 Unauthorized 응답 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 사용자를 로그아웃시키고 Refresh Token을 무효화합니다.
     *
     * @param refreshTokenHeader Authorization 헤더에 포함된 Refresh Token
     * @return 로그아웃 성공 메시지 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutMember(@RequestHeader("Authorization") String refreshTokenHeader) {
        String refreshToken = jwtTokenProvider.resolveToken(refreshTokenHeader); // Authorization 헤더에서 Bearer 토큰을 제외한 Refresh Token만 추출

        jwtTokenProvider.invalidateRefreshToken(refreshToken); // Redis에서 토큰을 삭제(Refresh Token을 무효화하여 로그아웃 처리)
        SecurityContextHolder.clearContext(); // SecurityContextHolder에서 인증 정보 삭제

        jwtTokenProvider.addToBlacklist(refreshToken); // refreshToken을 블랙리스트에 추가
        return ResponseEntity.ok(Map.of("message", "로그아웃에 성공하였습니다."));
    }
}
