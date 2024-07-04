package com.solucitation.midpoint_backend.domain.email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationCodeRequestDto {
    private String email;
    private String code;
}
