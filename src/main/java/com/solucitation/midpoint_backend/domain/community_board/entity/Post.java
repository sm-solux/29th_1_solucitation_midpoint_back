package com.solucitation.midpoint_backend.domain.community_board.entity;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name="post")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id; // 게시글 번호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime create_date;

    @LastModifiedDate
    private LocalDateTime update_date; 

    @OneToMany(mappedBy = "post")
    private List<Post_hashtag> postHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Image> images = new ArrayList<>();
}
