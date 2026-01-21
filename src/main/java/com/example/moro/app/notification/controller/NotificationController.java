package com.example.moro.app.notification.controller;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.notification.dto.FcmTokenRequest;
import com.example.moro.app.notification.dto.NotificationResponse;
import com.example.moro.app.notification.entity.NotificationType;
import com.example.moro.app.notification.service.FcmService;
import com.example.moro.app.notification.service.NotificationService;
import com.example.moro.app.notification.service.SseEmitterService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import com.example.moro.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;


@Tag(name = "Notifications", description = "알림(FCM, SSE) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;
    private final FcmService fcmService;
    private final SecurityUtil securityUtil;


    @Operation(summary = "알림 스트림 연결 (SSE)", description = "서버로부터 실시간 알림을 받기 위해 SSE 연결을 요청합니다. (EventStream)")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal Member member, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        return sseEmitterService.subscribe(member.getId());
    }

    @Operation(summary = "FCM 토큰 등록", description = "앱 로그인 시 발급받은 FCM 기기 토큰을 서버에 저장합니다.")
    @PostMapping("/token")
    public ResponseEntity<ApiResponseTemplate<Void>> registerFcmToken(@RequestBody FcmTokenRequest request) {
        Member me = securityUtil.getCurrentMember();
        fcmService.registerToken(me.getId(), request.getFcmToken());
        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, null);
    }

    @Operation(summary = "FCM 토큰 삭제", description = "앱 로그아웃 시 서버에 저장된 FCM 토큰을 삭제합니다.")
    @DeleteMapping("/token")
    public ResponseEntity<ApiResponseTemplate<Void>> deleteFcmToken(@RequestParam String token) {
        fcmService.deleteToken(token);
        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, null);
    }


//    @PostMapping("/push")
//    public void pushNotification(@RequestParam Long receiverId, @RequestParam NotificationType type) {
//        notificationService.notify(receiverId, type);
//    }


    @Operation(summary = "알림 목록 조회", description = "나에게 도착한 알림 목록을 그룹화(오늘, 어제 등)하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponseTemplate<Map<String, List<NotificationResponse>>>> getMyNotifications() {
        Member me = securityUtil.getCurrentMember();

        Map<String, List<NotificationResponse>> groupedNotifications = notificationService.getMyNotificationsGrouped(me.getId());

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, groupedNotifications);
    }


    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 클릭하여 읽음 상태로 변경합니다.")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponseTemplate<Void>> readNotification(@PathVariable Long notificationId) {
        notificationService.read(notificationId);
        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, null);
    }

    /*
    @GetMapping("/test-fcm")
    public ResponseEntity<ApiResponseTemplate<String>> testFcmPush(
            @RequestParam Long userId,
            @RequestParam NotificationType type,
            @RequestParam String content
    ) {
        fcmService.sendPush(userId, type, content);
        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL,
                "FCM 테스트 전송 완료! 서버 로그 확인하세요.");
    }
`*/


}
