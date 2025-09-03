package com.ddi.assessment.news.api.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserDetail {
    private final Long userId;
    private final String loginId;
}
