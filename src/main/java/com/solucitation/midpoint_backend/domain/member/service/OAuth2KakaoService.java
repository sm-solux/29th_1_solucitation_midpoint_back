package com.solucitation.midpoint_backend.domain.member.service;

import com.solucitation.midpoint_backend.domain.member.dto.TokenResponseDto;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2KakaoService { // 인가 코드 -> 카카오 토큰을 요청
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final WebClient webClient = WebClient.builder().build();

    public String getKakaoAccessToken(String code) {
        String tokenUri = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code)
                .toUriString();

        Map<String, String> response = webClient.post()
                .uri(tokenUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response.get("access_token");
    }

    public Member getKakaoUser(String accessToken) {
        Map<String, Object> response = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");

        return Member.builder()
                .email(email)
                .name(nickname) // 카카오 프로필의 닉네임을 name에 저장
                .pwd("") // 비밀번호가 없는 OAuth 로그인 계정
                .build();
    }

    public TokenResponseDto registerOrAuthenticateKakaoUser(String code) {
        String accessToken = getKakaoAccessToken(code);
        Member kakaoUser = getKakaoUser(accessToken);

        Member member = memberRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> memberRepository.save(kakaoUser));

        // 외부 OAuth2 로그인의 경우, 외부 제공자가 이미 사용자를 인증했기 때문에, 신뢰하고 사용자를 설정
        // 애플리케이션 내부에서 인증된 사용자임을 보장x
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }
}
