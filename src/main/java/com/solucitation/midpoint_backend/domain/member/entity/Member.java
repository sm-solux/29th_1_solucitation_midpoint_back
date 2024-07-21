package com.solucitation.midpoint_backend.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 통한 무분별한 객체 생성을 방지
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_pw", nullable = true) // pwd는 null 가능
    private String pwd;

    @Column(name = "member_login_id", nullable = true, unique = true, length = 100) // loginId는 null 가능하지만 유니크
    private String loginId;
    
    @NotNull
    @Column(name = "member_name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "member_email", nullable = false, unique = true, length = 150)
    private String email;

    @NotNull
    @Column(name = "member_nickname", nullable = false, unique = true, length = 100)
    private String nickname;

    public Member(String password, String name, String email, String nickname) {
        this.email= email;
        this.name = name;
        this.pwd = password;
        this.nickname = nickname;
    }
}
