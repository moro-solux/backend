package com.example.moro.app.auth.controller;

import com.example.moro.app.auth.dto.LoginRequest;
import com.example.moro.app.auth.dto.LoginResponse;
import com.example.moro.app.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 및 권한 부여를 담당하는 컨트롤러
 * 소셜 로그인 및 토큰 발급관련 api를 처리함
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 구글 인증 성공 후 로그인을 처리하는 엔드포인트
     * 프론트로부터 받은 구글 사용자 정보를 바탕으로 회원가입 또는 로그인을 수행하고 jwt를 반환함.
     * @param request 구글에서 전달받은 사용자 정보 (이메일, 이름 등)을 담은 객체
     * @return 발급된 jwt 토큰 및 회원 정보를 포함한 LoginResponse
     */

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        //비즈니스 로직 (회원 확인 및 토큰 생성)은 AuthService에 위임함.
        LoginResponse response = authService.login(request.getEmail(), request.getName());
        return ResponseEntity.ok(response);
    }
}