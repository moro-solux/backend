package com.example.moro.global.common;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 기본 에러
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾지 못했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다."),
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, "해당 리소스에 대한 접근 권한이 없습니다."),
    ALREADY_EXIST_SUBJECT_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서버가 일시적으로 사용 불가 상태입니다."),

    //s3 에러
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제에 실패했습니다."),
    S3_ACCESS_DENIED(HttpStatus.FORBIDDEN, "S3 접근 권한이 없습니다."),
    S3_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "S3 서버와의 통신에 실패했습니다.");



    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

