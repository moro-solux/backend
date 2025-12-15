package com.example.moro.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    //기본적인 성공 응답
    OPERATION_SUCCESSFUL(HttpStatus.OK, "작업이 성공적으로 처리되었습니다."),

    // 리소스 관련
    RESOURCE_CREATED(HttpStatus.CREATED, "리소스가 성공적으로 생성되었습니다."),
    RESOURCE_UPDATED(HttpStatus.OK, "리소스가 성공적으로 업데이트되었습니다."),
    RESOURCE_DELETED(HttpStatus.NO_CONTENT, "리소스가 성공적으로 삭제되었습니다."),
    RESOURCE_RETRIEVED(HttpStatus.OK, "리소스가 성공적으로 조회되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
