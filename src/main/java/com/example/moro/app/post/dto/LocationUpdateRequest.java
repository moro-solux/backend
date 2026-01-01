package com.example.moro.app.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationUpdateRequest {
    private Double lat;
    private Double lng;
}
