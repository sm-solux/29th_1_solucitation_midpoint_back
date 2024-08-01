package com.solucitation.midpoint_backend.domain.member.api;
import com.solucitation.midpoint_backend.domain.member.dto.KakaoLoginRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.TokenResponseDto;
import com.solucitation.midpoint_backend.domain.member.service.OAuth2KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {
    private final OAuth2KakaoService oAuth2KakaoService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @PostMapping("/kakao-login-info")
    public ResponseEntity<Map<String, String>> getKakaoConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("clientId", clientId);
        config.put("redirectUri", redirectUri);
        return ResponseEntity.ok(config);
    }
    /**
     * 카카오 OAuth2 인증 후 프론트엔드에서 전달된 인가 코드를 처리합니다.
     *
     * 프론트엔드에서 카카오 로그인 인증을 완료한 후, 카카오 서버로부터 전달받은 인가 코드를 사용하여
     * 백엔드에서 카카오 액세스 토큰을 요청하고, 사용자 정보를 가져와서 회원으로 등록하거나 인증을 수행합니다.
     * 그 후, 자체 JWT 액세스 토큰과 리프레시 토큰을 생성하여 응답합니다. (기존 로그인과 동일)
     *
     * @param code 인가 코드를 포함한 요청 본문
     * @return 성공 시 JWT 액세스 토큰과 리프레시 토큰을 포함한 응답
     */
    @PostMapping("/oauth2/code/kakao")
    public ResponseEntity<?> oauth2KakaoCallback(@RequestParam String code) {
        TokenResponseDto tokenResponse = oAuth2KakaoService.registerOrAuthenticateKakaoUser(code);
        return ResponseEntity.ok(tokenResponse);
    }
}