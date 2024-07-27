package com.solucitation.midpoint_backend.global.exception;

import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 처리기 - 인증되지 않은 접근에 대해 401 응답 반환
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationEntryPoint(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Throwable cause = authException.getCause();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (cause instanceof ExpiredJwtException expiredException) { // 만료된 토큰
            boolean isRefreshToken = false;
            try {
                Claims claims = expiredException.getClaims();
                if (claims != null) {
                    isRefreshToken = "refresh".equals(claims.get("type"));
//                    log.info("isRefreshToken은?" + isRefreshToken);
                }
            } catch (Exception ex) {
                log.error("Error while checking if token is refresh token", ex);
            }
            if (isRefreshToken) {
                response.getWriter().write("{\"error\": \"refresh_token_expired\", \"message\": \"Refresh Token이 만료되었습니다.\"}");
            } else {
                response.getWriter().write("{\"error\": \"access_token_expired\", \"message\": \"Access Token이 만료되었습니다.\"}");
            }
        } else { // 유효하지 않은 토큰
            response.getWriter().write("{\"error\": \"invalid_token\", \"message\": \"유효하지 않은 Access Token입니다.\"}");
        }
    }
}
