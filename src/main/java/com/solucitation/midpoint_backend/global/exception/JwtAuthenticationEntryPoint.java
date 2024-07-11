package com.solucitation.midpoint_backend.global.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 처리기 - 인증되지 않은 접근에 대해 401 응답 반환
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Throwable cause = authException.getCause();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (cause instanceof ExpiredJwtException) { // 만료된 토큰
            response.getWriter().write("{\"error\": \"access_token_expired\", \"message\": \"Access Token이 만료되었습니다.\"}");
        } else { // 유효하지 않은 토큰
            response.getWriter().write("{\"error\": \"invalid_token\", \"message\": \"유효하지 않은 Access Token입니다.\"}");
        }
    }
}
