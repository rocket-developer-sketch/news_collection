package com.ddi.assessment.news.api.security;

public record TokenPair(
        String access,
        String rawRefresh,
        long accessExpEpoc,
        long refreshExpEpoc
) {}