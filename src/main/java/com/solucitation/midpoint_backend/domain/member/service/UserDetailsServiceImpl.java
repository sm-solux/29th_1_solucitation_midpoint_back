package com.solucitation.midpoint_backend.domain.member.service;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 사용자 정의 UserDetailsService 구현체 - 이메일로 사용자 로드
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmailOrNickname(identifier, identifier)
                .orElseThrow(() -> {
                    log.error("Invalid email or nickname: " + identifier);
                    return new UsernameNotFoundException("Invalid email or nickname: " + identifier);
                });

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        return new org.springframework.security.core.userdetails.User(member.getEmail(), member.getPwd() != null ? member.getPwd() : "", grantedAuthorities);
    }
}
