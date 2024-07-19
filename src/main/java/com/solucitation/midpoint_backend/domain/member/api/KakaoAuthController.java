package com.solucitation.midpoint_backend.domain.member.api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class KakaoAuthController {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @GetMapping("/kakao-login-info")
    public ResponseEntity<Map<String, String>> getKakaoConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("clientId", clientId);
        config.put("redirectUri", redirectUri);
        return ResponseEntity.ok(config);
    }
}