package com.ddi.assessment.news.api.auth.dto;

import java.time.LocalDateTime;

public record CreateRefreshTokenRequest(
        String userId,
        LocalDateTime issuedAt,
        LocalDateTime expiredAt,
        String tokenHash
) {}
