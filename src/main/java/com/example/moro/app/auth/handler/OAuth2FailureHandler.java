package com.example.moro.app.auth.handler;

import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // [Step 1] 컨벤션 적용
                var apiResponse = ApiResponseTemplate.error(
                ErrorCode.UNAUTHORIZED_EXCEPTION,
                exception.getMessage() // 구체적인 실패 이유를 함께 전달
        );

        // [Step 2] 응답 설정 및 출력
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // [Step 3] 템플릿 객체를 JSON 문자열로 변환하여 전송
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}