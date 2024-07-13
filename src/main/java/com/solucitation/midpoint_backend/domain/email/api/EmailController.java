package com.solucitation.midpoint_backend.domain.email.api;

import com.solucitation.midpoint_backend.domain.email.dto.EmailMessage;
import com.solucitation.midpoint_backend.domain.email.dto.PwResetRequestDto;
import com.solucitation.midpoint_backend.domain.email.dto.VerificationCodeRequestDto;
import com.solucitation.midpoint_backend.domain.email.dto.VerificationEmailRequestDto;
import com.solucitation.midpoint_backend.domain.email.service.EmailService;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailController {
    private final EmailService emailService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup/verify-email")
    public ResponseEntity<?> sendVerificationEmailV2(@RequestBody VerificationEmailRequestDto verificationEmailRequestDto) {
        if (memberService.isEmailAlreadyInUse(verificationEmailRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "email_in_use", "message", "이미 사용중인 이메일입니다."));
        }
        EmailMessage emailMessage = EmailMessage.builder()
                .to(verificationEmailRequestDto.getEmail())
                .subject("[midpoint] 이메일 인증을 위한 인증 코드 발송")
                .build();
        String code = emailService.sendVerificationMail(emailMessage, "verify");
        return ResponseEntity.ok(Map.of("message", "[회원가입] 인증 코드가 이메일로 발송되었습니다. 회원가입 창으로 돌아가 4분 이내에 입력해주세요."));
    }

    @PostMapping("/signup/verify-code")
    public ResponseEntity<?> verifyCodeV2(@RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {
        boolean isValid = emailService.verifyCode(verificationCodeRequestDto.getEmail(), verificationCodeRequestDto.getCode());

        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "올바른 인증번호입니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_code", "message", "유효하지 않거나 만료된 인증번호입니다."));
        }
    }

    @PostMapping("/reset-pw/verify-email")
    public ResponseEntity<?> sendPwResetEmailV3(@RequestBody PwResetRequestDto pwResetRequestDto) {
        if (!memberService.isNameAndEmailMatching(pwResetRequestDto.getName(), pwResetRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "name_email_mismatch", "message", "이름 또는 이메일 정보가 일치하지 않습니다."));
        }
        EmailMessage emailMessage = EmailMessage.builder()
                .to(pwResetRequestDto.getEmail())
                .subject("[midpoint] 비밀번호 재설정을 위한 인증 코드 발송")
                .build();
        String code = emailService.sendVerificationMail(emailMessage, "reset-pw");
        return ResponseEntity.ok(Map.of("message", "[비밀번호 찾기] 인증 코드가 이메일로 발송되었습니다. 비밀번호 찾기 창으로 돌아가 4분 이내에 입력해주세요."));
    }

    @PostMapping("/reset-pw/verify-code")
    public ResponseEntity<?> verifyCodeV3(@RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {
        boolean isValid = emailService.verifyCode(verificationCodeRequestDto.getEmail(), verificationCodeRequestDto.getCode());

        if (isValid) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    verificationCodeRequestDto.getEmail(), null, Collections.emptyList()
            );
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            return ResponseEntity.ok(Map.of("token", "Bearer " + accessToken));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_code", "message", "유효하지 않거나 만료된 인증번호입니다."));
        }
    }
}
