package com.solucitation.midpoint_backend.domain.community_board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Table(name="hashtag")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Hashtag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hashtag_id")
    private Long id;

    @Column(name="hashtag_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "hashtag", fetch = LAZY)
    private List<PostHashtag> postHashtags = new ArrayList<>();
}