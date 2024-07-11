package com.solucitation.midpoint_backend.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenResponseDto {
    @NotNull
    private String grantType; // 토큰 타입 (예) Bearer)

    @NotNull
    private String accessToken;

    @NotNull
    private String refreshToken;
}
