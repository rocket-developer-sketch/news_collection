package com.ddi.assessment.news.domain.auth.vo;

import java.time.LocalDateTime;

public record ActiveRefreshTokenCommand (
        String userId,
        LocalDateTime issuedAt,
        LocalDateTime expiredAt,
        String tokenHash
) {}
