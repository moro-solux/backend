package com.example.moro.app.colormap.dto;

// 색상별 게시물 조회 응답
public record ColorPostResponse(
        Long postId,
        String imageUrl
) { }
