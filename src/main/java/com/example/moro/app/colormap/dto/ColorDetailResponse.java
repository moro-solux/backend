package com.example.moro.app.colormap.dto;

// 전체 조회 시 개별 색상 정보
public record ColorDetailResponse(
        Long colorId,
        String hexCode,
        Integer postCount,
        Boolean unlocked,
        Boolean isRepresentative
) {
}