package com.ddi.assessment.news.domain.auth.vo;

public record ActiveRefreshToken (
    String userId,
    String tokenHash
) {
    public static ActiveRefreshToken empty() {
        return new ActiveRefreshToken("", "");
    }
}
