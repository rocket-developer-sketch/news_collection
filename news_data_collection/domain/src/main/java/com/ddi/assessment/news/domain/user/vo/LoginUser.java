package com.ddi.assessment.news.domain.user.vo;

public record LoginUser (
    Long id,
    String userId,
    String passwordHash
) {}