package com.example.moro.app.follow.dto;

import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowUserResponse {
    private Long followId;
    private FollowStatus status;

    private Long memberId;
    private String userName;

    public static FollowUserResponse fromFollower(Follow follow) {
        return FollowUserResponse.builder()
                .followId(follow.getFollowId())
                .status(follow.getStatus())
                .memberId(follow.getFollower().getId())
                .userName(follow.getFollower().getUserName())
                .build();
    }

    public static FollowUserResponse fromFollowing(Follow follow) {
        return FollowUserResponse.builder()
                .followId(follow.getFollowId())
                .status(follow.getStatus())
                .memberId(follow.getFollowing().getId())
                .userName(follow.getFollowing().getUserName())
                .build();
    }
}
