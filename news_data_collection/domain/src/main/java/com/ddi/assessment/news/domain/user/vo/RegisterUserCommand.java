package com.ddi.assessment.news.domain.user.vo;


public record RegisterUserCommand (
    String userId,
    String email,
    String passwordHash
) {}
