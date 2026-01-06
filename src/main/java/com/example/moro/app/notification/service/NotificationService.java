package com.example.moro.app.notification.service;

import com.example.moro.app.notification.dto.NotificationResponse;
import com.example.moro.app.notification.entity.Notification;
import com.example.moro.app.notification.entity.NotificationType;
import com.example.moro.app.notification.repository.NotificationRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final FcmService fcmService;
    private final NotificationContentFactory notificationContentFactory;
    private final ObjectMapper objectMapper;



    @Transactional(readOnly = true)
    public Map<String, List<NotificationResponse>> getMyNotificationsGrouped(Long memberId) {

        List<Notification> notifications = notificationRepository.findByReceiverId(memberId);
        ObjectMapper om = new ObjectMapper();

        List<NotificationResponse> responseList = notifications.stream()
                .map(n -> NotificationResponse.from(n, om))
                .toList();

        Map<String, List<NotificationResponse>> grouped = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            NotificationResponse r = responseList.get(i);

            long daysDiff = java.time.Duration.between(n.getCreatedAt().toLocalDate().atStartOfDay(), now.toLocalDate().atStartOfDay()).toDays();
            String key;
            if (daysDiff == 0) key = "Today";
            else if (daysDiff == 1) key = "Yesterday";
            else if (daysDiff <= 7) key = "Last 7 days";
            else key = "Earlier";

            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        return grouped;
    }

    @Transactional
    public void read(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 알림을 찾을 수 없습니다."));

        notification.setRead(true);
    }

    @Transactional
    protected void notifyInternal(Long receiverId, NotificationType type, String content) {

        Notification notification = notificationRepository.save(Notification.create(receiverId, type, content));

        NotificationResponse response = NotificationResponse.from(notification, objectMapper);

        if (sseEmitterService.isConnected(receiverId)) {
            sseEmitterService.send(receiverId, response);
        } else {
            fcmService.sendPush(receiverId, type, content);
        }
    }

    @Transactional
    public void notifyLike(Long receiverId, Long actorId, String actorName, Long postId, String imageUrl) {

        String content = notificationContentFactory.liked(actorId, actorName, postId, imageUrl);

        notifyInternal(receiverId, NotificationType.LIKED, content);
    }

    @Transactional
    public void notifyComment(Long receiverId, Long actorId, String actorName, String targetType, Long postId, Long commentId, String commentPreview) {

        String content = notificationContentFactory.commented(
                actorId, actorName, targetType, postId, commentId, commentPreview
        );

        notifyInternal(receiverId, NotificationType.COMMENT, content);
    }

    @Transactional
    public void notifyFollow(Long receiverId, Long actorId, String actorName) {

        String content = notificationContentFactory.followed(actorId, actorName);

        notifyInternal(receiverId, NotificationType.FOLLOWING, content);
    }

    @Transactional
    public void notifyMission(Long receiverId, Long missionId, String missionTitle, int remainingHours) {

        String content = notificationContentFactory.mission( missionId, missionTitle, remainingHours);

        notifyInternal(receiverId, NotificationType.MISSION, content);
    }

    @Transactional
    public void notifyColorUnlocked(Long receiverId) {
        notifyInternal(receiverId, NotificationType.COLOR_UNLOCKED, null);
    }
}