package com.example.moro.app.colormap.dto;

// 변경 결과 응답
public record UpdateMainColorResponse(
        Long postId,
        Integer updatedMainColorId,
        String updatedHexCode,
        Boolean isUnlocked
) {
}
