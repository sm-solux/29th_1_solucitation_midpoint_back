package com.solucitation.midpoint_backend.domain.member.dto;

import lombok.Getter;

@Getter
public class PwResetRequestDto {
    private String name;
    private String email;
}