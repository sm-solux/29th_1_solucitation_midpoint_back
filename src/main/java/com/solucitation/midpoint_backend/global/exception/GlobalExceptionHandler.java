package com.solucitation.midpoint_backend.global.exception;

import com.solucitation.midpoint_backend.domain.member.dto.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 모든 예외를 처리합니다.
     *
     * @param e 발생한 예외
     * @return 500 Internal Server Error와 구조화된 오류 메시지를 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleAllExceptions(Exception e) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                Collections.singletonList(new ValidationErrorResponse.FieldError("exception", "서버 에러가 발생했습니다. 관리자에게 문의하세요."))
        );
        log.error("서버 에러 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}