package com.example.moro.app.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 인증 및 권한 부여를 담당하는 컨트롤러
 * 소셜 로그인 관련 API를 처리함
 */

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Operation(
            summary = "구글 로그인",
            description = "구글 OAuth2 로그인을 시작합니다. 이 엔드포인트는 구글 로그인 페이지로 리다이렉트됩니다."
    )
    @GetMapping("/login/google")
    public RedirectView loginGoogle() {
        // 구글 OAuth2 인증 페이지로 리다이렉트
        return new RedirectView("/oauth2/authorization/google");
    }
}