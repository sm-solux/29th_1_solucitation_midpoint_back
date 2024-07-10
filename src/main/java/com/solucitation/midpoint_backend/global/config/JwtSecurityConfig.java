//package com.solucitation.midpoint_backend.global.config;
//
//import com.solucitation.midpoint_backend.global.auth.JwtFilter;
//import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.DefaultSecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// * JWT 보안 설정 클래스 - JwtTokenProvider와 JwtFilter를 HttpSecurity에 추가
// */
//@RequiredArgsConstructor
//public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        JwtFilter customFilter = new JwtFilter(jwtTokenProvider);
//        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
//    }
//}
