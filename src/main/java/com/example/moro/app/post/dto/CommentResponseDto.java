package com.example.moro.app.post.dto;

import com.example.moro.app.post.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String username;   // 작성자 이름
    private LocalDateTime createdAt; // 프론트에서 변환할 원본 시간 데이터

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUserName(); // 연관된 멤버에서 가져옴
        this.createdAt = comment.getCreatedAt();

    }
}

    // 유저 엔티티에 사용자 사진 추가되면 여기에도 추가해줘야 함!!!!!!!!!