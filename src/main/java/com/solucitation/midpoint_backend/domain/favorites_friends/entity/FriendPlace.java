package com.solucitation.midpoint_backend.domain.favorites_friends.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FriendPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String friendName;  // 사용자 정의 친구 이름
    private String placeTypes;
    private String address;
    private Double latitude;
    private Double longitude;
    private String name;
    private String placeID;
}
