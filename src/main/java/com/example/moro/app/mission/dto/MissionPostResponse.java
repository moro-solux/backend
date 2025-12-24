package com.example.moro.app.mission.dto;

import com.example.moro.app.mission.entity.MissionPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MissionPostResponse {
    private Long misPostId;
    private String missionTitle;  // 어떤 미션인지
    private String userName;  // 작성자 이름
    private String imageUrl;   // 이미지 경로
    private String detail;    // 글 내용?
    private Double lat;
    private Double lon;
    private LocalDateTime createdAt;

    // entity를 dto로 변환하는 메서드
    public static MissionPostResponse from(MissionPost post) {
        return MissionPostResponse.builder()
                .misPostId(post.getMisPostId())
                .missionTitle(post.getMission().getMissionTitle())
                .userName(post.getMember().getUserName())
                .detail(post.getDetail())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
