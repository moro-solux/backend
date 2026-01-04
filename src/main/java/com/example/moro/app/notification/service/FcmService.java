package com.example.moro.app.notification.service;

import com.example.moro.app.notification.entity.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    public void sendPush(Long userId, NotificationType type) {

        // TODO (안드 연동 시)
        // 1. userId → FCM 토큰 조회
        // 2. Firebase Admin SDK로 푸시 전송
        // type = 뭐시기뭐시기
        // data payload로 보내서
        // 앱에서 문구 생성해도록 해야할 듯/.//

        System.out.println("💭FCM PUSH → user=" + userId + ", type=" + type);
    }
}