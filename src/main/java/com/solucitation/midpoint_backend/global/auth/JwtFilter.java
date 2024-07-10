package com.solucitation.midpoint_backend.global.auth;

import com.solucitation.midpoint_backend.global.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 필터 클래스 - 요청의 Authorization 헤더에서 JWT를 추출하고 검증하여 SecurityContext에 저장
 */
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request); // JWT 추출
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) { // 토큰 검증
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContext에 인증 정보 저장
            }
        }
        catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.error("Expired JWT token", e);
            SecurityContextHolder.clearContext(); // 인증 정보 삭제
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"access_token_expired\", \"message\": \"The access token has expired\"}");
            return;
        }
        catch (RedisConnectionFailureException e) {
            SecurityContextHolder.clearContext();
            throw new BaseException("REDIS_ERROR");
        } catch (Exception e) {
            throw new BaseException("INVALID_JWT");
        }
        filterChain.doFilter(request, response);
    }
}
