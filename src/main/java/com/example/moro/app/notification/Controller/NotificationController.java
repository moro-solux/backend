package com.example.moro.app.notification.Controller;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.notification.dto.NotificationResponse;
import com.example.moro.app.notification.entity.NotificationType;
import com.example.moro.app.notification.service.NotificationService;
import com.example.moro.app.notification.service.SseEmitterService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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
    public List<NotificationResponse> getMyNotifications() {
        Member me = getCurrentMember();
        return notificationService.getMyNotifications(me.getId());
    }


    @PutMapping("/{notificationId}/read")
    public void readNotification(@PathVariable Long notificationId) {
        notificationService.read(notificationId);
    }

    @PostMapping("/test")
    public void testNotification() {
        Member me = getCurrentMember();
        notificationService.notifyLike(me.getId(),1L,"모로",10L,0);
    }
}
