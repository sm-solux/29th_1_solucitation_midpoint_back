package com.solucitation.midpoint_backend.domain.favorites.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite_places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FavoritePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_place_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "place_name", nullable = false, length = 255)
    private String placeName;

    @Column(name = "place_location", nullable = false, length = 255)
    private String placeLocation; // 예: 주소, 좌표 등
}
