package com.solucitation.midpoint_backend.domain.history2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlaceInfoV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_info_id")
    private Long id;

    @Column(name="place_id", nullable = false)
    private String placeId;

    @ManyToOne
    @JoinColumn(name = "search_history_id", nullable = false)
    private SearchHistoryV2 searchHistory;

    @Column(name = "place_name", nullable = false)
    private String name;

    @Column(name = "place_address", nullable = false)
    private String address;

    @Column(name="place_image_url", nullable = false)
    private String imageUrl;
}
