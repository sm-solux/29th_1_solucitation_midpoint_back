package com.solucitation.midpoint_backend.domain.email.dto;

import lombok.Getter;

@Getter
public class PwResetRequestDto {
    private String name;
    private String email;
}