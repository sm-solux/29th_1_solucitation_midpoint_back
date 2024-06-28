package com.solucitation.midpoint_backend.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Column(name = "member_pw", nullable = false)
    private String pwd;

    @NotNull
    @Column(name = "member_name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "member_email", nullable = false, unique = true, length = 150)
    private String email;

    @NotNull
    @Column(name = "member_nickname", nullable = false, unique = true, length = 100)
    private String nickname;
}
