package com.ddi.assessment.news.domain.auth.exception;

public class RefreshTokenRotationException extends RuntimeException {
    public RefreshTokenRotationException(String message) {
        super(message);
    }
}
