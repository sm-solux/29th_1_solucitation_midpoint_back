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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailController {
    private final EmailService emailService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    // [회원가입] 유효한 이메일인지 판단하기 위해 인증코드 발송하는 api
    @PostMapping("/signup/verify-email")
    public ResponseEntity<String> sendVerificationEmailV2(@RequestBody VerificationEmailRequestDto verificationEmailRequestDto) {
        // 이메일 중복 체크
        if (memberService.isEmailAlreadyInUse(verificationEmailRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body("이미 사용중인 이메일입니다.");
        }
        EmailMessage emailMessage = EmailMessage.builder()
                .to(verificationEmailRequestDto.getEmail())
                .subject("[midpoint] 이메일 인증을 위한 인증 코드 발송")
                .build();
        String code = emailService.sendVerificationMail(emailMessage, "verify");
//        return ResponseEntity.ok(code);
        return ResponseEntity.ok("[회원가입] 인증 코드가 이메일로 발송되었습니다. 회원가입 창으로 돌아가 4분 이내에 입력해주세요.");
    }

    // [회원가입] 사용자가 입력한 이메일 인증 코드를 검증하는 api
    @PostMapping("/signup/verify-code")
    public ResponseEntity<String> verifyCodeV2(@RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {
        boolean isValid = emailService.verifyCode(verificationCodeRequestDto.getEmail(), verificationCodeRequestDto.getCode());

        if (isValid) {
            return ResponseEntity.ok("올바른 인증번호입니다.");
        } else {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 인증번호입니다.");
        }
    }

    // [비밀번호 찾기] 유효한 이메일인지 판단하기 위해 인증코드 발송하는 api
    @PostMapping("/reset-pw/verify-email")
    public ResponseEntity<String> sendPwResetEmailV3(@RequestBody PwResetRequestDto pwResetRequestDto) {
        // 이름과 이메일이 모두 일치하는지 검증
        if (!memberService.isNameAndEmailMatching(pwResetRequestDto.getName(), pwResetRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body("이름 또는 이메일 정보가 일치하지 않습니다.");
        }
        EmailMessage emailMessage = EmailMessage.builder()
                .to(pwResetRequestDto.getEmail())
                .subject("[midpoint] 비밀번호 재설정을 위한 인증 코드 발송")
                .build();
        String code = emailService.sendVerificationMail(emailMessage, "reset-pw");
//        return ResponseEntity.ok(code);
        return ResponseEntity.ok("[비밀번호 찾기] 인증 코드가 이메일로 발송되었습니다. 비밀번호 찾기 창으로 돌아가 4분 이내에 입력해주세요.");
    }

    // [비밀번호 찾기] 사용자가 입력한 이메일 인증 코드를 검증하는 api
    @PostMapping("/reset-pw/verify-code")
    public ResponseEntity<String> verifyCodeV3(@RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {
        boolean isValid = emailService.verifyCode(verificationCodeRequestDto.getEmail(), verificationCodeRequestDto.getCode());

        if (isValid) {
            // 사용자 인증
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    verificationCodeRequestDto.getEmail(), null, Collections.emptyList()
            );

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(authentication);

            // Bearer 접두사 추가하여 리턴
            return ResponseEntity.ok("Bearer " + accessToken);
        } else {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 인증번호입니다.");
        }
    }
}
