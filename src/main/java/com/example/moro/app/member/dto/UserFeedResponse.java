package com.example.moro.app.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserFeedResponse {
    private Long postId;
    private String imageUrl;
}
