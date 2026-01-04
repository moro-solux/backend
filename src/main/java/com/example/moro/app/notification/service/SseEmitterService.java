package com.example.moro.app.notification.service;

import com.example.moro.app.notification.repository.EmitterRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(userId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            emitterRepository.delete(userId);
            throw new BusinessException(ErrorCode.SSE_CONNECTION_ERROR, "SSE 구독 중 연결 실패: " + e.getMessage());

        }

        emitter.onCompletion(() -> emitterRepository.delete(userId));
        emitter.onTimeout(() -> emitterRepository.delete(userId));
        emitter.onError(e -> emitterRepository.delete(userId));

        return emitter;
    }

    public void send(Long userId, Object data) {
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter == null) {
            throw new BusinessException(ErrorCode.SSE_NOT_CONNECTED, "해당 유저는 SSE에 연결되어 있지 않습니다.");

        }

        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name("notification")
                    .data(data));
        } catch (IOException e) {
            emitterRepository.delete(userId);
            throw new BusinessException(ErrorCode.SSE_SEND_ERROR, "SSE 전송 중 오류 발생: " + e.getMessage());

        }
    }

    public boolean isConnected(Long userId) {
        return emitterRepository.exists(userId);
    }
}
