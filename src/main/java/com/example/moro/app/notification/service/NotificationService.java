package com.example.moro.app.notification.service;

import com.example.moro.app.notification.dto.NotificationResponse;
import com.example.moro.app.notification.entity.Notification;
import com.example.moro.app.notification.entity.NotificationType;
import com.example.moro.app.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final FcmService fcmService;
    private final NotificationContentFactory notificationContentFactory;
    private final ObjectMapper objectMapper;



    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(n -> NotificationResponse.from(n, objectMapper))
                .toList();
    }

    @Transactional
    public void read(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림 없음"));

        notification.setRead(true);
    }

    @Transactional
    protected void notifyInternal(Long receiverId, NotificationType type, String content) {

        Notification notification = notificationRepository.save(Notification.create(receiverId, type, content));

        NotificationResponse response = NotificationResponse.from(notification, objectMapper);

        if (sseEmitterService.isConnected(receiverId)) {
            sseEmitterService.send(receiverId, response);
        } else {
            fcmService.sendPush(receiverId, type);
        }
    }

    @Transactional
    public void notifyLike(Long receiverId, Long actorId, String actorName, Long postId) {

        String content = notificationContentFactory.liked(actorId, actorName, postId);

        notifyInternal(receiverId, NotificationType.LIKED, content);
    }

    @Transactional
    public void notifyComment(Long receiverId, Long actorId, String actorName, Long postId, Long commentId, String commentPreview) {

        String content = notificationContentFactory.commented(
                actorId, actorName, postId, commentId, commentPreview
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