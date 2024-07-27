package com.solucitation.midpoint_backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequestDto {
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 100, message = "이름은 최대 100글자까지 입력할 수 있습니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 100, message = "닉네임은 최소 2글자, 최대 100글자까지 허용됩니다.")
    private String nickname;

    @NotNull(message = "기본 이미지 여부는 필수 입력 항목입니다.")
    private Boolean useDefaultImage;
}
