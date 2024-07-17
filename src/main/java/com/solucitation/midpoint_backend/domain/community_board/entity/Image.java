package com.solucitation.midpoint_backend.domain.community_board.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "member_id"))
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id")
    private Long id;

    @Column(nullable = false, name="image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(nullable = false, name="create_date", updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name="udpate_date")
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id", nullable = true)
    private Member member;

    public Image(String image_url, Member member, LocalDateTime createDate, LocalDateTime updateDate) {
        this.imageUrl = image_url;
        this.member = member;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}