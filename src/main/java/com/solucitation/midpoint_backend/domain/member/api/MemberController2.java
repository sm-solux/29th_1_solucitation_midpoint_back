package com.solucitation.midpoint_backend.domain.member.api;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController2 {
    private final MemberService memberService;
    @GetMapping("/api/member/profile")
    public ResponseEntity<?> getMemberInfo(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberService.getMemberByEmail(email);
        return ResponseEntity.ok(member.getName());
    }
}
