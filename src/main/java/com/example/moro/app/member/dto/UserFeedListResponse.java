package com.example.moro.app.member.dto;

import com.example.moro.global.common.dto.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserFeedListResponse {
    private ProfileFeedType viewType;
    private PageResponse<UserFeedResponse> page;
}
