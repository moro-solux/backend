package com.example.moro.app.notification.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationContentFactory {

    private final ObjectMapper objectMapper;


    public String liked(Long actorId, String actorName, Long postId, int extraCount) {
        return toJson(Map.of(
                "actorId", actorId,
                "actorName", actorName,
                "postId", postId,
                "extraCount", extraCount
        ));
    }

    public String commented(Long actorId, String actorName,
                            Long postId, Long commentId, String commentPreview) {

        return toJson(Map.of(
                "actorId", actorId,
                "actorName", actorName,
                "postId", postId,
                "commentId", commentId,
                "commentPreview", commentPreview
        ));
    }

    public String followed(Long actorId, String actorName) {
        return toJson(Map.of(
                "actorId", actorId,
                "actorName", actorName
        ));
    }

    public String mission(Long missionId, String missionTitle, int remainingHours) {
        return toJson(Map.of(
                "missionId", missionId,
                "missionTitle", missionTitle,
                "remainingHours", remainingHours
        ));
    }

    public String unlocked(String colorName, String colorHex) {
        return toJson(Map.of(
                "colorName", colorName,
                "colorHex", colorHex
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
