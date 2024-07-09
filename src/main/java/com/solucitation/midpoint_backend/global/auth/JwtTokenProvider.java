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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
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

    private long accessExpirationTime =  1000 * 60 * 60 * 24;
    private long refreshExpirationTime = 1000 * 60 * 60 * 24 * 7;

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
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired", e);
            throw new BaseException("EXPIRED_JWT");
        } catch (JwtException e) {
            log.error("Invalid JWT token", e);
            throw new BaseException("INVALID_JWT");
        }
    }
}
