package com.example.moro.app.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private Integer mainColorId;
    private String imageUrl;
    private Double lat;
    private Double lng;
}
