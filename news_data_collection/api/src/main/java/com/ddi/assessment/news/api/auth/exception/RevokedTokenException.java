package com.ddi.assessment.news.api.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class RevokedTokenException extends AuthenticationException {
    public RevokedTokenException(String msg) {
        super(msg);
    }
}
