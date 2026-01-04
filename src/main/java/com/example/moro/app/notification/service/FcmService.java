package com.example.moro.app.notification.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.notification.entity.FcmToken;
import com.example.moro.app.notification.entity.NotificationType;
import com.example.moro.app.notification.repository.FcmTokenRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
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