package com.example.moro.app.colormap.dto;

import java.util.List;

// 테마 별 색 조회
public record ThemeGroupResponse(
        String themeName,
        List<ColorDetailResponse> colors
){}