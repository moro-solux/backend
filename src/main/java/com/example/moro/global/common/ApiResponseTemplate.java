package com.example.moro.global.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ApiResponseTemplate<T> {

    private final int status;
    private final boolean success;
    private final String message;
    private final T data;

    //성공시 응답
    public static <T> ResponseEntity<ApiResponseTemplate<T>> success(
            SuccessCode successCode,
            T data
    ) {
        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(ApiResponseTemplate.<T>builder()
                        .status(successCode.getHttpStatus().value())
                        .success(true)
                        .message(successCode.getMessage())
                        .data(data)
                        .build());
    }

    //실패시 응답 - data O
    public static ResponseEntity<ApiResponseTemplate<Void>> error(
            ErrorCode errorCode
    ) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponseTemplate.<Void>builder()
                        .status(errorCode.getHttpStatus().value())
                        .success(false)
                        .message(errorCode.getMessage())
                        .build());
    }

    //실패시 응답 - data X
    public static <T> ResponseEntity<ApiResponseTemplate<T>> error(
            ErrorCode errorCode,
            T data
    ) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponseTemplate.<T>builder()
                        .status(errorCode.getHttpStatus().value())
                        .success(false)
                        .message(errorCode.getMessage())
                        .data(data)
                        .build());
    }

}
