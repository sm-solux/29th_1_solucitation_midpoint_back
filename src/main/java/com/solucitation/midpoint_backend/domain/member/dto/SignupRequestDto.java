package com.solucitation.midpoint_backend.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 100, message = "이름은 최대 100글자까지 입력할 수 있습니다.")
    private String name;

    @NotBlank(message = "아이디는 필수항목입니다.")
    @Size(min=6, max=12, message = "아이디는 6자 이상 12자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,12}$", message = "로그인 아이디는 6~12자의 영문과 숫자만 사용 가능합니다.")
    private String loginId;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 150, message = "이메일은 최대 150글자까지 허용됩니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 100, message = "닉네임은 최소 2글자, 최대 100글자까지 허용됩니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z가-힣])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).+$", message = "비밀번호는 반드시 문자(알파벳 또는 한글), 숫자 및 특수 문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String confirmPassword;
}