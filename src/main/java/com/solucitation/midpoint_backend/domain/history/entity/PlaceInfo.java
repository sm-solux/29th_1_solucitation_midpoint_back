package com.solucitation.midpoint_backend.domain.history.entity;

import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlaceInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="palce_info_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="search_history_id", nullable = false)
    private Post post;

    @Column(name="place_url")
    private String url;
}
