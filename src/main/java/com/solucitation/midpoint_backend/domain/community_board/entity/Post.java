package com.solucitation.midpoint_backend.domain.community_board.entity;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id; // 게시글 번호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @CreatedDate
    @Column(name="create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name="update_date")
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "post", fetch= LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags;

    @OneToMany(mappedBy = "post", fetch= LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    public Post(Member member, String title, String content, LocalDateTime createDate, LocalDateTime updateDate) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public void addPostHashtag(PostHashtag postHashtag) {
        if (postHashtags == null) postHashtags =  new ArrayList<>();
        postHashtags.add(postHashtag);
        postHashtag.setPost(this);
    }

    public void addImage(Image image) {
        if (images == null) images = new ArrayList<>();
        images.add(image);
        image.setPost(this);
    }
}
