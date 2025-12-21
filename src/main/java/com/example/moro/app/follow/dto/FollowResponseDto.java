package com.example.moro.app.follow.dto;

import com.example.moro.app.follow.entity.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponseDto {
    private Long followId;
    private FollowStatus status;
}
