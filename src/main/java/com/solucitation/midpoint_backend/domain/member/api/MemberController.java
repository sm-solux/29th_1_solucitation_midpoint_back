package com.solucitation.midpoint_backend.domain.member.api;

import com.solucitation.midpoint_backend.domain.member.dto.SignupRequestDto;
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
public class MemberController {
    private final MemberService memberService;

    /**
     * 새로운 회원을 등록합니다.
     *
     * @param signupRequestDto 회원가입 요청 DTO
     * @return 성공 시 200 OK와 성공 메시지, 실패 시 400 Bad Request와 일반 오류 메시지 반환
     */
    @PostMapping("/signup")
    private ResponseEntity<String> signUpMember(@RequestBody SignupRequestDto signupRequestDto) {
        try {
            memberService.signUpMember(signupRequestDto);
            return ResponseEntity.ok("회원가입에 성공하였습니다!");
        } catch (IllegalArgumentException e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("회원가입에 실패하였습니다. 입력한 정보를 확인해 주세요.");
        }
    }
}
