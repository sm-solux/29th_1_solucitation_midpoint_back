package com.solucitation.midpoint_backend.domain.member.service;

import com.solucitation.midpoint_backend.domain.email.service.EmailServiceV2;
import com.solucitation.midpoint_backend.domain.member.dto.SignupRequestDto;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.exception.EmailAlreadyInUseException;
import com.solucitation.midpoint_backend.domain.member.exception.EmailNotVerifiedException;
import com.solucitation.midpoint_backend.domain.member.exception.NicknameAlreadyInUseException;
import com.solucitation.midpoint_backend.domain.member.exception.PasswordMismatchException;
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
    private final EmailServiceV2 emailServiceV2;

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
     * 닉네임이 이미 사용 중인지 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임이 이미 사용 중이면 true, 아니면 false
     */
    public boolean isNicknameAlreadyInUse(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    /**
     * 새로운 회원을 등록합니다.
     *
     * @param signupRequestDto 회원가입 요청 DTO
     */
    @Transactional
    public void signUpMember(SignupRequestDto signupRequestDto) {
        // 이메일 인증 여부 확인
        if (!emailServiceV2.isEmailVerified(signupRequestDto.getEmail())) {
            throw new EmailNotVerifiedException("이메일 인증을 먼저 시도해주세요.");
        }

        // 닉네임이 이미 사용 중인지 확인
        if (isNicknameAlreadyInUse(signupRequestDto.getNickname())) {
            throw new NicknameAlreadyInUseException("이미 사용중인 닉네임입니다.");
        }

        // 이메일이 이미 사용 중인지 확인
        if (isEmailAlreadyInUse(signupRequestDto.getEmail())) {
            throw new EmailAlreadyInUseException("이미 사용중인 이메일입니다.");
        }

        // 비밀번호와 비밀번호 확인이 일치하는지 확인
        if (!signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword())) {
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
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
