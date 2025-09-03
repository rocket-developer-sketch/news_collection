package com.ddi.assessment.news.domain.auth.vo;

public record RevokeRefreshTokenCommand(
        String userId,
        String currentHashToken
) {
}
