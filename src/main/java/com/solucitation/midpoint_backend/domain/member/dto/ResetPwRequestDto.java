package com.solucitation.midpoint_backend.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPwRequestDto {
//    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
//    @Email(message = "유효한 이메일 형식이 아닙니다.")
//    private String email;

    @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인은 필수 입력 항목입니다.")
    private String newPasswordConfirm;
}
