package com.solucitation.midpoint_backend.domain.member.exception;

public class NicknameAlreadyInUseException extends RuntimeException {
    public NicknameAlreadyInUseException(String message) {
        super(message);
    }
}
