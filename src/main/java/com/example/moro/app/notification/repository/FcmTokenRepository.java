package com.example.moro.app.notification.repository;

import com.example.moro.app.notification.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    void deleteByFcmToken(String fcmToken);
    List<FcmToken> findAllByMemberId(Long memberId);

}
