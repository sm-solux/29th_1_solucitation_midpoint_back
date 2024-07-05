package com.solucitation.midpoint_backend.global.exception;

/**
 * 사용자 정의 예외 클래스 - 애플리케이션 전반에서 사용
 */
public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }
}
