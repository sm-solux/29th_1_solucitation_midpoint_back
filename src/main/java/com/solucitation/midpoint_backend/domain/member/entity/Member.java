package com.solucitation.midpoint_backend.domain.member.entity;

import com.solucitation.midpoint_backend.domain.FavFriend.entity.FavFriend;
import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavPlace> favPlaces = new HashSet<>(); // 초기화

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavFriend> favFriends = new HashSet<>(); // 초기화

    public Member(String password, String name, String email, String nickname, String loginId) {
        this.email = email;
        this.name = name;
        this.pwd = password;
        this.nickname = nickname;
        this.loginId = loginId;
    }

    public void addFavPlace(FavPlace favPlace) {
        favPlaces.add(favPlace);
        favPlace.assignMember(this);
    }

    public void removeFavPlace(FavPlace favPlace) {
        favPlaces.remove(favPlace);
        favPlace.assignMember(null);
    }

    public void addFavFriend(FavFriend favFriend) {
        favFriends.add(favFriend);
        favFriend.assignMember(this);
    }

    public void removeFavFriend(FavFriend favFriend) {
        favFriends.remove(favFriend);
        favFriend.assignMember(null);
    }

    public void assignName(String name) {
        this.name = name;
    }

    public void assignNickname(String nickname) {
        this.nickname = nickname;
    }

    public void assignPwd(String pwd) {
        this.pwd = pwd;
    }
}