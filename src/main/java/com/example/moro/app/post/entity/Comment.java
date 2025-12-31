package com.example.moro.app.post.entity;

import com.example.moro.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmmtId")
    private Long id;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    //erd에 없지만 필요해서 작성자 id (사용자id) 추가함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member member;

    // 댓글 작성 시간도 와이어 프레임에 구현되어 있어 erd 에 없지만 필드 추가함.
    private LocalDateTime createdAt;

    //와이어 프레임 보니까 사용자 이미지도 들어가야할 거 같은데 member 엔티티가 수정이 되면 추가하기!!!
}
