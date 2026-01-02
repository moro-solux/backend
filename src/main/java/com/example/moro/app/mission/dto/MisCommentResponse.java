package com.example.moro.app.mission.dto;

import java.time.LocalDateTime;

public record MisCommentResponse(
        Long misCommentId,
        String misContent,  // 댓글 내용
        String username,         // 댓글 작성자
        // 댓글 프로필?
        LocalDateTime misCreatedAt// 댓글 작성 시간
) {
}
