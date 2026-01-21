package com.example.moro.app.notification.service;

import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.follow.repository.FollowRepository;
import com.example.moro.app.follow.service.FollowService;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final FcmService fcmService;
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final NotificationContentFactory notificationContentFactory;
    private final ObjectMapper objectMapper;



    @Transactional(readOnly = true)
    public Map<String, List<NotificationResponse>> getMyNotificationsGrouped(Long memberId) {

        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(memberId);

        List<NotificationResponse> responseList = notifications.stream()
                .map(n -> {
                    NotificationResponse response = NotificationResponse.from(n, objectMapper);

                    if (n.getType() == NotificationType.FOLLOWING) {
                        updateFollowStatus(memberId, response);
                    }

                    return response;
                })
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

    private void updateFollowStatus(Long currentMemberId, NotificationResponse response) {
        try {
            Map<String, Object> contentMap = (Map<String, Object>) response.getContent();

            Object actorIdObj = contentMap.get("actorId");
            Long actorId = ((Number) actorIdObj).longValue();

            Optional<Follow> followOpt = followRepository.findByFollowerIdAndFollowingId(currentMemberId, actorId);

            String currentStatus;
            if (followOpt.isEmpty()) {
                currentStatus = "NONE";
            } else if (followOpt.get().getStatus() == FollowStatus.PENDING) {
                currentStatus = "PENDING";
            } else {
                currentStatus = "ACCEPTED";
            }

            contentMap.put("followBackStatus", currentStatus);

        } catch (Exception e) {
            System.out.println("팔로우 상태 업데이트 실패: " + e.getMessage());
        }
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
    protected void notifyInternal(Long receiverId, NotificationType type, Long targetId, String content)
    {
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .receiverId(receiverId)
                        .type(type)
                        .targetId(targetId)
                        .content(content)
                        .build()
        );

        NotificationResponse response =
                NotificationResponse.from(notification, objectMapper);

        if (sseEmitterService.isConnected(receiverId)) {
            sseEmitterService.send(receiverId, response);
        } else {
            fcmService.sendPush(receiverId, type, content);
        }
    }

    @Transactional
    public void notifyLike(Long receiverId, Long actorId, String actorName, Long postId, String imageUrl) {

        String content = notificationContentFactory.liked(actorId, actorName, postId, imageUrl);

        notificationRepository
                .findByReceiverIdAndTypeAndTargetId(receiverId, NotificationType.LIKED, postId)
                .ifPresentOrElse(
                        noti -> {noti.setContent(content);},
                        () -> notifyInternal(receiverId, NotificationType.LIKED, postId, content)
                );

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

        Optional<Notification> existingOpt = notificationRepository.findByReceiverIdAndTypeAndTargetId(
                receiverId, NotificationType.FOLLOWING, actorId
        );

        if (existingOpt.isPresent()) {
            Notification existing = existingOpt.get();
            existing.setCreatedAt(LocalDateTime.now());
            return;
        }

        Optional<Follow> followOpt = followRepository.findByFollowerIdAndFollowingId(receiverId, actorId);

        String followBackStatus;

        if (followOpt.isEmpty()) {
            followBackStatus = "NONE";
        } else if (followOpt.get().getStatus() == FollowStatus.PENDING) {
            followBackStatus = "PENDING";
        } else {
            followBackStatus = "ACCEPTED";
        }

        String content = notificationContentFactory.followed(actorId, actorName, followBackStatus);

        notifyInternal(receiverId, NotificationType.FOLLOWING, actorId, content);
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