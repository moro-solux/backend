package com.example.moro.app.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 사진 촬영 요청
@Getter
@NoArgsConstructor
public class CaptureRequest {
    private String imageUrl;
    private Double lat;
    private Double lng;

    public CaptureRequest(String imageUrl, Double lat, Double lng) {
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
    }
}
