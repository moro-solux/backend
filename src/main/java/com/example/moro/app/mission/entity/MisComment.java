package com.example.moro.app.mission.entity;

import com.example.moro.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MisComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long misCommentId;  // 주키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "misPostId")   // fk 연결 -> 어떤 게시물의 댓글인지
    private MissionPost missionPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member member;

    private String misContent;
    private LocalDateTime misCreatedAt;


    // 댓글 내용 수정을 위한 메서드
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }
        this.misContent = newContent;
    }
}
