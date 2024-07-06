package com.solucitation.midpoint_backend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String name;
    private String email;
    private String nickname;
    private String password;
}