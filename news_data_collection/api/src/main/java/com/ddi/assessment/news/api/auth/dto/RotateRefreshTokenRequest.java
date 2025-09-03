package com.ddi.assessment.news.api.auth.dto;

import java.time.LocalDateTime;

public record RotateRefreshTokenRequest(
        String userId,
        String currentTokenHash,
        String newTokenHash,
        LocalDateTime newTokenIssuedAt,
        LocalDateTime newTokenExpiredAt
) {}
