package com.solucitation.midpoint_backend.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationCodeRequestDto {
    private String email;
    private String code;
}
