package com.example.moro.app.notification.Controller;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.notification.dto.NotificationResponse;
import com.example.moro.app.notification.service.NotificationService;
import com.example.moro.app.notification.service.SseEmitterService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

import static com.example.moro.global.util.SecurityUtil.getCurrentMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;


    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");


        Member me = getCurrentMember();
        return sseEmitterService.subscribe(me.getId());
    }


//    @PostMapping("/push")
//    public void pushNotification(@RequestParam Long receiverId, @RequestParam NotificationType type) {
//        notificationService.notify(receiverId, type);
//    }


    @GetMapping
    public ResponseEntity<ApiResponseTemplate<Map<String, List<NotificationResponse>>>> getMyNotifications() {
        Member me = getCurrentMember();

        Map<String, List<NotificationResponse>> groupedNotifications = notificationService.getMyNotificationsGrouped(me.getId());

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, groupedNotifications);
    }


    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponseTemplate<Void>> readNotification(@PathVariable Long notificationId) {
        notificationService.read(notificationId);
        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, null);
    }

}
