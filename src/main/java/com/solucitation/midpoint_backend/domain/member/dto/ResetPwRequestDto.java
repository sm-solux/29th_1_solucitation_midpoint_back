package com.solucitation.midpoint_backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPwRequestDto {
    private String email;

    @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인은 필수 입력 항목입니다.")
    private String newPasswordConfirm;
}
