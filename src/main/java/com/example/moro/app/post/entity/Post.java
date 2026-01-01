package com.example.moro.app.post.entity;

import com.example.moro.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId",updatable = false)
    private Long id; //PostId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "userId")
    private Member member;

    @Column(nullable = false)
    private Integer mainColorId;

    private String imageUrl;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    private Double lat;
    private Double lng;

    //공유 횟수를 저장할 필드가 필요하여 추가함. (혹시 이 필드 추가 때문에 꼬이지 않은지 post 부분 살필 것)
    @Column
    private int shareCount=0;
    public void increaseShareCount() {
        this.shareCount++;
    }

    @Builder
    public Post(Member member, Integer mainColorId, String imageUrl, LocalDateTime createdAt, Double lat, Double lng) {
        this.member = member;
        this.mainColorId = mainColorId;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.lat = lat;
        this.lng = lng;
        this.shareCount=0;
    }

    // 1. 좋아요 자동 삭제 (Like 엔티티 내부의 'post' 필드와 매핑)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    // 2. 댓글 자동 삭제 (Comment 엔티티 내부의 'post' 필드와 매핑)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 3. 포스트 컬러 자동 삭제 (PostColor 엔티티 내부의 'post' 필드와 매핑)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostColor> postColors = new ArrayList<>();
}
