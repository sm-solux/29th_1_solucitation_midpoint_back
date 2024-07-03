package com.solucitation.midpoint_backend.domain.member.api;

import com.solucitation.midpoint_backend.domain.member.dto.EmailMessage;
import com.solucitation.midpoint_backend.domain.member.dto.PwResetRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.VerificationEmailRequestDto;
import com.solucitation.midpoint_backend.domain.member.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailController {
    private final EmailService emailService;

    // 유효한 이메일인지 판단하기 위해 인증코드 발송하는 api
    @PostMapping("/verify-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody VerificationEmailRequestDto verificationEmailRequestDto) {
        // TODO 이메일 중복 체크
        EmailMessage emailMessage = EmailMessage.builder()
                .to(verificationEmailRequestDto.getEmail())
                .subject("[midpoint] 이메일 인증을 위한 인증 코드 발송")
                .build();
        String code = emailService.sendVerificationMail(emailMessage, "verify");
        return ResponseEntity.ok(code);
    }

    // 비밀번호를 잊어버렸을 때 임시 비밀번호를 발급하는 api
    @PostMapping("/reset-pw")
    public ResponseEntity<String> sendPwResetEmail(@RequestBody PwResetRequestDto pwResetRequestDto) {
        // TODO 이름과 이메일이 모두 일치하는지 검증
        EmailMessage emailMessage = EmailMessage.builder()
                .to(pwResetRequestDto.getEmail())
                .subject("[midpoint] 비밀번호 초기화를 위한 인증 코드 발송")
                .build();
        String code = emailService.sendVerificationMail(emailMessage, "reset-pw");
        return ResponseEntity.ok(code);
    }
}
