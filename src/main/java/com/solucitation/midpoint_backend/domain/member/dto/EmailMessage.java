package com.solucitation.midpoint_backend.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailMessage {
    private String to; // 받는 사람
    private String subject; // 메일 제목
    private String message; // 메일 본문
}
