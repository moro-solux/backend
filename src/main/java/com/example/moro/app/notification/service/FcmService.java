package com.example.moro.app.notification.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.notification.entity.FcmToken;
import com.example.moro.app.notification.entity.NotificationType;
import com.example.moro.app.notification.repository.FcmTokenRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public void registerToken(Long memberId, String token) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 회원을 찾을 수 없습니다."));

        fcmTokenRepository.deleteByFcmToken(token);

        FcmToken fcmToken = FcmToken.builder()
                .member(member)
                .fcmToken(token)
                .createdAt(LocalDateTime.now())
                .build();
        fcmTokenRepository.save(fcmToken);
    }

    public List<String> getTokens(Long memberId) {
        return fcmTokenRepository.findAllByMemberId(memberId)
                .stream()
                .map(FcmToken::getFcmToken)
                .toList();
    }

    @Transactional
    public void deleteToken(String token) {
        fcmTokenRepository.deleteByFcmToken(token);
    }


    public void sendPush(Long userId, NotificationType type, String contentJson) {
//         안드 연동시에 확인...
//         1. userId → FCM 토큰 조회
//         2. Firebase Admin SDK로 푸시 전송
//         type = 뭐시기뭐시기
//         data payload로 보내서
//         앱에서 문구 생성해도록 해야할 듯/.//
        List<String> tokens = getTokens(userId);
        if(tokens.isEmpty()) {
            System.out.println("⚠️ 해당 유저는 등록된 FCM 토큰이 없습니다.");
            return;
        }
        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle("Moro")
                            .setBody("Moro에서 새 알림이 도착했습니다. 확인해보세요!")
                            .build())
                    .putData("type", type.name())
                    .putData("payload", contentJson)
                    .build();

            try {
                String response = firebaseMessaging.send(message);
                System.out.println("FCM 전송 성공: " + response);
            } catch (FirebaseMessagingException e) {
                System.err.println("FCM 전송 실패: " + e.getMessage());
            }
        }

        System.out.println("💭FCM PUSH → user=" + userId + ", type=" + type + ", content=" + contentJson);
    }

//    public void sendPushTest(Long userId, NotificationType type, String content) {
//        String dummyToken = "TEST_FAKE_TOKEN_12345";
//
//        System.out.println("💭 FCM 테스트 시작 → user=" + userId + ", type=" + type + ", content=" + content);
//
//        Message message = Message.builder()
//                .setToken(dummyToken)
//                .setNotification(com.google.firebase.messaging.Notification.builder()
//                        .setTitle("Moro 테스트 알림")
//                        .setBody(content)
//                        .build())
//                .putData("type", type.name())
//                .build();
//
//        try {
//            firebaseMessaging.send(message);
//            System.out.println("✅ FCM 테스트 전송 시도 완료!");
//        } catch (FirebaseMessagingException e) {
//            System.out.println("⚠️ FCM 테스트 전송 실패 (정상적인 테스트): " + e.getMessage());
//        }
//
//    }


}