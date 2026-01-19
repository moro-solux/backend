package com.example.moro.app.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MapPostDetailResponse {
    private Long postId;

    private String createdAt;

    private String imageUrl;

    private String placeName;
    private String addressKo;
    private String addressEn;

    private String hexCode1;
    private String hexCode2;
    private String hexCode3;
    private String hexCode4;
}
