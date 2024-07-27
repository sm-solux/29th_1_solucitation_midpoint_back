package com.solucitation.midpoint_backend.domain.member.exception;

public class LoginIdAlreadyInUseException extends RuntimeException {
    public LoginIdAlreadyInUseException(String message) {
        super(message);
    }
}
