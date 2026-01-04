package com.example.moro.app.notification.service;

import com.example.moro.app.notification.repository.EmitterRepository;
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
        }

        emitter.onCompletion(() -> emitterRepository.delete(userId));
        emitter.onTimeout(() -> emitterRepository.delete(userId));
        emitter.onError(e -> emitterRepository.delete(userId));

        return emitter;
    }

    public void send(Long userId, Object data) {
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name("notification")
                    .data(data));
        } catch (IOException e) {
            emitterRepository.delete(userId);
        }
    }

    public boolean isConnected(Long userId) {
        return emitterRepository.exists(userId);
    }
}
