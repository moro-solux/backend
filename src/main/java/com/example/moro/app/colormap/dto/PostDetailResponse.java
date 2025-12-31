package com.example.moro.app.colormap.dto;

import java.util.List;

public record PostDetailResponse(
        // 작성자 정보
        String username,
        //String profileImageUrl,

        // 게시물 정보
        Long postId,
        String imageUrl,
        Integer mainColorId,
        //String mainHexCode,
        // 사진 추출해서 뽑은 후보 색 4개
        List<ColorCandidateResponse> colorCandidates

        // 댓글
        // 공유
) {
}
