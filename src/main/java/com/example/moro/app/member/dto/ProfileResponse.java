package com.example.moro.app.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private Long userId;
    private String userName;
    private String userColorHex;
    private int colorCount;
    private int postCount;
    private int followingCount;
    private boolean isCurrentUser;

    private List<String> colorCodes;

}
