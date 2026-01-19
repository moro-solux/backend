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

    //memberId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "userId")
    private Member member;

    //mainColorId
    @Column(nullable = false)
    @Setter
    private Integer mainColorId;

    //ImageUrl
    private String imageUrl;

    //생성 시간
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    //위경도
    @Setter
    private Double lat;
    @Setter
    private Double lng;

    // 주소 정보 (nullable)
    @Column(nullable = true)
    @Setter
    private String address;

    @Column
    @Setter
    private String detailAddressKo;

    @Column
    @Setter
    private String detailAddressEn;

    //공유 횟수를 저장할 필드가 필요하여 추가함.
    @Column
    @Setter
    private int shareCount=0;
    public void increaseShareCount() {
        this.shareCount++;
    }

    // ===== 게시물 상태 (임시/최종) =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.DRAFT;

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

    // ===== 상태 관련 메서드들 =====
    public void publish() {
        this.status = PostStatus.PUBLISHED;
    }

    public boolean isDraft() {
        return this.status == PostStatus.DRAFT;
    }

    public boolean isPublished() {
        return this.status == PostStatus.PUBLISHED;
    }

    public enum PostStatus {
        DRAFT,      // 임시 상태 (미리보기)
        PUBLISHED   // 최종 게시물
    }
}
