package com.example.moro.app.auth.handler;

import com.example.moro.app.auth.dto.LoginResponse;
import com.example.moro.app.auth.service.AuthService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
/**
 * OAuth2 로그인 성공 시 실행되는 핸들러
 * 구글로부터 받은 사용자 정보를 우리 서비스의 회원 시스템과 연결하고 JWT를 발급함
 */

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // [Step 1] 구글 사용자 정보 추출
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // [Step 2] 로그인 처리 및 데이터 생성
        LoginResponse loginResponse = authService.login(email, name);

        // [Step 3] 컨벤션 적용
        var apiResponse = ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, loginResponse);

        // [Step 4] 공통 규격으로 설정된 JSON 응답 내보내기
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}