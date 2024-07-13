package com.solucitation.midpoint_backend.domain.member.api;

import com.solucitation.midpoint_backend.domain.member.dto.ResetPwRequestDto;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController2 {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("/profile")
    public ResponseEntity<?> getMemberInfo(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberService.getMemberByEmail(email);
        return ResponseEntity.ok(member.getName());
    }

    @PostMapping("/reset-pw")
    public ResponseEntity<String> resetPassword(@RequestHeader("Authorization") String token, @RequestBody @Valid ResetPwRequestDto resetPwRequestDto) {
        String email = jwtTokenProvider.extractEmailFromToken(token);
        if (email == null || !email.equals(resetPwRequestDto.getEmail())) {
            return ResponseEntity.status(401).body("비밀번호를 재설정할 수 있는 권한이 없습니다.");
        }
        if (!resetPwRequestDto.getNewPassword().equals(resetPwRequestDto.getNewPasswordConfirm())) {
            return ResponseEntity.badRequest().body("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
        }
        memberService.resetPassword(resetPwRequestDto.getEmail(), resetPwRequestDto.getNewPassword());
        return ResponseEntity.ok("비밀번호 재설정이 성공적으로 완료되었습니다.");
    }
}
