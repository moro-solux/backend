package com.example.moro.app.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
    private Long userId;
    private String userName;
    private String userColorHex;
    private String followingStatus;
    private int colorCount;
    private int followerCount;
    private int followingCount;
    private boolean isCurrentUser;
    private boolean isVisible;

    private List<UserColor> colorCodes;

}
