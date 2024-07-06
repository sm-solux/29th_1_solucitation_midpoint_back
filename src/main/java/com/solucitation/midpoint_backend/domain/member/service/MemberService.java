package com.solucitation.midpoint_backend.domain.member.service;

import com.solucitation.midpoint_backend.domain.member.dto.SignupRequestDto;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관리를 위한 서비스 클래스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일이 이미 사용 중인지 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 이메일이 이미 사용 중이면 true, 아니면 false
     */
    public boolean isEmailAlreadyInUse(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * 이름과 이메일이 일치하는지 확인합니다.
     *
     * @param name  확인할 이름
     * @param email 확인할 이메일
     * @return 이름과 이메일이 일치하면 true, 아니면 false
     */
    public boolean isNameAndEmailMatching(String name, String email) {
        return memberRepository.findByEmail(email)
                .map(member -> member.getName().equals(name))
                .orElse(false);
    }

    /**
     * 새로운 회원을 등록합니다.
     *
     * @param signupRequestDto 회원가입 요청 DTO
     */
    @Transactional
    public void signUpMember(SignupRequestDto signupRequestDto) {

        // 비밀번호 암호화 및 새로운 회원 생성
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        Member newMember = Member.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())
                .nickname(signupRequestDto.getNickname())
                .pwd(encodedPassword)
                .build();

        // 회원 저장
        memberRepository.save(newMember);
    }
}
