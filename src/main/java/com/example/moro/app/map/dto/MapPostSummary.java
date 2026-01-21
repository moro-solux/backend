package com.example.moro.app.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MapPostSummary {
    private Long postId;
    private Double lat;
    private Double lng;
}
