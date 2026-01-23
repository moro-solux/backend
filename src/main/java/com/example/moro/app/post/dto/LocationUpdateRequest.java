package com.example.moro.app.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

//위치 수정 요청
@Getter
@NoArgsConstructor
public class LocationUpdateRequest {
    private Double lat;
    private Double lng;
    private String placeName;
}
