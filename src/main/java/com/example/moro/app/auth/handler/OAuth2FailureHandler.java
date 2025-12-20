package com.example.moro.app.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 인증 과정에서 실패했을 때 실행되는 핸들러
 * 실패 원인을 분석하여 클라이언트에게 규격화된 JSON 에러 메시지를 반환함
 */

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper; // JSON 변환을 위한 객체

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {

        // [Step 1] 에러 내용을 담을 바구니(Map) 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "OAuth2 로그인에 실패했습니다.");
        errorResponse.put("error", exception.getMessage());

        // [Step 2] HTTP 응답 헤더 설정 (401 Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // [Step 3] JSON으로 변환하여 본문에 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
