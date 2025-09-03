package com.ddi.assessment.news.api.auth.dto;

public record LoginUserRequest(
        String userId,
        String rawPassword
) {}
