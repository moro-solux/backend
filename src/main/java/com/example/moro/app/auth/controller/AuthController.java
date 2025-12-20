package com.example.moro.app.auth.controller;

import com.example.moro.app.auth.dto.LoginRequest;
import com.example.moro.app.auth.dto.LoginResponse;
import com.example.moro.app.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 구글 로그인 요청 처리
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getEmail(), request.getName());
        return ResponseEntity.ok(response);
    }
}