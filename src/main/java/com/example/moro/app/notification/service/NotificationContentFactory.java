package com.example.moro.app.notification.service;


import com.example.moro.app.follow.entity.FollowStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationContentFactory {

    private final ObjectMapper objectMapper;


    public String liked(Long actorId, String actorName, Long postId, String imageUrl) {
        return toJson(Map.of(
                "actorId", actorId,
                "actorName", actorName,
                "postId", postId,
                "imageUrl", imageUrl
        ));
    }

    public String commented(Long actorId, String actorName, String targetType,
                            Long postId, Long commentId, String commentPreview) {

        return toJson(Map.of(
                "actorId", actorId,
                "actorName", actorName,
                "targetType", targetType,
                "postId", postId,
                "commentId", commentId,
                "commentPreview", commentPreview
        ));
    }

    public String followed(Long actorId, String actorName, String followBackStatus) {
        return toJson(Map.of(
                "actorId", actorId,
                "actorName", actorName,
                "followBackStatus", followBackStatus
        ));
    }

    public String mission(Long missionId, String missionTitle, int remainingHours) {
        return toJson(Map.of(
                "missionId", missionId,
                "missionTitle", missionTitle,
                "remainingHours", remainingHours
        ));
    }


    private String toJson(Map<String, Object> content) {
        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("알림 content JSON 변환 실패", e);
        }
    }
}
