package com.solucitation.midpoint_backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordVerifyRequestDto {
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}