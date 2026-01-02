package com.example.moro.app.mission.dto;

// 댓글 생성 시 사용
public record MisCommentRequest(
        Long misPostId,
        String misContent
) {
}
