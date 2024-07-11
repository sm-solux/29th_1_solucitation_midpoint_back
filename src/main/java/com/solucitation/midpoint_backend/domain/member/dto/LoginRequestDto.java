package com.solucitation.midpoint_backend.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "이메일 또는 닉네임은 필수 입력 항목입니다.")
    private String identifier;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}