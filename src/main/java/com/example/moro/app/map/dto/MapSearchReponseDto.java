package com.example.moro.app.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MapSearchReponseDto {
    private Long postId;
    private String imageUrl;
    private Double lat;
    private Double lng;
    private String address;
}
