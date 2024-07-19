package com.solucitation.midpoint_backend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberProfileResponseDto {
    private String name;
    private String nickname;
    private String email;
    private String profileImageUrl;
}
