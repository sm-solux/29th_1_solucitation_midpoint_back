package com.solucitation.midpoint_backend.global.exception;

import com.solucitation.midpoint_backend.domain.member.dto.ValidationErrorResponse;
import com.solucitation.midpoint_backend.domain.member.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                List.of(new ValidationErrorResponse.FieldError("exception", "서버 에러가 발생했습니다. 관리자에게 문의하세요."))
        );
        log.error("서버 에러 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * DTO 검증 실패 예외를 처리합니다.
     *
     * @param e MethodArgumentNotValidException 예외
     * @return 400 Bad Request와 구조화된 검증 오류 메시지를 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ValidationErrorResponse.FieldError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);
        log.error("검증 실패: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * ConstraintViolationException 예외를 처리합니다.
     *
     * @param e ConstraintViolationException 예외
     * @return 400 Bad Request와 구조화된 검증 오류 메시지를 반환
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        List<ValidationErrorResponse.FieldError> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);
        log.error("검증 실패: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 사용자 정의 예외를 처리합니다.
     *
     * @param e 발생한 사용자 정의 예외
     * @return 400 Bad Request와 구조화된 오류 메시지를 반환
     */
    @ExceptionHandler({EmailNotVerifiedException.class, NicknameAlreadyInUseException.class, EmailAlreadyInUseException.class, PasswordMismatchException.class})
    public ResponseEntity<ValidationErrorResponse> handleSignUpExceptions(RuntimeException e) {
        String field;
        if (e instanceof EmailNotVerifiedException) {
            field = "email";
        } else if (e instanceof NicknameAlreadyInUseException) {
            field = "nickname";
        } else if (e instanceof EmailAlreadyInUseException) {
            field = "email";
        } else if (e instanceof PasswordMismatchException) {
            field = "password";
        } else {
            field = "unknown";
        }

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                List.of(new ValidationErrorResponse.FieldError(field, e.getMessage()))
        );
        log.error("회원가입 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * IllegalArgumentException 예외를 처리합니다.
     *
     * @param e IllegalArgumentException 예외
     * @return 400 Bad Request와 구조화된 오류 메시지를 반환
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                List.of(new ValidationErrorResponse.FieldError("IllegalArgumentException", e.getMessage()))
        );
        log.error("잘못된 요청: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
