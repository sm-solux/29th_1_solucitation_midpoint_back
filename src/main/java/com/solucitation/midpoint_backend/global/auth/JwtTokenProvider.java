package com.solucitation.midpoint_backend.global.auth;

import com.solucitation.midpoint_backend.domain.member.service.UserDetailsServiceImpl;
import com.solucitation.midpoint_backend.global.exception.BaseException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JWT 토큰 제공 클래스 - 토큰 생성, 검증, 인증 정보 추출
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final RedisTemplate<String, String> tokenRedisTemplate;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    private long accessExpirationTime = 1000 * 60 * 60 * 24; // 1일
    private long refreshExpirationTime = 1000 * 60 * 60 * 24 * 7; // 7일

    @Autowired
    public JwtTokenProvider(UserDetailsServiceImpl userDetailsService,
                            @Qualifier("tokenRedisTemplate") RedisTemplate<String, String> tokenRedisTemplate) {
        this.userDetailsService = userDetailsService;
        this.tokenRedisTemplate = tokenRedisTemplate;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("type", "refresh"); // 토큰 타입을 명시적으로 추가
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();

        tokenRedisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshExpirationTime,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        String userPrincipal = claims.getSubject();

        // 자체 로그인 -> UserDetailsService를 사용하여 사용자 정보를 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        // 소셜 로그인(권한 정보가 없는 경우) -> 기본 권한 "ROLE_USER"을 추가하여 인증 객체 생성
        if (userDetails.getAuthorities().isEmpty()) {
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 메소드 오버로딩
    public String resolveToken(HttpServletRequest httpServletRequest) { // HttpServletRequest를 인자로 받는 메소드
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveToken(String token) { // String을 인자로 받는 메소드
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired", e);
            throw e; // 예외를 던져 만료된 토큰 처리
        } catch (JwtException e) {
            log.error("Invalid JWT token", e);
            throw new BaseException("INVALID_JWT");
        }
    }
    // 비밀번호 확인시 발급되는 토큰 검증
    public boolean validateTokenByPwConfirm(String token, String expectedPurpose) {
        try {
            Claims claims = getClaimsFromToken(token);
            log.info("Claims: {}", claims);

            String purpose = claims.get("purpose", String.class);
            log.info("Token purpose: {}", purpose);

            if (!expectedPurpose.equals(purpose)) {
                log.error("Token purpose mismatch: expected={}, actual={}", expectedPurpose, purpose);
                return false;
            }

            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                log.error("Token has expired: expiration={}", expiration);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Refresh Token을 무효화하여 로그아웃 처리
    public void invalidateRefreshToken(String refreshToken) {
        try {
            Claims claims = getClaimsFromToken(refreshToken);
            String username = claims.getSubject();
            deleteRefreshToken(username, refreshToken);
        } catch (Exception e) {
            log.error("토큰 무효화 중 오류 발생: {}", e.getMessage());
            throw new BaseException("LOGOUT_ERROR");
        }
    }

    // Redis에서 토큰 삭제
    public void deleteRefreshToken(String username, String refreshToken) { // username과 refreshToken가 모두 일치하는 값 제거
        try {
            String storedToken = tokenRedisTemplate.opsForValue().get(username);
            if (refreshToken.equals(storedToken)) {
                tokenRedisTemplate.delete(username);
                log.info("Redis에서 토큰 삭제 완료: {}", username);
            } else {
                log.info("저장된 토큰과 일치하지 않음: {}", username);
            }
        } catch (Exception e) {
            log.error("Redis에서 토큰 삭제 중 오류 발생: {}", e.getMessage());
            throw new BaseException("DELETE_REDIS");
        }
    }

    // 블랙리스트에 refreshToken을 추가하고 만료 시간 설정
    public void addToBlacklist(String refreshToken) {
        long expiration = getClaimsFromToken(refreshToken).getExpiration().getTime();
        long currentTime = System.currentTimeMillis();
        long ttl = expiration - currentTime;

        tokenRedisTemplate.opsForValue().set(refreshToken, "invalid", ttl, TimeUnit.MILLISECONDS);
    }

    // 블랙리스트 확인하기
    public boolean isInBlacklist(String refreshToken) {
        return Boolean.TRUE.equals(tokenRedisTemplate.hasKey(refreshToken));
    }

    public String extractEmailFromToken(String token) {
        try {
            return getClaimsFromToken(token.replace("Bearer ", "")).getSubject();
        } catch (Exception e) {
            log.error("토큰에서 이메일 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    public String createShortLivedTokenWithPurpose(Authentication authentication, String purpose) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("purpose", purpose);

        Date now = new Date();
        Date validity = new Date(now.getTime() + 600_000); // 매우 짧은 만료 시간인 10분을 가짐

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return "refresh".equals(claims.get("type"));
    }
}