package com.solucitation.midpoint_backend.domain.member.service;

import com.solucitation.midpoint_backend.domain.community_board.entity.Image;
import com.solucitation.midpoint_backend.domain.community_board.repository.ImageRepository;
import com.solucitation.midpoint_backend.domain.email.service.EmailServiceV2;
import com.solucitation.midpoint_backend.domain.file.service.S3Service;
import com.solucitation.midpoint_backend.domain.member.dto.LoginRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.SignupRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.TokenResponseDto;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.exception.EmailAlreadyInUseException;
import com.solucitation.midpoint_backend.domain.member.exception.EmailNotVerifiedException;
import com.solucitation.midpoint_backend.domain.member.exception.NicknameAlreadyInUseException;
import com.solucitation.midpoint_backend.domain.member.exception.PasswordMismatchException;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import com.solucitation.midpoint_backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

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
    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


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
    public void signUpMember(SignupRequestDto signupRequestDto, MultipartFile profileImage) {
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

        // 이메일 인증 여부 확인
        if (!emailServiceV2.isEmailVerified(signupRequestDto.getEmail())) {
            throw new EmailNotVerifiedException("이메일 인증을 먼저 시도해주세요.");
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

        // 프로필 이미지 업로드 및 저장
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String profileImageUrl = s3Service.upload("profile-images", profileImage.getOriginalFilename(), profileImage);
                Image image = Image.builder()
                        .imageUrl(profileImageUrl)
                        .member(newMember)
                        .build();
                imageRepository.save(image);
            } catch (IOException e) {
                log.error("프로필 이미지 업로드 실패: {}", e.getMessage());
                // 이미지 업로드 실패 알림
                throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.");
            }
        }
    }

    /**
     * 회원 로그인 처리
     *
     * @param loginRequestDto 로그인 요청 DTO
     * @return JWT 액세스 토큰
     * @throws InvalidCredentialsException 로그인 실패 시 예외 발생
     */
    @Transactional
    public TokenResponseDto loginMember(LoginRequestDto loginRequestDto) throws InvalidCredentialsException {
        // Member 정보 확인 및 비밀번호 검증
        Optional<Member> foundMember = memberRepository.findByEmailOrNickname(loginRequestDto.getIdentifier(), loginRequestDto.getIdentifier());
        foundMember
                .orElseThrow(() -> new InvalidCredentialsException("이메일/닉네임 정보가 일치하지 않습니다."));
        Member member = foundMember.get();

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPwd())) {
            throw new InvalidCredentialsException("비밀번호 정보가 일치하지 않습니다.");
        }

        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            member.getEmail(), // 인증을 이메일로 통일
                            loginRequestDto.getPassword()
                    )
            );

            // 인증된 사용자 정보를 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            // 토큰 응답 DTO 생성
            return TokenResponseDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (BadCredentialsException e) {
            log.error("유효하지 않은 사용자 정보: {}", e.getMessage());
            throw new InvalidCredentialsException("유효하지 않은 이메일/닉네임 또는 비밀번호입니다.");
        } catch (Exception e) {
            log.error("로그인 도중 예상치 못한 오류 발생: {}", e.getMessage());
            throw new BaseException("로그인 중 예상치 못한 오류가 발생했습니다.");
        }
    }

    @Transactional
    public TokenResponseDto refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
        // TODO 현재 로그아웃된 사용자의 토큰이 아닌지 검증
        String email = jwtTokenProvider.getClaimsFromToken(refreshToken).getSubject();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        // 기존 Refresh Token 삭제
        jwtTokenProvider.deleteRefreshToken(email);

        // 새로운 Access Token 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), null, Collections.emptyList());
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));
    }
}
