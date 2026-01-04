package com.example.moro.app.notification.dto;

import com.example.moro.app.notification.entity.Notification;
import com.example.moro.app.notification.entity.NotificationType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private Map<String, Object> content;
    private boolean read;
    private String createdAt;

    public static NotificationResponse from(Notification n, ObjectMapper om) {
        try {
            return NotificationResponse.builder()
                    .id(n.getNotiId())
                    .type(n.getType())
                    .content(
                            n.getContent() == null
                                    ? null
                                    : om.readValue(n.getContent(),new TypeReference<Map<String, Object>>() {})
                    )
                    .read(n.isRead())
                    .createdAt(formatTime(n.getCreatedAt()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("알림 DTO 변환 실패", e);
        }
    }

    private static String formatTime(LocalDateTime time) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("a hh:mm", Locale.ENGLISH);
        return time.format(formatter);
    }
}
