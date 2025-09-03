package com.ddi.assessment.news.api.auth.dto;

public record CreateRefreshTokenResponse (String userId, String tokenHash) {}
