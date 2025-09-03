package com.ddi.assessment.news.api.auth.dto;

public record RegisterUserRequest (
        String userId,
        String email,
        String rawPassword
) {}
