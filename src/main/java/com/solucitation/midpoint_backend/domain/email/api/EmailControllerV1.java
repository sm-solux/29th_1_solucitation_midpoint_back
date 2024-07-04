package com.solucitation.midpoint_backend.domain.email.api;

import com.solucitation.midpoint_backend.domain.email.dto.EmailMessage;
import com.solucitation.midpoint_backend.domain.email.dto.PwResetRequestDto;
import com.solucitation.midpoint_backend.domain.email.dto.VerificationCodeRequestDto;
import com.solucitation.midpoint_backend.domain.email.dto.VerificationEmailRequestDto;
import com.solucitation.midpoint_backend.domain.email.service.EmailServiceV1;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
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
public class EmailControllerV1 {
    private final EmailServiceV1 emailServiceV1;
    private final MemberService memberService;

    // 유효한 이메일인지 판단하기 위해 인증코드 발송하는 api
    @PostMapping("/verify-email/v1")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody VerificationEmailRequestDto verificationEmailRequestDto) {
        // 이메일 중복 체크
        if (memberService.isEmailAlreadyInUse(verificationEmailRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body("이미 사용중인 이메일입니다.");
        }
        EmailMessage emailMessage = EmailMessage.builder()
                .to(verificationEmailRequestDto.getEmail())
                .subject("[midpoint] 이메일 인증을 위한 인증 코드 발송")
                .build();
        String code = emailServiceV1.sendVerificationMail(emailMessage, "verify");
        return ResponseEntity.ok(code);
    }

    // 비밀번호를 잊어버렸을 때 임시 비밀번호를 발급하는 api
    @PostMapping("/reset-pw/v1")
    public ResponseEntity<String> sendPwResetEmail(@RequestBody PwResetRequestDto pwResetRequestDto) {
        // 이름과 이메일이 모두 일치하는지 검증
        if (!memberService.isNameAndEmailMatching(pwResetRequestDto.getName(), pwResetRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body("이름 또는 이메일 정보가 일치하지 않습니다.");
        }
        EmailMessage emailMessage = EmailMessage.builder()
                .to(pwResetRequestDto.getEmail())
                .subject("[midpoint] 비밀번호 초기화를 위한 인증 코드 발송")
                .build();
        String code = emailServiceV1.sendVerificationMail(emailMessage, "reset-pw");
        return ResponseEntity.ok(code);
    }

    // 사용자가 입력한 인증 코드를 검증하는 api
    @PostMapping("/verify-code/v1")
    public ResponseEntity<String> verifyCodeV1(@RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {
        boolean isValid = emailServiceV1.verifyCode(verificationCodeRequestDto.getEmail(), verificationCodeRequestDto.getCode());

        if (isValid) {
            return ResponseEntity.ok("올바른 인증번호입니다.");
        } else {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 인증번호입니다.");
        }
    }
}
