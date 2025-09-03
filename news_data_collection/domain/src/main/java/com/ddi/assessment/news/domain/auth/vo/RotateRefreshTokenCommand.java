package com.ddi.assessment.news.domain.auth.vo;

import java.time.LocalDateTime;

public record RotateRefreshTokenCommand(
        String userId,
        String currentTokenHash,
        String newTokenHash,
        LocalDateTime newTokenIssuedAt,
        LocalDateTime newTokenExpiredAt
) {
}
