package com.solucitation.midpoint_backend.domain.history.entity;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "search_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SearchHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="serach_history_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    @CreatedDate
    @Column(name="search_date", nullable = false, updatable = false)
    private LocalDateTime searchDate;

    @OneToMany(mappedBy = "search_history", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceInfo> placeList;
}
