package com.ddi.assessment.news.api.security;

public record TokenRotate(
        Long userId,
        String tokenHash
) {}
