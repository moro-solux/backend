package com.example.moro.app.sample;

import com.example.moro.global.exception.BusinessException;
import org.springframework.stereotype.Service;

import static com.example.moro.global.common.ErrorCode.BAD_REQUEST;

@Service
public class SampleService {

    public String getHelloMessage() {
        return "Swagger 테스트용 GET 요청 성공! (Service 호출)";
    }

    public String echoMessage(SampleRequestDTO request) {
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            throw new BusinessException(BAD_REQUEST, "메시지가 비어있습니다!");
        }
        return "Swagger 테스트용 POST 요청: " + request.getMessage() + " (Service 호출)";
    }
}