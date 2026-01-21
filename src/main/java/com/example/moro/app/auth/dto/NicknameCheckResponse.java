package com.example.moro.app.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NicknameCheckResponse {
    private boolean available;  // 사용 가능 여부
    private boolean exists; // 중복 여부
}
