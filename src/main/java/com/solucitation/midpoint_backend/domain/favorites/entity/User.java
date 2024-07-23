package com.solucitation.midpoint_backend.domain.favorites.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 통한 무분별한 객체 생성을 방지
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_pw", nullable = true) // 비밀번호는 null 가능
    private String password;

    @Column(name = "user_login_id", nullable = true, unique = true, length = 100) // 로그인 ID는 null 가능하지만 유니크
    private String loginId;

    @NotNull
    @Column(name = "user_name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "user_email", nullable = false, unique = true, length = 150)
    private String email;

    @NotNull
    @Column(name = "user_nickname", nullable = false, unique = true, length = 100)
    private String nickname;

    public User(String password, String name, String email, String nickname, String loginId) {
        this.password = password;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.loginId = loginId;
    }
}
