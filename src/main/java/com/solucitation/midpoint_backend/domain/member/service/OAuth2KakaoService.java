package com.solucitation.midpoint_backend.domain.member.service;

import com.solucitation.midpoint_backend.domain.member.dto.TokenResponseDto;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2KakaoService { // 인가 코드 -> 카카오 토큰을 요청
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final WebClient webClient = WebClient.builder().build();

    public String getKakaoAccessToken(String code) {
        String tokenUri = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code)
                .toUriString();
        log.info("code는?!!" + code);
        log.info("client_id는?!" + clientId);
        log.info("client_secret" + clientSecret);
        log.info("redirect_uri" + redirectUri);
        try {
            Map<String, Object> response = webClient.post()
                    .uri(tokenUri)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if (response != null && response.get("access_token") instanceof String) {
                return (String) response.get("access_token");
            }

            throw new IllegalStateException("Failed to retrieve access token from Kakao");
        } catch (WebClientResponseException e) {
            log.error("Error response from Kakao API: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            throw new IllegalStateException("Unexpected error while retrieving access token from Kakao", e);
        }
    }

    public Member getKakaoUser(String accessToken) {
        Map<String, Object> response = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        if (response != null) {
            Optional<Map<String, Object>> kakaoAccountOpt = cast(response.get("kakao_account"));
            if (kakaoAccountOpt.isPresent()) {
                Map<String, Object> kakaoAccount = kakaoAccountOpt.get();
                String email = (String) kakaoAccount.get("email");

                Optional<Map<String, Object>> profileOpt = cast(kakaoAccount.get("profile"));
                if (profileOpt.isPresent()) {
                    Map<String, Object> profile = profileOpt.get();
                    String nickname = generateRandomNickname();

                    return Member.builder()
                            .email(email)
                            .name((String) profile.get("nickname")) // 카카오 프로필의 닉네임을 name에 저장
                            .nickname(nickname) // 임의의 영어와 숫자의 7자리 조합
                            .pwd("") // 빈 문자열로 비밀번호 설정
                            .build();
                }
            }
        }

        throw new IllegalStateException("Failed to retrieve user info from Kakao");
    }

    public TokenResponseDto registerOrAuthenticateKakaoUser(String code) {
        String accessToken = getKakaoAccessToken(code);
        Member kakaoUser = getKakaoUser(accessToken);

        Member member = memberRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> memberRepository.save(kakaoUser));

        // 외부 OAuth2 로그인의 경우, 외부 제공자가 이미 사용자를 인증했기 때문에, 신뢰하고 사용자를 설정
        // 애플리케이션 내부에서 인증된 사용자임을 보장x
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // 기본 권한: ROLE_USER
        Authentication authentication = new UsernamePasswordAuthenticationToken( // 인증된 사용자 정보를 담는 객체를 생성 (이메일과 "ROLE_USER" 권한을 포함)
                member.getEmail(), "", authorities); // 빈 문자열로 비밀번호 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증된 사용자 정보를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    // 안전한 타입 캐스팅을 위한 유틸리티 메서드
    @SuppressWarnings("unchecked")
    private <T> Optional<T> cast(Object obj) {
        try {
            return Optional.of((T) obj);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    // 7자리 임의의 영어와 숫자와 문자 '_'의 조합을 생성하는 메서드
    private String generateRandomNickname() {
        int length = 7;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}