package com.ddi.assessment.news.api.auth.dto;

public record RevokeRefreshTokenRequest (
        String userId,
        String currentTokenHash
) {}
