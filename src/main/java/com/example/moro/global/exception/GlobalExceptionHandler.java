package com.example.moro.global.exception;

import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        return ApiResponseTemplate.error(ErrorCode.UNAUTHORIZED_EXCEPTION, "아이디 또는 비밀번호가 올바르지 않습니다.");
    }


    // 400 Bad Request 처리 ( 클라이언트애서 잘못된 JSON 형식으로 데이터가 온경우)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("잘못된 JSON 형식 요청", e);
        return ApiResponseTemplate.error(ErrorCode.BAD_REQUEST, "잘못된 요청 형식입니다: " + e.getMessage());
    }

    // 400 Bad Request 처리 (비즈니스 로직 상 유효하지 않은 값, 범위 초과된 데이터가 전달 되었을 경우)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("잘못된 인자 값", e);
        return ApiResponseTemplate.error(ErrorCode.BAD_REQUEST, "잘못된 인자 값입니다: " + e.getMessage());
    }

    // 여러 에러 응담 코드 처리
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleResponseStatusException(ResponseStatusException e) {
        HttpStatus status = (HttpStatus) e.getStatusCode();  // ResponseStatusException에서 HttpStatus 추출
        // 상태 코드에 맞는 ErrorCode를 매핑
        ErrorCode errorCode = mapHttpStatusToErrorCode(status);

        return ApiResponseTemplate.error(errorCode, e.getReason());
    }

    // 400, 404 등 특정 요청 파라미터 누락시 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleMissingParamException(MissingServletRequestParameterException e) {
        logger.error("누락된 요청 파라미터", e);
        return ApiResponseTemplate.error(ErrorCode.BAD_REQUEST, "요청 파라미터가 누락되었습니다: " + e.getParameterName());
    }

    // 기본적인 500 서버 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleGeneralException(Exception e) {
        logger.error("예상치 못한 서버 오류 발생", e);
        return ApiResponseTemplate.error(ErrorCode.INTERNAL_SERVER_ERROR, "예상치 못한 서버 오류가 발생했습니다: " + e.getMessage());
    }

    //valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleValidationException(MethodArgumentNotValidException e) {
        logger.error("유효성 검사 실패", e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("유효성 검사 실패");
        return ApiResponseTemplate.error(ErrorCode.BAD_REQUEST, errorMessage);
    }

    // 400 Bad Request - 타입 불일치 (예: @PathVariable Long id에 abc가 들어온 경우)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("요청 파라미터 타입 불일치", e);
        String message = String.format("파라미터 '%s'의 값 '%s'는(은) 올바른 형식이 아닙니다.", e.getName(), e.getValue());
        return ApiResponseTemplate.error(ErrorCode.BAD_REQUEST, message);
    }

    // 데이터 무결성 위반 시
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("데이터 무결성 위반", e);
        return ApiResponseTemplate.error(ErrorCode.ALREADY_EXIST_SUBJECT_EXCEPTION, "데이터 무결성 위반: " + e.getRootCause().getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleDataAccessException(DataAccessException e) {
        logger.error("데이터베이스 오류 발생", e);
        return ApiResponseTemplate.error(ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 데이터 처리 오류가 발생했습니다.");
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleBusinessException(BusinessException e) {
        logger.warn("비즈니스 예외 발생: {}", e.getMessage());
        return ApiResponseTemplate.error(e.getErrorCode(), e.getDetail() != null ? e.getDetail() : null);
    }

    // HttpStatus에 맞는 ErrorCode 매핑
    private ErrorCode mapHttpStatusToErrorCode(HttpStatus status) {
        switch (status) {
            case BAD_REQUEST:
                return ErrorCode.BAD_REQUEST;  // 400 Bad Request
            case UNAUTHORIZED:
                return ErrorCode.UNAUTHORIZED_EXCEPTION;  // 401 Unauthorized
            case FORBIDDEN:
                return ErrorCode.ACCESS_DENIED_EXCEPTION;  // 403 Forbidden
            case NOT_FOUND:
                return ErrorCode.RESOURCE_NOT_FOUND;  // 404 Not Found
            case CONFLICT:
                return ErrorCode.ALREADY_EXIST_SUBJECT_EXCEPTION;  // 409 Conflict
            case SERVICE_UNAVAILABLE:
                return ErrorCode.SERVICE_UNAVAILABLE;  // 503 Service Unavailable
            default:
                return ErrorCode.INTERNAL_SERVER_ERROR;  // 기타 모든 상태 코드는 내부 서버 오류로 처리
        }
    }
}