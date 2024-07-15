package com.solucitation.midpoint_backend.domain.email.api;

import com.solucitation.midpoint_backend.domain.email.dto.EmailMessage;
import com.solucitation.midpoint_backend.domain.email.dto.PwResetRequestDto;
import com.solucitation.midpoint_backend.domain.email.dto.VerificationCodeRequestDto;
import com.solucitation.midpoint_backend.domain.email.dto.VerificationEmailRequestDto;
import com.solucitation.midpoint_backend.domain.email.service.EmailService;
import com.solucitation.midpoint_backend.domain.member.dto.AccessTokenResponseDto;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * 이메일 관련 인증 API 컨트롤러 클래스.
 * 이메일 인증, 인증 코드 확인 등의 기능을 제공한다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailController {
    private final EmailService emailService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 시 이메일 인증을 위해 인증 코드를 발송한다.
     *
     * @param verificationEmailRequestDto 이메일 인증 요청 DTO
     * @return 인증 코드 발송 결과 응답
     */
    @PostMapping("/signup/verify-email")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody @Valid VerificationEmailRequestDto verificationEmailRequestDto) {
        // 이메일 중복 체크
        if (memberService.isEmailAlreadyInUse(verificationEmailRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "email_in_use", "message", "이미 사용중인 이메일입니다."));
        }
        // 인증 이메일 발송
        return sendEmailCode(verificationEmailRequestDto.getEmail());
    }

    /**
     * 회원가입 시 사용자가 입력한 인증 코드를 검증한다.
     *
     * @param verificationCodeRequestDto 인증 코드 검증 요청 DTO
     * @return 인증 코드 검증 결과 응답
     */
    @PostMapping("/signup/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid VerificationCodeRequestDto verificationCodeRequestDto) {
        // 인증 코드 검증
        boolean isValid = emailService.verifyCode(verificationCodeRequestDto.getEmail(), verificationCodeRequestDto.getCode());

        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "올바른 인증번호입니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_code", "message", "유효하지 않거나 만료된 인증번호입니다."));
        }
    }

    /**
     * 비밀번호 재설정을 위해 이메일 인증 코드를 발송한다. (이후 로그인했을 때 이메일 인증 공통 코드로 쓰일 수 있음)
     *
     * @param pwResetRequestDto 비밀번호 재설정 요청 DTO
     * @return 인증 코드 발송 결과 응답
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> sendPwResetEmail(@RequestBody @Valid PwResetRequestDto pwResetRequestDto) {
        // 이름과 이메일이 일치하는지 검증
        if (!memberService.isNameAndEmailMatching(pwResetRequestDto.getName(), pwResetRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "name_email_mismatch", "message", "이름 또는 이메일 정보가 일치하지 않습니다."));
        }
        return sendEmailCode(pwResetRequestDto.getEmail());
    }

    /**
     * 비밀번호 재설정을 위한 이메일 인증 코드를 검증하고,
     * 유효한 경우 JWT 액세스 토큰을 발급한다.
     *
     * @param verificationCodeRequestDto 인증 코드 검증 요청 DTO
     * @return 유효한 인증 코드일 경우 JWT 액세스 토큰, 그렇지 않을 경우 오류 메시지
     */
    @PostMapping("/reset-pw/verify-code")
    public ResponseEntity<?> verifyCodeV3(@RequestBody @Valid VerificationCodeRequestDto verificationCodeRequestDto) {
        boolean isValid = emailService.verifyCode(verificationCodeRequestDto.getEmail(), verificationCodeRequestDto.getCode());

        if (isValid) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    verificationCodeRequestDto.getEmail(), null, Collections.emptyList()
            );
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            AccessTokenResponseDto tokenResponse = AccessTokenResponseDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .build();
            return ResponseEntity.ok(tokenResponse);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_code", "message", "유효하지 않거나 만료된 인증번호입니다."));
        }
    }

    private ResponseEntity<Map<String, String>> sendEmailCode(String email) {
        // 인증 이메일 발송
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("[midpoint] 이메일 인증을 위한 인증 코드 발송")
                .build();
        String code = emailService.sendVerificationMail(emailMessage, "verify");
        log.info("인증번호 code는??" + code);
        return ResponseEntity.ok(Map.of("message", "인증 코드가 이메일로 발송되었습니다. 이전 창으로 돌아가 4분 이내에 입력을 완료해주세요."));
    }
}
