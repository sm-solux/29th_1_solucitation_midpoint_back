package com.solucitation.midpoint_backend.domain.community_board.entity;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id")
    private Long id; // 이미지 아이디

    @Column(nullable = false)
    private String image_url; // 이미지 URL

    @Lob
    private String description; // 이미지 부가 설명

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime create_date; // 게시 날짜

    @LastModifiedDate
    private LocalDateTime update_date; // 수정 날짜

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    public Image(String image_url, Member member, LocalDateTime create_date) {
        this.image_url = image_url;
        this.member = member;
        this.create_date = create_date;
    }
}
